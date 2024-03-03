package heavenboards.task.service.task.controller;

import heavenboards.task.service.task.service.TaskCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.task.TaskOperationResultTo;
import transfer.contract.domain.task.TaskTo;

/**
 * Контроллер для взаимодействия с задачами.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@Tag(name = "TaskController", description = "Контроллер для взаимодействия с задачами")
public class TaskController {
    /**
     * Use case создания задачи.
     */
    private final TaskCreateUseCase taskCreateUseCase;

    /**
     * Создать задачу.
     *
     * @param task - to-модель задачи
     * @return результат операции создания задачи
     */
    @PostMapping
    @Operation(summary = "Создание задачи")
    public TaskOperationResultTo createTask(final @Valid @RequestBody TaskTo task) {
        return taskCreateUseCase.createTask(task);
    }
}
