package com.taskbot.service;

import com.taskbot.model.RecurringTask;
import com.taskbot.model.ReminderNotification;
import com.taskbot.model.Subtask;
import com.taskbot.model.Task;
import com.taskbot.model.TaskStats;
import com.taskbot.model.User;
import com.taskbot.repository.RecurringTaskRepository;
import com.taskbot.repository.SubtaskRepository;
import com.taskbot.repository.TaskRepository;
import com.taskbot.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;
    private final RecurringTaskRepository recurringTaskRepository;

    public TaskService(UserRepository userRepository, TaskRepository taskRepository,
                       SubtaskRepository subtaskRepository, RecurringTaskRepository recurringTaskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.subtaskRepository = subtaskRepository;
        this.recurringTaskRepository = recurringTaskRepository;
    }

    public User registerUser(long telegramId, String username, String firstName) {
        return userRepository.findOrCreate(telegramId, username, firstName);
    }

    public Task addTask(long telegramId, String title, String description, LocalDateTime dueDate, int priority) {
        User user = resolveUser(telegramId);
        return taskRepository.addTask(user.getId(), title, description, dueDate, priority);
    }

    public List<Task> listTasks(long telegramId) {
        User user = resolveUser(telegramId);
        return taskRepository.getUserTasks(user.getId());
    }

    public List<Task> listDueOn(long telegramId, LocalDate date) {
        User user = resolveUser(telegramId);
        return taskRepository.findDueBetween(user.getId(), date.atStartOfDay(), date.atTime(23, 59, 59));
    }

    public List<Task> listOverdue(long telegramId) {
        User user = resolveUser(telegramId);
        return taskRepository.findOverdue(user.getId(), LocalDateTime.now());
    }

    public List<Task> listHighPriority(long telegramId) {
        User user = resolveUser(telegramId);
        return taskRepository.findHighPriorityPending(user.getId());
    }

    public List<Task> search(long telegramId, String keyword) {
        User user = resolveUser(telegramId);
        return taskRepository.search(user.getId(), keyword);
    }

    public TaskStats getStats(long telegramId) {
        User user = resolveUser(telegramId);
        return taskRepository.getStats(user.getId(), LocalDateTime.now());
    }

    public Task getTask(long telegramId, int taskId) {
        User user = resolveUser(telegramId);
        return taskRepository.getTask(user.getId(), taskId);
    }

    public boolean updateTask(long telegramId, int taskId, String title, String description,
                              LocalDateTime dueDate, int priority) {
        User user = resolveUser(telegramId);
        return taskRepository.updateTask(user.getId(), taskId, title, description, dueDate, priority);
    }

    public Task completeTask(long telegramId, int taskId) {
        User user = resolveUser(telegramId);
        Task task = taskRepository.getTask(user.getId(), taskId);
        if (task == null) {
            return null;
        }

        RecurringTask recurringTask = recurringTaskRepository.findByTaskId(taskId);
        boolean completed = taskRepository.completeTask(user.getId(), taskId, LocalDateTime.now());
        if (!completed) {
            return null;
        }

        if (recurringTask != null && task.getDueDate() != null) {
            LocalDateTime nextDueDate = calculateNextDueDate(task.getDueDate(), recurringTask.getRecurrenceType());
            Task nextTask = taskRepository.addTask(user.getId(), task.getTitle(), task.getDescription(),
                    nextDueDate, task.getPriority());
            LocalDateTime followingDueDate = calculateNextDueDate(nextDueDate, recurringTask.getRecurrenceType());
            recurringTaskRepository.save(nextTask.getId(), recurringTask.getRecurrenceType(),
                    recurringTask.getIntervalValue(), followingDueDate);
        }

        return taskRepository.getTask(user.getId(), taskId);
    }

    public boolean deleteTask(long telegramId, int taskId) {
        User user = resolveUser(telegramId);
        return taskRepository.deleteTask(user.getId(), taskId);
    }

    public Subtask addSubtask(long telegramId, int taskId, String title) {
        User user = resolveUser(telegramId);
        Task task = taskRepository.getTask(user.getId(), taskId);
        if (task == null) {
            return null;
        }
        return subtaskRepository.addSubtask(taskId, title);
    }

    public List<Subtask> listSubtasks(long telegramId, int taskId) {
        User user = resolveUser(telegramId);
        Task task = taskRepository.getTask(user.getId(), taskId);
        if (task == null) {
            return List.of();
        }
        return subtaskRepository.findByTaskId(taskId);
    }

    public boolean markSubtaskDone(long telegramId, int subtaskId) {
        User user = resolveUser(telegramId);
        return subtaskRepository.markDone(user.getId(), subtaskId);
    }

    public boolean deleteSubtask(long telegramId, int subtaskId) {
        User user = resolveUser(telegramId);
        return subtaskRepository.delete(user.getId(), subtaskId);
    }

    public String repeatTask(long telegramId, int taskId, String recurrenceType) {
        String normalizedType = recurrenceType.toLowerCase();
        if (!normalizedType.equals("daily") && !normalizedType.equals("weekly") && !normalizedType.equals("monthly")) {
            return "Tipe repeat harus daily, weekly, atau monthly.";
        }

        User user = resolveUser(telegramId);
        Task task = taskRepository.getTask(user.getId(), taskId);
        if (task == null) {
            return "Task tidak ditemukan.";
        }
        if (task.getDueDate() == null) {
            return "Task ini belum punya due date. Edit task dulu sebelum dibuat berulang.";
        }

        LocalDateTime nextDueDate = calculateNextDueDate(task.getDueDate(), normalizedType);
        recurringTaskRepository.save(taskId, normalizedType, 1, nextDueDate);
        return "Repeat berhasil diatur: task #" + taskId + " akan berulang " + normalizedType + ".";
    }

    public List<ReminderNotification> getDueReminderNotifications() {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findDueReminderNotifications(now, now.plusHours(1));
    }

    public List<ReminderNotification> getOverdueNotifications() {
        return taskRepository.findOverdueNotifications(LocalDateTime.now());
    }

    public void markReminderHandled(ReminderNotification notification) {
        if (notification.isOverdue()) {
            taskRepository.markOverdueNotified(notification.getTaskId());
        } else {
            taskRepository.markReminderSent(notification.getTaskId());
        }
    }

    public List<ReminderNotification> getAllNotifications() {
        List<ReminderNotification> notifications = new ArrayList<>();
        notifications.addAll(getDueReminderNotifications());
        notifications.addAll(getOverdueNotifications());
        return notifications;
    }

    private User resolveUser(long telegramId) {
        User user = userRepository.findByTelegramId(telegramId);
        return user != null ? user : userRepository.findOrCreate(telegramId, null, null);
    }

    private LocalDateTime calculateNextDueDate(LocalDateTime dueDate, String recurrenceType) {
        return switch (recurrenceType.toLowerCase()) {
            case "weekly" -> dueDate.plusWeeks(1);
            case "monthly" -> dueDate.plusMonths(1);
            default -> dueDate.plusDays(1);
        };
    }
}
