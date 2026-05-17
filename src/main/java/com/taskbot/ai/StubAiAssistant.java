package com.taskbot.ai;

public class StubAiAssistant implements AiAssistant {
    @Override
    public String answer(String question) {
        return "AI assistant belum aktif. Isi AI_PROVIDER=GEMINI dan GEMINI_API_KEY di file .env dulu.";
    }
}
