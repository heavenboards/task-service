package heavenboards.task.service.task.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для взаимодействия с задачами.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/task")
@Tag(name = "TaskController", description = "Контроллер для взаимодействия с задачами")
public class TaskController {
}
