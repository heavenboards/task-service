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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
     * Вес позиции группы задач на доске.
     * Нужен для определения порядка отображения групп на доске на UI.
     */
    private Integer positionWeight;

    /**
     * Задачи, которые находятся в этой группе.
     */
    @Builder.Default
    @OneToMany(mappedBy = "group")
    private List<TaskEntity> tasks = new ArrayList<>();

    /**
     * Сравнение двух объектов через id.
     *
     * @param another - объект для сравнения
     * @return равны ли объекты
     */
    @Override
    public boolean equals(final Object another) {
        if (this == another) {
            return true;
        }

        if (another == null || getClass() != another.getClass()) {
            return false;
        }

        GroupEntity that = (GroupEntity) another;
        return Objects.equals(id, that.id);
    }

    /**
     * Хеш код идентификатора.
     *
     * @return хеш код идентификатора
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Строковое отображение объекта.
     * @return строковое отображение объекта
     */
    @Override
    public String toString() {
        return "GroupEntity{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", boardId=" + boardId
            + ", positionWeight=" + positionWeight
            + '}';
    }
}
