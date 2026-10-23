package sg.edu.ntu.taskflowapi.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public List<Task> findAllTasks() {
        return service.findAllTasks();
    }

    @GetMapping("/{id}")
    public Task findTaskById(@PathVariable("id") Long id) {
        return service.findTaskById(id);
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task created = service.createTask(task);
        URI location = URI.create("/api/tasks/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public Task updateTask(
            @PathVariable("id") Long id, @RequestBody Task task) {
        return service.updateTask(id, task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(
            @PathVariable("id") Long id) {
        service.deleteTask(id);
        return ResponseEntity.ok(Map.of(
                "message", "Task " + id + " deleted"));
    }

    @PutMapping("/{id}/complete")
    public Task markTaskAsComplete(@PathVariable("id") Long id) {
        return service.markTaskAsComplete(id);
    }
}
