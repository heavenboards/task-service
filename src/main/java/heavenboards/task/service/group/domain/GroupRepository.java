package heavenboards.task.service.group.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для групп задач.
 */
public interface GroupRepository extends JpaRepository<GroupEntity, UUID> {
    /**
     * Найти группу задач по названию и идентификатору доски.
     *
     * @param name    - название группы задач
     * @param boardId - идентификатор доски
     * @return группа задач или пустота
     */
    Optional<GroupEntity> findByNameAndBoardId(String name, UUID boardId);

    /**
     * Найти идентификаторы всех групп по идентификатору доски.
     *
     * @param boardId - идентификатор доски, на которой ищем группы
     * @return идентификаторы всех групп на доске
     */
    @Query(value = "SELECT id FROM group_entity WHERE board_id = :boardId", nativeQuery = true)
    List<UUID> findGroupIdsByBoardId(UUID boardId);
}
