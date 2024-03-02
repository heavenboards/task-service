package heavenboards.task.service.group.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для взаимодействия с группами задач.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
@Tag(name = "GroupController", description = "Контроллер для взаимодействия с группами задач")
public class GroupController {

}
