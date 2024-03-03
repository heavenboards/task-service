package heavenboards.task.service.task.domain;

import heavenboards.task.service.group.domain.GroupEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
 * Сущность задачи.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "task_entity")
public class TaskEntity {
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
     * Описание.
     */
    private String description;

    /**
     * Группа, в которой лежит задача.
     */
    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    private GroupEntity group;

    /**
     * Вес позиции задачи в группе.
     * Нужен для определения порядка отображения задач в группе на UI.
     */
    private Integer positionWeight;

    /**
     * Номер задачи в группе.
     */
    private Integer number;

    /**
     * Участники задачи и их роли.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<TaskParticipantEntity> participants = new ArrayList<>();

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

        TaskEntity that = (TaskEntity) another;
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
     *
     * @return строковое отображение объекта
     */
    @Override
    public String toString() {
        return "TaskEntity{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", description='" + description + '\''
            + ", positionWeight=" + positionWeight
            + ", number=" + number
            + '}';
    }
}
