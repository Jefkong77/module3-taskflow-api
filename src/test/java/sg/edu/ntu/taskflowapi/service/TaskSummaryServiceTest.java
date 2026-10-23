package sg.edu.ntu.taskflowapi.service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.server.ResponseStatusException;
import sg.edu.ntu.taskflowapi.model.Task;
import sg.edu.ntu.taskflowapi.repository.TaskRepository;

import static org.junit.jupiter.api.Assertions.*;

class TaskSummaryServiceTest {
    private final TaskService tasks = new TaskService(new TaskRepository());

    private TaskSummaryService service(ChatModel model) {
        return new TaskSummaryService(tasks, ChatClient.builder(model));
    }

    @Test
    void emptyListNeedsNoProviderCall() {
        TaskSummaryService summary = service(prompt -> {
            fail("An empty list should not call the model");
            return null;
        });
        assertEquals("There are no tasks. Nothing is pending or completed.",
                summary.summarizeTasks());
    }

    @Test
    void sendsStoredTasksAndAccurateCountsAsLiteralData() {
        tasks.createTask(new Task(null, "Review {source}\ncarefully", false));
        tasks.createTask(new Task(null, "Write tests", true));
        AtomicReference<Prompt> sent = new AtomicReference<>();
        TaskSummaryService summary = service(prompt -> {
            sent.set(prompt);
            return new ChatResponse(List.of(new Generation(
                    new AssistantMessage(" One task is done and one is pending. "))));
        });
        assertEquals("One task is done and one is pending.", summary.summarizeTasks());
        String userData = sent.get().getInstructions().getLast().getText();
        assertTrue(userData.contains("Completed: 1\nPending: 1"));
        assertTrue(userData.contains("1 | PENDING | Review {source} carefully"));
        assertTrue(userData.contains("2 | DONE | Write tests"));
        assertFalse(tasks.findTaskById(1L).isCompleted());
    }

    @Test
    void rejectsListsAboveTheDocumentedLimitBeforeCallingProvider() {
        for (int i = 0; i < 51; i++) {
            tasks.createTask(new Task(null, "Task " + i, false));
        }
        TaskSummaryService summary = service(prompt -> {
            fail("Oversized input should not call the model");
            return null;
        });
        assertEquals(413, assertThrows(ResponseStatusException.class,
                summary::summarizeTasks).getStatusCode().value());
    }

    @Test
    void providerFailureReturns502WithoutLeakingProviderDetails() {
        tasks.createTask(new Task(null, "Research", false));
        TaskSummaryService summary = service(prompt -> {
            throw new IllegalStateException("Sensitive provider diagnostic");
        });
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                summary::summarizeTasks);
        assertEquals(502, error.getStatusCode().value());
        assertFalse(error.getReason().contains("Sensitive provider diagnostic"));
    }

    @Test
    void emptyProviderResponseIsAnErrorRatherThanAPretendSummary() {
        tasks.createTask(new Task(null, "Research", false));
        TaskSummaryService summary = service(prompt -> new ChatResponse(
                List.of(new Generation(new AssistantMessage("   ")))));
        assertEquals(502, assertThrows(ResponseStatusException.class,
                summary::summarizeTasks).getStatusCode().value());
    }
}
