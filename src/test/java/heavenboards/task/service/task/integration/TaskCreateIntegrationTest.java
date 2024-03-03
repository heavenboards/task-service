package heavenboards.task.service.task.integration;


import heavenboards.task.service.group.domain.GroupEntity;
import heavenboards.task.service.group.domain.GroupRepository;
import heavenboards.task.service.task.domain.TaskEntity;
import heavenboards.task.service.task.domain.TaskRepository;
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
import transfer.contract.api.UserApi;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.group.GroupTo;
import transfer.contract.domain.task.TaskOperationResultTo;
import transfer.contract.domain.task.TaskTo;

import java.util.Optional;
import java.util.UUID;

/**
 * Тест создания группы задач.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RequiredArgsConstructor
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(
    scripts = "classpath:sql/group/create.sql",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
@Sql(
    scripts = "classpath:sql/clear-all.sql",
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
    config = @SqlConfig(encoding = "UTF-8")
)
public class TaskCreateIntegrationTest {
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
     * Репозиторий для групп задач.
     */
    @Autowired
    private GroupRepository groupRepository;

    /**
     * Репозиторий для задач.
     */
    @Autowired
    private TaskRepository taskRepository;

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
     * Тест валидного создания задачи.
     */
    @Test
    @DisplayName("Тест валидного создания задачи")
    public void validTaskCreateTest() {
        securityTestUtil.securityContextHelper();

        Mockito.when(userApi.findUserByUsername(Mockito.any()))
            .thenReturn(securityTestUtil.getAuthenticatedUser());

        Optional<GroupEntity> groupEntity = groupRepository.findById(UUID
            .fromString("f91b820e-7721-43ad-b188-1cbd6c0b9d42"));
        Assertions.assertTrue(groupEntity.isPresent());

        GroupTo groupTo = GroupTo.builder()
            .id(groupEntity.get().getId())
            .name(groupEntity.get().getName())
            .build();

        Response response = createTaskAndGetResponse(groupTo);
        TaskOperationResultTo operationResult = response.getBody()
            .as(TaskOperationResultTo.class);

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatusCode());
        Assertions.assertEquals(OperationStatus.OK, operationResult.getStatus());
        Assertions.assertNotNull(operationResult.getTaskId());

        Optional<TaskEntity> taskEntity = taskRepository.findById(operationResult.getTaskId());
        Assertions.assertTrue(taskEntity.isPresent());
        Assertions.assertEquals(1, taskEntity.get().getNumber());
        Assertions.assertEquals(groupEntity.get().getId(), taskEntity.get().getGroup().getId());
        Assertions.assertEquals(2, taskEntity.get().getParticipants().size());

        Optional<GroupEntity> updatedGroupEntity = groupRepository.findById(UUID
            .fromString("f91b820e-7721-43ad-b188-1cbd6c0b9d42"));
        Assertions.assertTrue(updatedGroupEntity.isPresent());
        Assertions.assertEquals(1, updatedGroupEntity.get().getTasks().size());
        Assertions.assertEquals(taskEntity.get().getId(),
            updatedGroupEntity.get().getTasks().get(0).getId());
    }

    /**
     * Оправить запрос на создание задачи и получить ответ.
     *
     * @param group - группа задач
     * @return ответ
     */
    private Response createTaskAndGetResponse(GroupTo group) {
        return RestAssured
            .given()
            .contentType("application/json")
            .header(new Header(HttpHeaders.AUTHORIZATION, securityTestUtil.authHeader()))
            .body(TaskTo.builder()
                .name("Task name")
                .positionWeight(1000)
                .group(group)
                .build())
            .when()
            .post("/task");
    }
}
