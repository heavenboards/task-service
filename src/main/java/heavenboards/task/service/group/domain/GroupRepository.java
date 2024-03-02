package heavenboards.task.service.group.domain;

import org.springframework.data.jpa.repository.JpaRepository;

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
}
