package heavenboards.task.service.group.controller;

import heavenboards.task.service.group.service.GroupCreateUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import transfer.contract.domain.group.GroupOperationResultTo;
import transfer.contract.domain.group.GroupTo;

/**
 * Контроллер для взаимодействия с группами задач.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "GroupController", description = "Контроллер для взаимодействия с группами задач")
public class GroupController {
    /**
     * Use case создания группы задач.
     */
    private final GroupCreateUseCase groupCreateUseCase;

    /**
     * Создать группу задач.
     *
     * @param group - to-модель группы задач
     * @return результат операции создания группы задач
     */
    @PostMapping
    @Operation(summary = "Создание группы задач")
    public GroupOperationResultTo createGroup(final @Valid @RequestBody GroupTo group) {
        return groupCreateUseCase.createGroup(group);
    }
}
