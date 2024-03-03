package heavenboards.task.service.task.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * Репозиторий для задач.
 */
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    /**
     * Найти максимальный номер задачи в группе задач.
     *
     * @param groupId - идентификатор группы задач
     * @return максимальный номер задачи в группе задач
     */
    @Query(value = "SELECT COALESCE(MAX(number), 0) FROM task_entity WHERE group_id = :groupId",
        nativeQuery = true)
    int findMaxTaskNumberInGroup(UUID groupId);
}
