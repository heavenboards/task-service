package heavenboards.task.service.group.mapping;

import heavenboards.task.service.group.domain.GroupEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import transfer.contract.domain.group.GroupTo;

/**
 * Маппер для групп задач.
 */
@Mapper(componentModel = "spring")
@RequiredArgsConstructor
public abstract class GroupMapper {
    /**
     * Маппинг из to в entity.
     *
     * @param to - to-модель группы задач
     * @return entity с проставленными полями
     */
    @Mapping(target = "boardId", source = "board.id")
    @Mapping(target = "tasks", ignore = true)
    public abstract GroupEntity mapFromTo(GroupTo to);
}
