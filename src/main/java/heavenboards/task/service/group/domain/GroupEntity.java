package heavenboards.task.service.group.domain;

import heavenboards.task.service.task.domain.TaskEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

/**
 * Сущность группы задач.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "group_entity")
public class GroupEntity {
    /**
     * Идентификатор.
     */
    @Id
    @UuidGenerator
    private UUID id;

    /**
     * Название.
     */
    private String name;

    /**
     * Идентификатор доски, в которой находится группа задач.
     */
    private UUID boardId;

    /**
     * Порядковый номер группы задач на доске.
     */
    private Integer ordinalNumber;

    /**
     * Задачи, которые находятся в этой группе.
     */
    @OneToMany(mappedBy = "group")
    private List<TaskEntity> tasks;
}
