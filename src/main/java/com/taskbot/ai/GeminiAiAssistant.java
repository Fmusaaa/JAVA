package com.taskbot.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeminiAiAssistant implements AiAssistant {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAiAssistant.class);
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";
    private static final int TELEGRAM_SAFE_LENGTH = 3500;

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;

    public GeminiAiAssistant(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public String answer(String question) {
        if (apiKey == null || apiKey.isBlank()) {
            return "Gemini belum aktif. Isi GEMINI_API_KEY di file .env dulu.";
        }
        if (question == null || question.isBlank()) {
            return "Gunakan format: /ask <pertanyaan>";
        }

        try {
            String responseBody = sendRequest(buildPrompt(question));
            String answer = sanitizeForTelegram(extractText(responseBody).trim());
            return truncateForTelegram(answer);
        } catch (Exception e) {
            logger.error("Gemini assistant error", e);
            return "AI assistant sedang gagal menjawab. Coba lagi sebentar lagi.";
        }
    }

    private String sendRequest(String prompt) throws IOException, InterruptedException {
        String payload = buildPayload(prompt).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(API_URL, model)))
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Gemini API error " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private JsonObject buildPayload(String prompt) {
        JsonObject textPart = new JsonObject();
        textPart.addProperty("text", prompt);

        JsonArray parts = new JsonArray();
        parts.add(textPart);

        JsonObject content = new JsonObject();
        content.add("parts", parts);

        JsonArray contents = new JsonArray();
        contents.add(content);

        JsonObject payload = new JsonObject();
        payload.add("contents", contents);
        return payload;
    }

    private String buildPrompt(String question) {
        return """
                Kamu adalah asisten belajar di dalam Telegram Task Manager Bot.
                Jawab pertanyaan user dalam bahasa Indonesia yang santai, jelas, dan ringkas.
                Boleh membantu menjelaskan konsep, membuat outline, memberi contoh kode kecil, atau merangkum ide tugas.
                Jangan mengarang fakta kalau tidak yakin; beri catatan singkat jika perlu verifikasi.
                Usahakan jawaban mudah dibaca di Telegram.
                Jangan gunakan Markdown seperti **bold**, __underline__, heading ###, atau bullet *.
                Jika perlu daftar, pakai format "1. ..." atau "- ..." saja.

                Pertanyaan user:
                %s
                """.formatted(question);
    }

    private String extractText(String responseBody) {
        JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
        return root.getAsJsonArray("candidates")
                .get(0).getAsJsonObject()
                .getAsJsonObject("content")
                .getAsJsonArray("parts")
                .get(0).getAsJsonObject()
                .get("text").getAsString();
    }

    private String truncateForTelegram(String answer) {
        if (answer.length() <= TELEGRAM_SAFE_LENGTH) {
            return answer;
        }
        return answer.substring(0, TELEGRAM_SAFE_LENGTH) + "\n\n[Jawaban dipotong agar muat di Telegram.]";
    }

    private String sanitizeForTelegram(String text) {
        return text
                .replaceAll("\\*\\*(.*?)\\*\\*", "$1")
                .replaceAll("__(.*?)__", "$1")
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("(?m)^\\s*\\*\\s+", "- ")
                .replace("`", "")
                .trim();
    }
}
