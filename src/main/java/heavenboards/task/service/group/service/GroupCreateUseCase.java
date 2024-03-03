package heavenboards.task.service.group.service;

import heavenboards.task.service.group.domain.GroupEntity;
import heavenboards.task.service.group.domain.GroupRepository;
import heavenboards.task.service.group.mapping.GroupMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transfer.contract.api.BoardApi;
import transfer.contract.domain.board.BoardTo;
import transfer.contract.domain.common.OperationStatus;
import transfer.contract.domain.group.GroupOperationErrorCode;
import transfer.contract.domain.group.GroupOperationResultTo;
import transfer.contract.domain.group.GroupTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case создания группы задач.
 */
@Service
@RequiredArgsConstructor
public class GroupCreateUseCase {
    /**
     * Репозиторий для групп задач.
     */
    private final GroupRepository groupRepository;

    /**
     * Api-клиент для сервиса досок.
     */
    private final BoardApi boardApi;

    /**
     * Маппер для групп задач.
     */
    private final GroupMapper groupMapper;

    /**
     * Создать группу задач.
     *
     * @param group - to-модель группы задач
     * @return результат операции создания группы задач
     */
    @Transactional
    public GroupOperationResultTo createGroup(final GroupTo group) {
        Optional<GroupEntity> groupByNameAndBoardId = groupRepository
            .findByNameAndBoardId(group.getName(), group.getBoard().getId());
        if (groupByNameAndBoardId.isPresent()) {
            return GroupOperationResultTo.builder()
                .status(OperationStatus.FAILED)
                .errors(List.of(GroupOperationResultTo.GroupOperationErrorTo.builder()
                    .failedGroupId(groupByNameAndBoardId.get().getId())
                    .errorCode(GroupOperationErrorCode.GROUP_NAME_ALREADY_EXIST_IN_BOARD)
                    .build()))
                .build();
        }

        checkBoardExist(group.getBoard().getId());
        GroupEntity groupEntity = groupRepository.save(groupMapper.mapFromTo(group));
        return GroupOperationResultTo.builder()
            .groupId(groupEntity.getId())
            .build();
    }

    /**
     * Проверка существования доски по идентификатору.
     *
     * @param boardId - идентификатор проверяемой доски
     */
    private void checkBoardExist(final UUID boardId) {
        try {
            BoardTo board = boardApi.findBoardById(boardId);
            if (!Objects.equals(board.getId(), boardId)) {
                throw new RuntimeException();
            }
        } catch (Exception ignored) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Доска с идентификатором %s не найдена", boardId));
        }
    }
}
