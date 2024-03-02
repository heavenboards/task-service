package heavenboards.task.service.group.integration;

import feign.FeignException;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import security.service.util.test.SecurityTestUtil;
import transfer.contract.api.BoardApi;
import transfer.contract.api.UserApi;
import transfer.contract.domain.board.BoardTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.group.GroupOperationErrorCode;
import transfer.contract.domain.group.GroupOperationResultTo;
import transfer.contract.domain.group.GroupTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.UUID;

/**
 * Тест создания группы задач.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class GroupCreateIntegrationTest {
    /**
     * Utility-класс с настройкой security для тестов.
     */
    @Autowired
    private SecurityTestUtil securityTestUtil;

    /**
     * Mock api-клиента для сервиса пользователей.
     */
    @MockBean
    private UserApi userApi;

    /**
     * Mock api-клиента для сервиса досок.
     */
    @MockBean
    private BoardApi boardApi;

    /**
     * Порт приложения.
     */
    @LocalServerPort
    private int port;

    /**
     * Конфигурация перед тестами.
     */
    @BeforeAll
    public void init() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost:" + port + "/api/v1";
        RestAssured.defaultParser = Parser.JSON;
    }

    /**
     * Тест валидного создания группы задач.
     */
    @Test
    @DisplayName("Тест валидного создания группы задач")
    public void validGroupCreateTest() {
        securityTestUtil.securityContextHelper();

        BoardTo board = BoardTo.builder()
            .id(UUID.randomUUID())
            .name("Existing board")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(boardApi.findBoardById(Mockito.any()))
            .thenReturn(board);

        Response response = createBoardAndGetResponse(board);
        GroupOperationResultTo operationResult = response
            .getBody()
            .as(GroupOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());
        Assertions.assertNotNull(operationResult.getGroupId());
    }

    /**
     * Тест создания группы задач с несуществующей доской.
     */
    @Test
    @DisplayName("Тест создания группы задач с несуществующей доской")
    public void boardNotExistCreateTest() {
        securityTestUtil.securityContextHelper();

        BoardTo board = BoardTo.builder()
            .id(UUID.randomUUID())
            .name("Not existing board")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(boardApi.findBoardById(Mockito.any()))
            .thenThrow(FeignException.FeignServerException.class);

        Response response = createBoardAndGetResponse(board);
        ClientApplicationException applicationException = response
            .getBody()
            .as(ClientApplicationException.class);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
        Assertions.assertEquals(BaseErrorCode.NOT_FOUND, applicationException.getErrorCode());
    }

    /**
     * Тест создания группы с уже существующим названием на доске.
     */
    @Test
    @DisplayName("Тест создания группы с уже существующим названием на доске")
    public void existingGroupNameCreateTest() {
        securityTestUtil.securityContextHelper();

        BoardTo board = BoardTo.builder()
            .id(UUID.randomUUID())
            .name("Not existing board")
            .build();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());
        Mockito.when(boardApi.findBoardById(Mockito.any()))
            .thenReturn(board);

        Response successResponse = createBoardAndGetResponse(board);
        GroupOperationResultTo successOperationResult = successResponse
            .getBody()
            .as(GroupOperationResultTo.class);

        Response errorResponse = createBoardAndGetResponse(board);
        GroupOperationResultTo errorOperationResult = errorResponse
            .getBody()
            .as(GroupOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), errorResponse.getStatusCode());
        Assertions.assertEquals(OperationStatus.FAILED, errorOperationResult.getStatus());
        Assertions.assertEquals(List.of(GroupOperationResultTo.GroupOperationErrorTo.builder()
            .errorCode(GroupOperationErrorCode.GROUP_NAME_ALREADY_EXIST_IN_BOARD)
            .failedGroupId(successOperationResult.getGroupId())
            .build()), errorOperationResult.getErrors());
    }

    /**
     * Оправить запрос на создание группы задач и получить ответ.
     *
     * @param board - доска
     * @return ответ
     */
    private Response createBoardAndGetResponse(BoardTo board) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(GroupTo.builder()
                .name("Group name")
                .positionWeight(1000)
                .board(board)
                .build())
            .when()
            .post("/group");
    }
}
