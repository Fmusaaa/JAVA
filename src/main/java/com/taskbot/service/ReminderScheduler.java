package com.taskbot.service;

import com.taskbot.model.ReminderNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TaskService taskService;
    private final ReminderSender reminderSender;
    private final int intervalMinutes;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ReminderScheduler(TaskService taskService, ReminderSender reminderSender, int intervalMinutes) {
        this.taskService = taskService;
        this.reminderSender = reminderSender;
        this.intervalMinutes = intervalMinutes;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkReminders, intervalMinutes, intervalMinutes, TimeUnit.MINUTES);
        logger.info("Reminder scheduler started. Interval: {} minute(s)", intervalMinutes);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void checkReminders() {
        try {
            List<ReminderNotification> notifications = taskService.getAllNotifications();
            for (ReminderNotification notification : notifications) {
                reminderSender.send(notification);
                taskService.markReminderHandled(notification);
            }
        } catch (Exception e) {
            logger.error("Reminder scheduler error", e);
        }
    }

    @FunctionalInterface
    public interface ReminderSender {
        void send(ReminderNotification notification) throws Exception;
    }
}
