package com.taskbot.ai;

import com.taskbot.model.ParsedTaskAction;

public class StubAiTaskParser implements AiTaskParser {
    @Override
    public ParsedTaskAction parse(String message) {
        // TODO: Integrate GPT/Gemini here later. Do not put API keys in code.
        return ParsedTaskAction.askClarification("AI parser belum aktif. Gunakan /add_task untuk menambah task manual.");
    }
}
