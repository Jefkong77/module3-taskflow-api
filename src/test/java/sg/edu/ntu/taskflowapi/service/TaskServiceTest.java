package sg.edu.ntu.taskflowapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {
    private TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService(new TaskRepository());
    }

    @Test
    void startsEmpty() {
        assertTrue(service.findAllTasks().isEmpty());
    }

    @Test
    void generatesUniqueIdsAndTrimsTitles() {
        Task first = service.createTask(new Task(900L, " First ", false));
        Task second = service.createTask(new Task(null, "Second", false));
        assertEquals(1L, first.getId().longValue());
        assertEquals(2L, second.getId().longValue());
        assertEquals("First", first.getTitle());
    }

    @Test
    void replacesExistingTaskWithoutChangingItsId() {
        Task created = service.createTask(new Task(null, "Original", false));
        Task updated = service.updateTask(
                created.getId(), new Task(999L, "Updated", true));
        assertEquals(created.getId(), updated.getId());
        assertEquals("Updated", updated.getTitle());
        assertTrue(updated.isCompleted());
    }

    @Test
    void completionIsIdempotent() {
        Task task = service.createTask(new Task(null, "Research", false));
        service.markTaskAsComplete(task.getId());
        Task repeated = service.markTaskAsComplete(task.getId());
        assertTrue(repeated.isCompleted());
        assertTrue(service.findTaskById(task.getId()).isCompleted());
    }

    @Test
    void deletingRemovesTheTask() {
        Task task = service.createTask(new Task(null, "Remove me", false));
        service.deleteTask(task.getId());
        ResponseStatusException error = assertThrows(
                ResponseStatusException.class,
                () -> service.findTaskById(task.getId()));
        assertEquals(404, error.getStatusCode().value());
    }

    @Test
    void missingOperationsReturn404WithoutCreatingRecords() {
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.findTaskById(99L)).getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.updateTask(
                        99L, new Task(null, "Missing", false)))
                .getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.deleteTask(99L)).getStatusCode().value());
        assertEquals(404, assertThrows(ResponseStatusException.class,
                () -> service.markTaskAsComplete(99L))
                .getStatusCode().value());
        assertTrue(service.findAllTasks().isEmpty());
    }

    @Test
    void invalidTitlesReturn400() {
        for (String title : new String[]{null, "", "   ", "x".repeat(201)}) {
            ResponseStatusException error = assertThrows(
                    ResponseStatusException.class,
                    () -> service.createTask(new Task(null, title, false)));
            assertEquals(400, error.getStatusCode().value());
        }
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.createTask(null)).getStatusCode().value());
    }

    @Test
    void titleLengthBoundaryIsEnforcedOnCreateAndUpdate() {
        Task task = service.createTask(
                new Task(null, "x".repeat(200), false));
        assertEquals(200, task.getTitle().length());
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.updateTask(
                        task.getId(), new Task(null, "x".repeat(201), false)))
                .getStatusCode().value());
        assertEquals(200, service.findTaskById(task.getId()).getTitle().length());
    }

    @Test
    void invalidIdsReturn400() {
        for (Long id : new Long[]{null, 0L, -1L}) {
            assertEquals(400, assertThrows(ResponseStatusException.class,
                    () -> service.findTaskById(id)).getStatusCode().value());
        }
    }

    @Test
    void returnedObjectsCannotMutateStorageWithoutSaving() {
        Task created = service.createTask(new Task(null, "Original", false));
        created.setTitle("Changed outside repository");
        assertEquals("Original",
                service.findTaskById(created.getId()).getTitle());
        service.findAllTasks().getFirst().setCompleted(true);
        assertFalse(service.findTaskById(created.getId()).isCompleted());
    }
}
