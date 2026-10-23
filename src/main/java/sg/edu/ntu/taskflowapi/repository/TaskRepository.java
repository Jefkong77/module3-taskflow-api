package sg.edu.ntu.taskflowapi.repository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import sg.edu.ntu.taskflowapi.model.Task;

@Repository
public class TaskRepository {
    private final Map<Long, Task> tasks = new HashMap<>();
    private long nextId = 1L;

    public synchronized List<Task> findAll() {
        return tasks.values().stream()
                .sorted(Comparator.comparing(Task::getId))
                .map(this::copy)
                .toList();
    }

    public synchronized Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id)).map(this::copy);
    }

    public synchronized Task create(Task task) {
        Task saved = new Task(nextId++, task.getTitle(), task.isCompleted());
        tasks.put(saved.getId(), saved);
        return copy(saved);
    }

    public synchronized Optional<Task> update(Long id, Task task) {
        if (!tasks.containsKey(id)) {
            return Optional.empty();
        }
        Task saved = new Task(id, task.getTitle(), task.isCompleted());
        tasks.put(id, saved);
        return Optional.of(copy(saved));
    }

    public synchronized boolean delete(Long id) {
        return tasks.remove(id) != null;
    }

    private Task copy(Task task) {
        return new Task(task.getId(), task.getTitle(), task.isCompleted());
    }
}
