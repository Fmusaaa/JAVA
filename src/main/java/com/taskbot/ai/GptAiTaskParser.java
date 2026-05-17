package com.taskbot.ai;

import com.taskbot.model.ParsedTaskAction;

public class GptAiTaskParser implements AiTaskParser {
    @Override
    public ParsedTaskAction parse(String message) {
        // TODO: Implement OpenAI parser later if AI_PROVIDER=OPENAI is needed.
        return ParsedTaskAction.askClarification("GPT parser belum diaktifkan. Pakai GEMINI dulu ya.");
    }
}
