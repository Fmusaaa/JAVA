package com.taskbot.handler;

import com.taskbot.model.Subtask;
import com.taskbot.model.ParsedTaskAction;
import com.taskbot.model.Task;
import com.taskbot.service.TaskService;
import com.taskbot.util.DateTimeUtil;
import com.taskbot.util.MessageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);

    private final TaskService taskService;

    public CommandHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    public void registerUser(long telegramId, String username, String firstName) {
        taskService.registerUser(telegramId, username, firstName);
    }

    public String handleHelp() {
        return "📋 Task Manager Bot Commands\n\n" +
                "Task utama:\n" +
                "/add_task - Tambah task baru\n" +
                "/list_tasks - Lihat semua task\n" +
                "/complete_task <id> - Tandai task selesai\n" +
                "/edit_task - Edit task dengan format lengkap\n" +
                "/edit_task <id> - Edit task tertentu lewat langkah berikutnya\n" +
                "/delete_task <id> - Hapus task\n\n" +
                "Filter dan pencarian:\n" +
                "/today - Task due hari ini\n" +
                "/tomorrow - Task due besok\n" +
                "/overdue - Task yang terlambat\n" +
                "/high_priority - Task prioritas tinggi\n" +
                "/search <keyword> - Cari task\n" +
                "/stats - Statistik produktivitas\n\n" +
                "AI assistant:\n" +
                "/ask <pertanyaan> - Tanya AI untuk bantu belajar atau tugas\n" +
                "Contoh: /ask jelasin polymorphism Java secara singkat\n\n" +
                "Subtask/checklist:\n" +
                "/add_subtask <task_id> <judul subtask>\n" +
                "/list_subtasks <task_id>\n" +
                "/done_subtask <subtask_id>\n" +
                "/delete_subtask <subtask_id>\n\n" +
                "Recurring task:\n" +
                "/repeat <task_id> <daily|weekly|monthly>\n" +
                "Contoh: /repeat 4 weekly\n\n" +
                "Format tanggal: dd/MM/yyyy HH:mm\n" +
                "Contoh: 15/03/2025 14:30";
    }

    public String initAddTask() {
        return "📝 Tambah task baru\n\n" +
                "Format:\n" +
                "<title> | <description> | <due_date> | <priority>\n\n" +
                "Contoh:\n" +
                "Kerjain laporan PBO | Bab database dan class diagram | 15/05/2026 20:00 | 3\n\n" +
                "Priority: 1 = Low, 2 = Medium, 3 = High\n" +
                "Due date dan priority boleh dikosongkan.";
    }

    public String addTask(long telegramId, String input) {
        try {
            String[] parts = input.split("\\|", -1);
            String title = parts.length > 0 ? parts[0].trim() : "";
            if (title.isBlank()) {
                return "Judul task tidak boleh kosong.";
            }

            String description = parts.length > 1 ? parts[1].trim() : "";
            LocalDateTime dueDate = null;
            int priority = 2;

            if (parts.length > 2 && !parts[2].trim().isBlank()) {
                dueDate = DateTimeUtil.parseUserDateTime(parts[2]);
            }
            if (parts.length > 3 && !parts[3].trim().isBlank()) {
                priority = parsePriority(parts[3].trim());
            }

            Task task = taskService.addTask(telegramId, title, description, dueDate, priority);
            return "✅ Task berhasil ditambahkan!\n\n" + MessageFormatter.formatTask(task);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Error in addTask", e);
            return "Terjadi error saat menambah task. Coba lagi ya.";
        }
    }

    public String addParsedTask(long telegramId, ParsedTaskAction parsed) {
        try {
            if (parsed == null || !"add_task".equalsIgnoreCase(parsed.getAction())) {
                return "AI belum menemukan task yang jelas dari pesan itu.";
            }
            if (parsed.getTitle() == null || parsed.getTitle().isBlank()) {
                return "Judul task belum jelas.";
            }

            int priority = parsed.getPriority() == null ? 2 : parsed.getPriority();
            if (priority < 1 || priority > 3) {
                priority = 2;
            }

            Task task = taskService.addTask(
                    telegramId,
                    parsed.getTitle(),
                    parsed.getDescription() == null ? "" : parsed.getDescription(),
                    parsed.getDueDate(),
                    priority
            );
            return "✅ Task berhasil dibuat dari AI!\n\n" + MessageFormatter.formatTask(task);
        } catch (Exception e) {
            logger.error("Error in addParsedTask", e);
            return "AI berhasil membaca pesan, tapi task gagal disimpan. Coba lagi ya.";
        }
    }

    public String listTasks(long telegramId) {
        return MessageFormatter.formatTaskList("📋 Daftar Task", taskService.listTasks(telegramId));
    }

    public String listToday(long telegramId) {
        return MessageFormatter.formatTaskList("📅 Task Hari Ini", taskService.listDueOn(telegramId, LocalDate.now()));
    }

    public String listTomorrow(long telegramId) {
        return MessageFormatter.formatTaskList("📅 Task Besok", taskService.listDueOn(telegramId, LocalDate.now().plusDays(1)));
    }

    public String listOverdue(long telegramId) {
        return MessageFormatter.formatTaskList("⚠️ Task Overdue", taskService.listOverdue(telegramId));
    }

    public String listHighPriority(long telegramId) {
        return MessageFormatter.formatTaskList("🔴 High Priority Tasks", taskService.listHighPriority(telegramId));
    }

    public String search(long telegramId, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "Gunakan format: /search <keyword>";
        }
        return MessageFormatter.formatTaskList("🔎 Hasil Search: " + keyword, taskService.search(telegramId, keyword.trim()));
    }

    public String stats(long telegramId) {
        return MessageFormatter.formatStats(taskService.getStats(telegramId));
    }

    public String initEditTask() {
        return "✏️ Edit Task\n\n" +
                "Format:\n" +
                "<task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>\n\n" +
                "Contoh:\n" +
                "4 | Kerjain laporan PBO revisi | Lengkapi bagian DAO | 15/05/2026 20:00 | 3";
    }

    public String initEditTaskWithId(String taskId) {
        return "✏️ Edit Task #" + taskId + "\n\n" +
                "Kirim detail baru dengan format:\n" +
                "<new_title> | <new_description> | <new_due_date> | <new_priority>\n\n" +
                "Contoh:\n" +
                "Kerjain laporan PBO revisi | Lengkapi bagian DAO | 15/05/2026 20:00 | 3";
    }

    public String editTask(long telegramId, String input) {
        try {
            String[] parts = input.split("\\|", -1);
            if (parts.length < 2) {
                return "Format salah. Gunakan: <task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>";
            }

            int taskId = parseId(parts[0], "Task ID");
            Task existingTask = taskService.getTask(telegramId, taskId);
            if (existingTask == null) {
                return "Task tidak ditemukan.";
            }

            String title = !parts[1].trim().isBlank() ? parts[1].trim() : existingTask.getTitle();
            String description = parts.length > 2 ? parts[2].trim() : existingTask.getDescription();
            LocalDateTime dueDate = existingTask.getDueDate();
            int priority = existingTask.getPriority();

            if (parts.length > 3 && !parts[3].trim().isBlank()) {
                dueDate = DateTimeUtil.parseUserDateTime(parts[3]);
            }
            if (parts.length > 4 && !parts[4].trim().isBlank()) {
                priority = parsePriority(parts[4].trim());
            }

            boolean updated = taskService.updateTask(telegramId, taskId, title, description, dueDate, priority);
            if (!updated) {
                return "Task gagal diupdate.";
            }
            return "✅ Task berhasil diupdate!\n\n" + MessageFormatter.formatTask(taskService.getTask(telegramId, taskId));
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            logger.error("Error in editTask", e);
            return "Terjadi error saat edit task.";
        }
    }

    public String completeTask(long telegramId, String taskIdText) {
        try {
            int taskId = parseId(taskIdText, "Task ID");
            Task task = taskService.completeTask(telegramId, taskId);
            if (task == null) {
                return "Task tidak ditemukan.";
            }
            return "✅ Task selesai!\n\n" + MessageFormatter.formatTask(task);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String deleteTask(long telegramId, String taskIdText) {
        try {
            int taskId = parseId(taskIdText, "Task ID");
            return taskService.deleteTask(telegramId, taskId)
                    ? "🗑️ Task #" + taskId + " berhasil dihapus."
                    : "Task tidak ditemukan.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String addSubtask(long telegramId, String args) {
        try {
            String[] parts = args.trim().split("\\s+", 2);
            if (parts.length < 2 || parts[1].isBlank()) {
                return "Gunakan format: /add_subtask <task_id> <judul subtask>";
            }
            int taskId = parseId(parts[0], "Task ID");
            Subtask subtask = taskService.addSubtask(telegramId, taskId, parts[1].trim());
            if (subtask == null) {
                return "Task tidak ditemukan.";
            }
            return "✅ Subtask ditambahkan: #" + subtask.getId() + " " + subtask.getTitle();
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String listSubtasks(long telegramId, String taskIdText) {
        try {
            int taskId = parseId(taskIdText, "Task ID");
            List<Subtask> subtasks = taskService.listSubtasks(telegramId, taskId);
            return MessageFormatter.formatSubtasks(subtasks);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String doneSubtask(long telegramId, String subtaskIdText) {
        try {
            int subtaskId = parseId(subtaskIdText, "Subtask ID");
            return taskService.markSubtaskDone(telegramId, subtaskId)
                    ? "✅ Subtask #" + subtaskId + " selesai."
                    : "Subtask tidak ditemukan.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String deleteSubtask(long telegramId, String subtaskIdText) {
        try {
            int subtaskId = parseId(subtaskIdText, "Subtask ID");
            return taskService.deleteSubtask(telegramId, subtaskId)
                    ? "🗑️ Subtask #" + subtaskId + " dihapus."
                    : "Subtask tidak ditemukan.";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    public String repeatTask(long telegramId, String args) {
        try {
            String[] parts = args.trim().split("\\s+");
            if (parts.length < 2) {
                return "Gunakan format: /repeat <task_id> <daily|weekly|monthly>";
            }
            int taskId = parseId(parts[0], "Task ID");
            return taskService.repeatTask(telegramId, taskId, parts[1]);
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }

    private int parseId(String value, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + " harus berupa angka.");
        }
    }

    private int parsePriority(String value) {
        try {
            int priority = Integer.parseInt(value);
            if (priority < 1 || priority > 3) {
                throw new IllegalArgumentException("Priority harus 1 (Low), 2 (Medium), atau 3 (High).");
            }
            return priority;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Priority harus berupa angka: 1, 2, atau 3.");
        }
    }
}
