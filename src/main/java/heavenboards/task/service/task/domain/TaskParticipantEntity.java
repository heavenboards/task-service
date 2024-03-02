package heavenboards.task.service.task.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.UuidGenerator;
import transfer.contract.domain.task.TaskRole;

import java.util.Objects;
import java.util.UUID;

/**
 * Сущность участника.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Accessors(chain = true)
@Entity
@Table(name = "task_participant_entity")
public class TaskParticipantEntity {
    /**
     * Идентификатор.
     */
    @Id
    @UuidGenerator
    private UUID id;

    /**
     * Идентификатор пользователя.
     */
    private UUID userId;

    /**
     * Задача.
     */
    @ManyToOne
    @JoinColumn(name = "task_id", referencedColumnName = "id")
    private TaskEntity task;

    /**
     * Роль пользователя в задаче.
     */
    @Enumerated(EnumType.STRING)
    private TaskRole role;

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

        TaskParticipantEntity that = (TaskParticipantEntity) another;
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
     * Переопределение toString() для избежания циклических отображений.
     *
     * @return строковое представление проекта
     */
    @Override
    public String toString() {
        return "TaskParticipantEntity{"
            + "id=" + id
            + ", userId=" + userId
            + ", role=" + role
            + '}';
    }
}
