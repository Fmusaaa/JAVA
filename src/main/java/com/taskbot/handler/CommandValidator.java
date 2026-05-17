package com.taskbot.handler;

import java.util.List;

public class CommandValidator {
    private static final List<String> AVAILABLE_COMMANDS = List.of(
            "/start",
            "/help",
            "/add_task",
            "/list_tasks",
            "/complete_task",
            "/edit_task",
            "/delete_task",
            "/today",
            "/tomorrow",
            "/overdue",
            "/high_priority",
            "/search",
            "/stats",
            "/ask",
            "/add_subtask",
            "/list_subtasks",
            "/done_subtask",
            "/delete_subtask",
            "/repeat"
    );

    public String getSuggestion(String commandName) {
        String normalized = commandName.toLowerCase();
        String bestCommand = null;
        int bestDistance = Integer.MAX_VALUE;

        for (String command : AVAILABLE_COMMANDS) {
            int distance = levenshtein(normalized, command);
            if (distance < bestDistance) {
                bestDistance = distance;
                bestCommand = command;
            }
        }

        return bestDistance <= 3 ? bestCommand : null;
    }

    public String availableCommandsMessage() {
        return "Command tidak dikenal.\n\nCommand tersedia:\n" + String.join("\n", AVAILABLE_COMMANDS);
    }

    private int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }
}
