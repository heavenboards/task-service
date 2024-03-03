package heavenboards.task.service.task.service;

import heavenboards.task.service.group.domain.GroupRepository;
import heavenboards.task.service.task.domain.TaskEntity;
import heavenboards.task.service.task.domain.TaskRepository;
import heavenboards.task.service.task.mapping.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import transfer.contract.domain.task.TaskOperationResultTo;
import transfer.contract.domain.task.TaskTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.UUID;

/**
 * Use case создания задачи.
 */
@Service
@RequiredArgsConstructor
public class TaskCreateUseCase {
    /**
     * Репозиторий для групп задач.
     */
    private final GroupRepository groupRepository;

    /**
     * Маппер для задач.
     */
    private final TaskMapper taskMapper;

    /**
     * Репозиторий для задач.
     */
    private final TaskRepository taskRepository;

    /**
     * Создать задачу.
     *
     * @param task - to-модель задачи
     * @return результат операции создания задачи
     */
    @Transactional
    public TaskOperationResultTo createTask(final TaskTo task) {
        checkGroupExist(task.getGroup().getId());
        TaskEntity entity = taskRepository.save(taskMapper.mapFromTo(new TaskEntity(), task));
        return TaskOperationResultTo.builder()
            .taskId(entity.getId())
            .build();
    }

    /**
     * Проверка существования группы задач по идентификатору.
     *
     * @param groupId - идентификатор группы задач
     */
    private void checkGroupExist(final UUID groupId) {
        try {
            if (!groupRepository.existsById(groupId)) {
                throw new RuntimeException();
            }
        } catch (Exception ignored) {
            throw new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Группа задач с идентификатором %s не найдена", groupId));
        }
    }
}
