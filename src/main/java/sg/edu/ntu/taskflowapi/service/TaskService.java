package sg.edu.ntu.taskflowapi.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.repository.TaskRepository;

@Service
public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAllTasks() {
        return repository.findAll();
    }

    public Task findTaskById(Long id) {
        validateId(id);
        return repository.findById(id).orElseThrow(() -> notFound(id));
    }

    public Task createTask(Task task) {
        String title = validateTitle(task);
        return repository.create(new Task(null, title, task.isCompleted()));
    }

    public Task updateTask(Long id, Task task) {
        validateId(id);
        String title = validateTitle(task);
        Task replacement = new Task(id, title, task.isCompleted());
        return repository.update(id, replacement)
                .orElseThrow(() -> notFound(id));
    }

    public void deleteTask(Long id) {
        validateId(id);
        if (!repository.delete(id)) {
            throw notFound(id);
        }
    }

    public Task markTaskAsComplete(Long id) {
        Task task = findTaskById(id);
        task.setCompleted(true);
        return repository.update(id, task).orElseThrow(() -> notFound(id));
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Task ID must be a positive integer");
        }
    }

    private String validateTitle(Task task) {
        if (task == null || task.getTitle() == null
                || task.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Task title must not be blank");
        }
        String title = task.getTitle().trim();
        if (title.length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Task title must be at most 200 characters");
        }
        return title;
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Task " + id + " was not found");
    }
}
