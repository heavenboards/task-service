package heavenboards.task.service.task.mapping;

import heavenboards.task.service.group.domain.GroupEntity;
import heavenboards.task.service.group.domain.GroupRepository;
import heavenboards.task.service.task.domain.TaskEntity;
import heavenboards.task.service.task.domain.TaskParticipantEntity;
import heavenboards.task.service.task.domain.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import transfer.contract.domain.task.TaskRole;
import transfer.contract.domain.task.TaskTo;
import transfer.contract.domain.user.UserTo;
import transfer.contract.exception.BaseErrorCode;
import transfer.contract.exception.ClientApplicationException;

import java.util.List;
import java.util.UUID;

/**
 * Маппер для задач.
 */
@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class TaskMapper {
    /**
     * Репозиторий для групп задач.
     */
    private GroupRepository groupRepository;

    /**
     * Репозиторий для задач.
     */
    private TaskRepository taskRepository;

    /**
     * Маппинг из to в entity.
     *
     * @param entity - сущность, которой проставляем поля
     * @param to     - to-модель задачи
     * @return сущность с проставленными полями
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "participants", ignore = true)
    public abstract TaskEntity mapFromTo(@MappingTarget TaskEntity entity, TaskTo to);

    /**
     * После маппинга из to в entity - проставляем оставшиеся поля.
     *
     * @param entity - сущность которой проставляем поя
     * @param to     - to-модель проекта
     */
    @AfterMapping
    @SuppressWarnings("unused")
    protected void afterMappingFromTo(final @MappingTarget TaskEntity entity,
                                      final TaskTo to) {
        var user = (UserTo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UUID groupId = to.getGroup().getId();
        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new ClientApplicationException(BaseErrorCode.NOT_FOUND,
                String.format("Группа задач с идентификатором %s не найдена", groupId)));

        entity.setGroup(group);
        group.getTasks().add(entity);

        entity.setParticipants(List.of(TaskParticipantEntity.builder()
            .userId(user.getId())
            .task(entity)
            .role(TaskRole.AUTHOR)
            .build(), TaskParticipantEntity.builder()
            .userId(user.getId())
            .task(entity)
            .role(TaskRole.ASSIGNEE)
            .build()));

        List<UUID> groupIds = groupRepository.findGroupIdsByBoardId(group.getBoardId());
        int maxNumber = groupIds.stream()
            .mapToInt(taskRepository::findMaxTaskNumberInGroup)
            .max().orElse(1);
        entity.setNumber(maxNumber + 1);
    }

    /**
     * Внедрение бина репозитория для групп задач.
     *
     * @param repository - бин GroupRepository
     */
    @Autowired
    public void setGroupRepository(final GroupRepository repository) {
        this.groupRepository = repository;
    }

    /**
     * Внедрение бина репозитория для задач.
     *
     * @param repository - бин TaskRepository
     */
    @Autowired
    public void setTaskRepository(final TaskRepository repository) {
        this.taskRepository = repository;
    }
}
