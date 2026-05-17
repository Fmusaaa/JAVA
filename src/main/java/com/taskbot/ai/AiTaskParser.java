package com.taskbot.ai;

import com.taskbot.model.ParsedTaskAction;

public interface AiTaskParser {
    ParsedTaskAction parse(String message);
}
