package com.taskbot.ai;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.taskbot.model.ParsedTaskAction;
import com.taskbot.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

public class GeminiAiTaskParser implements AiTaskParser {
    private static final Logger logger = LoggerFactory.getLogger(GeminiAiTaskParser.class);
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent";

    private final String apiKey;
    private final String model;
    private final HttpClient httpClient;

    public GeminiAiTaskParser(String apiKey, String model) {
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public ParsedTaskAction parse(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            return ParsedTaskAction.askClarification("Gemini belum aktif. Isi GEMINI_API_KEY di file .env dulu.");
        }

        try {
            String responseBody = sendRequest(buildPrompt(message));
            String text = extractText(responseBody);
            return parseJsonAction(text);
        } catch (Exception e) {
            logger.error("Gemini parser error", e);
            return ParsedTaskAction.askClarification("AI parser sedang gagal membaca pesan. Pakai /add_task dulu ya.");
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
        payload.add("generationConfig", buildGenerationConfig());
        return payload;
    }

    private JsonObject buildGenerationConfig() {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", "object");
        schema.add("properties", buildSchemaProperties());

        JsonArray required = new JsonArray();
        required.add("action");
        required.add("title");
        required.add("description");
        required.add("due_date");
        required.add("priority");
        required.add("clarification_message");
        schema.add("required", required);

        JsonObject config = new JsonObject();
        config.addProperty("responseMimeType", "application/json");
        config.add("responseJsonSchema", schema);
        return config;
    }

    private JsonObject buildSchemaProperties() {
        JsonObject properties = new JsonObject();
        properties.add("action", schemaField("string", "add_task jika pesan bisa jadi task, ask_clarification jika kurang jelas"));
        properties.add("title", nullableStringField("Judul task, null jika belum jelas"));
        properties.add("description", nullableStringField("Deskripsi singkat task, boleh string kosong"));
        properties.add("due_date", nullableStringField("Tanggal format dd/MM/yyyy HH:mm, null jika tidak disebut"));
        properties.add("priority", nullableIntegerField("1 low, 2 medium, 3 high, null jika tidak disebut"));
        properties.add("clarification_message", nullableStringField("Pertanyaan klarifikasi dalam bahasa Indonesia jika action ask_clarification"));
        return properties;
    }

    private JsonObject schemaField(String type, String description) {
        JsonObject field = new JsonObject();
        field.addProperty("type", type);
        field.addProperty("description", description);
        return field;
    }

    private JsonObject nullableStringField(String description) {
        JsonObject field = new JsonObject();
        JsonArray types = new JsonArray();
        types.add("string");
        types.add("null");
        field.add("type", types);
        field.addProperty("description", description);
        return field;
    }

    private JsonObject nullableIntegerField(String description) {
        JsonObject field = new JsonObject();
        JsonArray types = new JsonArray();
        types.add("integer");
        types.add("null");
        field.add("type", types);
        field.addProperty("description", description);
        return field;
    }

    private String buildPrompt(String message) {
        LocalDateTime now = LocalDateTime.now();
        return """
                Kamu adalah parser task untuk bot Telegram task manager.
                Ubah pesan user bahasa Indonesia/English menjadi JSON sesuai schema.

                Aturan:
                - action hanya boleh add_task atau ask_clarification.
                - Jika pesan bukan permintaan membuat/mengingatkan task, pakai ask_clarification.
                - Jika title tidak jelas, pakai ask_clarification.
                - due_date harus format dd/MM/yyyy HH:mm.
                - Jika user bilang besok, hitung dari tanggal sekarang.
                - Jika waktu tidak disebut, due_date boleh null.
                - Prioritas tinggi/high/urgent = 3, sedang/medium = 2, rendah/low = 1.
                - Jika prioritas tidak disebut, priority = 2.
                - Jangan tambahkan teks di luar JSON.

                Waktu sekarang: %s
                Pesan user: %s
                """.formatted(now.format(DateTimeUtil.USER_FORMATTER), message);
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

    private ParsedTaskAction parseJsonAction(String text) {
        JsonObject json = JsonParser.parseString(text).getAsJsonObject();
        String action = getNullableString(json, "action");

        if (!"add_task".equalsIgnoreCase(action)) {
            String clarification = getNullableString(json, "clarification_message");
            return ParsedTaskAction.askClarification(
                    clarification == null || clarification.isBlank()
                            ? "Maksudnya mau bikin task apa, brok?"
                            : clarification
            );
        }

        String title = getNullableString(json, "title");
        if (title == null || title.isBlank()) {
            return ParsedTaskAction.askClarification("Judul task-nya apa, brok?");
        }

        ParsedTaskAction parsed = new ParsedTaskAction();
        parsed.setAction("add_task");
        parsed.setTitle(title);
        parsed.setDescription(getNullableString(json, "description"));
        parsed.setPriority(getNullableInteger(json, "priority"));

        String dueDate = getNullableString(json, "due_date");
        if (dueDate != null && !dueDate.isBlank()) {
            parsed.setDueDate(DateTimeUtil.parseUserDateTime(dueDate));
        }
        return parsed;
    }

    private String getNullableString(JsonObject json, String name) {
        JsonElement element = json.get(name);
        return element == null || element.isJsonNull() ? null : element.getAsString();
    }

    private Integer getNullableInteger(JsonObject json, String name) {
        JsonElement element = json.get(name);
        return element == null || element.isJsonNull() ? null : element.getAsInt();
    }
}
