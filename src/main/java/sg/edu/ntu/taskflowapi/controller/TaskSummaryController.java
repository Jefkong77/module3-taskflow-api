package sg.edu.ntu.taskflowapi.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.ntu.taskflowapi.service.TaskSummaryService;

@RestController
@RequestMapping("/api/tasks")
public class TaskSummaryController {
    private final TaskSummaryService service;

    public TaskSummaryController(TaskSummaryService service) {
        this.service = service;
    }

    @GetMapping(value = "/summary", produces = MediaType.TEXT_PLAIN_VALUE)
    public String summarizeTasks() {
        return service.summarizeTasks();
    }
}
