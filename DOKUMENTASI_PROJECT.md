# Dokumentasi Telegram Task Manager Bot

## 1. Gambaran Umum

Project ini adalah bot Telegram berbasis Java Maven untuk mengelola task. Bot dapat menambah, menampilkan, mengedit, menyelesaikan, menghapus, mencari task, membuat subtask/checklist, mengatur recurring task, dan mengirim reminder otomatis.

Project ini dibuat dengan struktur OOP agar mudah dijelaskan dalam presentasi PBO:

- `model`: representasi data/object.
- `repository`: akses database/DAO.
- `service`: logika bisnis.
- `handler`: validasi command dan format respons.
- `bot`: integrasi Telegram.
- `util`: helper format tanggal dan pesan.
- `ai`: kerangka AI parser untuk pengembangan berikutnya.
- `config`: konfigurasi bot dan database.

## 2. Struktur Folder Penting

```text
src/main/java/com/taskbot/
├── TaskBotApplication.java
├── TaskManagerBot.java
├── ai/
│   ├── AiTaskParser.java
│   └── StubAiTaskParser.java
├── config/
│   ├── AppConfig.java
│   └── DatabaseType.java
├── database/
│   └── DatabaseManager.java
├── handler/
│   ├── CommandHandler.java
│   └── CommandValidator.java
├── model/
│   ├── ParsedTaskAction.java
│   ├── RecurringTask.java
│   ├── ReminderNotification.java
│   ├── Subtask.java
│   ├── Task.java
│   ├── TaskStats.java
│   └── User.java
├── repository/
│   ├── RecurringTaskRepository.java
│   ├── SubtaskRepository.java
│   ├── TaskRepository.java
│   └── UserRepository.java
├── service/
│   ├── ReminderScheduler.java
│   └── TaskService.java
└── util/
    ├── DateTimeUtil.java
    └── MessageFormatter.java
```

## 3. Konfigurasi

Konfigurasi utama dibaca dari environment variable melalui `AppConfig`.

File contoh:

```text
config.example.env
```

File lokal yang dipakai saat run:

```text
.env
```

Contoh konfigurasi SQLite:

```env
BOT_USERNAME=nama_bot_kamu
BOT_TOKEN=token_bot_kamu

DB_TYPE=SQLITE
DB_URL=jdbc:sqlite:taskbot.db

REMINDER_INTERVAL_MINUTES=1
```

Contoh konfigurasi MySQL:

```env
BOT_USERNAME=nama_bot_kamu
BOT_TOKEN=token_bot_kamu

DB_TYPE=MYSQL
DB_URL=jdbc:mysql://localhost:3306/taskbot?useSSL=false&serverTimezone=Asia/Jakarta
DB_USERNAME=root
DB_PASSWORD=password_mysql_kamu

REMINDER_INTERVAL_MINUTES=1
```

Token bot dan password database tidak ditulis langsung di source code.

## 4. Database

Project mendukung dua database:

- SQLite: default, memakai file `taskbot.db`.
- MySQL: memakai JDBC MySQL jika `DB_TYPE=MYSQL`.

Schema MySQL tersedia di:

```text
src/main/resources/db/mysql_schema.sql
```

### 4.1 Tabel `users`

Menyimpan data user Telegram.

```sql
users:
- id INT AUTO_INCREMENT PRIMARY KEY
- telegram_id BIGINT UNIQUE NOT NULL
- username VARCHAR(100)
- first_name VARCHAR(100)
- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

Fungsi:

- Menghubungkan user Telegram dengan task miliknya.
- Mencegah task antar user tercampur.

### 4.2 Tabel `tasks`

Menyimpan task utama.

```sql
tasks:
- id INT AUTO_INCREMENT PRIMARY KEY
- user_id INT NOT NULL
- title VARCHAR(255) NOT NULL
- description TEXT NULL
- due_date DATETIME NULL
- priority INT DEFAULT 1
- status VARCHAR(20) DEFAULT 'pending'
- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- completed_at DATETIME NULL
- reminder_sent BOOLEAN DEFAULT FALSE
- overdue_notified BOOLEAN DEFAULT FALSE
```

Keterangan field penting:

- `priority`: `1 = Low`, `2 = Medium`, `3 = High`.
- `status`: `pending` atau `completed`.
- `completed_at`: waktu saat task diselesaikan.
- `reminder_sent`: agar reminder tidak dikirim berulang.
- `overdue_notified`: agar warning overdue tidak spam.

### 4.3 Tabel `subtasks`

Menyimpan checklist/subtask dari sebuah task.

```sql
subtasks:
- id INT AUTO_INCREMENT PRIMARY KEY
- task_id INT NOT NULL
- title VARCHAR(255) NOT NULL
- is_done BOOLEAN DEFAULT FALSE
- created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
```

Jika task dihapus, subtask ikut terhapus karena memakai `ON DELETE CASCADE`.

### 4.4 Tabel `recurring_tasks`

Menyimpan pengaturan task berulang.

```sql
recurring_tasks:
- id INT AUTO_INCREMENT PRIMARY KEY
- task_id INT NOT NULL
- recurrence_type VARCHAR(20) NOT NULL
- interval_value INT DEFAULT 1
- next_due_date DATETIME NULL
```

Nilai `recurrence_type`:

- `daily`
- `weekly`
- `monthly`

## 5. Penjelasan Class Utama

### 5.1 `TaskBotApplication`

Lokasi:

```text
src/main/java/com/taskbot/TaskBotApplication.java
```

Fungsi:

- Entry point aplikasi.
- Membaca konfigurasi dari `AppConfig`.
- Membuat `TelegramBotsApi`.
- Mendaftarkan `TaskManagerBot`.

### 5.2 `TaskManagerBot`

Lokasi:

```text
src/main/java/com/taskbot/TaskManagerBot.java
```

Fungsi:

- Menerima pesan dari Telegram.
- Mengambil `chatId`, `telegramId`, username, dan isi pesan.
- Menentukan apakah pesan adalah command atau input lanjutan.
- Mengatur state user untuk flow `/add_task` dan `/edit_task`.
- Mengirim balasan ke Telegram.
- Menjalankan reminder scheduler.

State yang digunakan:

```java
IDLE
ADDING_TASK
EDITING_TASK
```

State disimpan berdasarkan gabungan `chatId` dan `telegramId`, supaya lebih aman saat bot dipakai di grup.

### 5.3 `CommandHandler`

Lokasi:

```text
src/main/java/com/taskbot/handler/CommandHandler.java
```

Fungsi:

- Memproses input command.
- Validasi format input.
- Parsing task, priority, tanggal, dan ID.
- Memanggil `TaskService`.
- Membuat pesan respons untuk user.

Contoh:

- `/add_task` memanggil `addTask`.
- `/stats` memanggil `stats`.
- `/repeat` memanggil `repeatTask`.

### 5.4 `CommandValidator`

Lokasi:

```text
src/main/java/com/taskbot/handler/CommandValidator.java
```

Fungsi:

- Menyimpan daftar command yang valid.
- Memberi saran command jika user typo.

Contoh:

```text
/list_trask
```

Bot akan menyarankan:

```text
/list_tasks
```

### 5.5 `TaskService`

Lokasi:

```text
src/main/java/com/taskbot/service/TaskService.java
```

Fungsi:

- Menyimpan logika bisnis utama.
- Mengatur task, subtask, stats, recurring task, dan reminder.
- Menghubungkan handler dengan repository.

Contoh logika bisnis:

- Saat task recurring diselesaikan, task berikutnya otomatis dibuat.
- Saat task tidak punya due date, command `/repeat` akan ditolak.
- Reminder hanya dikirim untuk task pending.

### 5.6 Repository/DAO

Lokasi:

```text
src/main/java/com/taskbot/repository/
```

Repository bertugas mengakses database. SQL tidak diletakkan di bot atau handler.

Daftar repository:

- `UserRepository`: membuat/mencari user Telegram.
- `TaskRepository`: CRUD task, search, stats, reminder query.
- `SubtaskRepository`: tambah/list/done/delete subtask.
- `RecurringTaskRepository`: menyimpan dan membaca data recurring task.

### 5.7 `DatabaseManager`

Lokasi:

```text
src/main/java/com/taskbot/database/DatabaseManager.java
```

Fungsi:

- Membuka koneksi database.
- Menentukan SQLite atau MySQL.
- Membuat schema awal jika tabel belum ada.
- Migrasi SQLite lama ke struktur baru.
- Helper untuk menyimpan dan membaca `LocalDateTime`.

Untuk mengambil ID terakhir setelah insert:

- SQLite memakai `SELECT last_insert_rowid()`.
- MySQL memakai `SELECT LAST_INSERT_ID()`.

## 6. Daftar Command Bot

### Task utama

```text
/add_task
/list_tasks
/complete_task <id>
/edit_task
/edit_task <id>
/delete_task <id>
```

Format tambah task:

```text
<title> | <description> | <due_date> | <priority>
```

Contoh:

```text
Kerjain laporan PBO | Bab database dan class diagram | 15/05/2026 20:00 | 3
```

### Filter dan pencarian

```text
/today
/tomorrow
/overdue
/high_priority
/search <keyword>
/stats
```

### Subtask/checklist

```text
/add_subtask <task_id> <subtask title>
/list_subtasks <task_id>
/done_subtask <subtask_id>
/delete_subtask <subtask_id>
```

### Recurring task

```text
/repeat <task_id> <daily|weekly|monthly>
```

Contoh:

```text
/repeat 4 weekly
```

## 7. Letak AI

AI parser sudah mendukung mode `STUB` dan `GEMINI`.

Lokasi:

```text
src/main/java/com/taskbot/ai/
```

File:

- `AiTaskParser.java`
- `AiAssistant.java`
- `StubAiTaskParser.java`
- `StubAiAssistant.java`
- `GeminiAiTaskParser.java`
- `GeminiAiAssistant.java`

Model hasil parsing:

```text
src/main/java/com/taskbot/model/ParsedTaskAction.java
```

### 7.1 `AiTaskParser`

Interface untuk parser AI.

```java
public interface AiTaskParser {
    ParsedTaskAction parse(String message);
}
```

Nanti jika ingin integrasi GPT/Gemini, buat class baru misalnya:

```text
GeminiAiTaskParser.java
```

atau:

```text
GptAiTaskParser.java
```

Class tersebut cukup implement `AiTaskParser`.

### 7.2 `StubAiTaskParser`

Implementasi sementara.

Fungsi:

- Tidak memanggil API.
- Tidak butuh API key.
- Mengembalikan pesan klarifikasi.

Contoh target future AI:

Input user:

```text
Besok jam 8 ingetin gw kerjain laporan PBO prioritas tinggi
```

Output parsing yang diharapkan:

```text
action = add_task
title = Kerjain laporan PBO
due_date = parsed datetime
priority = 3
```

### 7.3 `GeminiAiTaskParser`

Lokasi:

```text
src/main/java/com/taskbot/ai/GeminiAiTaskParser.java
```

Fungsi:

- Memanggil Gemini API melalui REST endpoint `generateContent`.
- Mengirim prompt yang meminta output JSON.
- Memakai structured output agar hasil AI konsisten.
- Mengubah response Gemini menjadi `ParsedTaskAction`.
- Jika action adalah `add_task`, bot langsung membuat task.

Aktifkan Gemini lewat `.env`:

```env
AI_PROVIDER=GEMINI
GEMINI_API_KEY=isi_api_key_gemini_kamu
AI_MODEL=gemini-2.5-flash-lite
```

Contoh pesan natural:

```text
Besok jam 8 ingetin gw kerjain laporan PBO prioritas tinggi
```

AI akan mencoba menghasilkan:

```json
{
  "action": "add_task",
  "title": "Kerjain laporan PBO",
  "description": "",
  "due_date": "15/05/2026 08:00",
  "priority": 3,
  "clarification_message": null
}
```

Jika pesan tidak jelas, AI mengembalikan `ask_clarification` dan bot akan meminta user memperjelas task.

### 7.4 `GeminiAiAssistant`

Lokasi:

```text
src/main/java/com/taskbot/ai/GeminiAiAssistant.java
```

Fungsi:

- Menjawab pertanyaan umum dari user.
- Dipakai oleh command `/ask`.
- Cocok untuk membantu belajar, menjelaskan konsep PBO, membuat outline tugas, atau memberi contoh sederhana.
- Tetap memakai `GEMINI_API_KEY` yang sama dengan AI task parser.

Contoh:

```text
/ask jelasin polymorphism Java secara singkat
```

atau:

```text
/ask bantu bikin outline laporan PBO tentang Telegram Task Manager Bot
```

Perbedaan mode AI:

- Pesan natural tanpa slash: dipakai untuk parsing task otomatis.
- `/ask <pertanyaan>`: dipakai untuk asisten umum.

## 8. Algoritma yang Dipakai

### 8.1 Algoritma Levenshtein Distance

Lokasi:

```text
src/main/java/com/taskbot/handler/CommandValidator.java
```

Dipakai untuk mendeteksi typo command.

Contoh:

```text
/list_trask
```

Dibandingkan dengan daftar command valid. Jika jaraknya kecil, bot memberi saran:

```text
Maksud kamu /list_tasks?
```

Cara kerja singkat:

- Hitung jumlah minimal operasi edit dari string A ke string B.
- Operasi edit: tambah karakter, hapus karakter, ganti karakter.
- Command dengan jarak terkecil dipilih sebagai saran.

### 8.2 Algoritma Reminder Scheduler

Lokasi:

```text
src/main/java/com/taskbot/service/ReminderScheduler.java
```

Menggunakan:

```java
ScheduledExecutorService
```

Alur:

1. Scheduler berjalan setiap `REMINDER_INTERVAL_MINUTES`.
2. Ambil task pending yang due dalam 1 jam.
3. Kirim reminder jika `reminder_sent = false`.
4. Tandai `reminder_sent = true`.
5. Ambil task pending yang sudah lewat due date.
6. Kirim warning overdue jika `overdue_notified = false`.
7. Tandai `overdue_notified = true`.

Dengan flag ini, bot tidak mengirim reminder berulang-ulang.

### 8.3 Algoritma Recurring Task

Lokasi:

```text
src/main/java/com/taskbot/service/TaskService.java
```

Alur:

1. User menjalankan `/repeat <task_id> <daily|weekly|monthly>`.
2. Bot cek task ada atau tidak.
3. Bot cek task punya `due_date` atau tidak.
4. Saat task recurring diselesaikan, task lama tetap `completed`.
5. Bot membuat task pending baru dengan due date berikutnya.

Perhitungan due date:

- `daily`: `dueDate.plusDays(1)`
- `weekly`: `dueDate.plusWeeks(1)`
- `monthly`: `dueDate.plusMonths(1)`

### 8.4 Algoritma Search

Lokasi:

```text
src/main/java/com/taskbot/repository/TaskRepository.java
```

Search memakai SQL:

```sql
LOWER(title) LIKE '%keyword%'
OR LOWER(description) LIKE '%keyword%'
```

Tujuannya agar pencarian tidak sensitif huruf besar/kecil.

### 8.5 Algoritma Stats

Lokasi:

```text
src/main/java/com/taskbot/repository/TaskRepository.java
```

Stats dihitung memakai query `COUNT`.

Yang dihitung:

- Total pending tasks.
- Completed tasks.
- Overdue tasks.
- High priority pending tasks.
- Tasks completed this week.

Completed this week dihitung mulai hari Senin minggu berjalan sampai waktu sekarang.

### 8.6 Parsing Tanggal

Lokasi:

```text
src/main/java/com/taskbot/util/DateTimeUtil.java
```

Format yang didukung:

```text
dd/MM/yyyy HH:mm
```

Contoh:

```text
15/03/2025 14:30
```

Jika format salah, bot memberi pesan:

```text
Format tanggal salah. Contoh: 15/03/2025 14:30
```

## 9. Alur Program

Alur saat user menambah task:

```text
User Telegram
  -> TaskManagerBot
  -> CommandHandler
  -> TaskService
  -> TaskRepository
  -> DatabaseManager
  -> SQLite/MySQL
```

Alur saat scheduler reminder berjalan:

```text
ReminderScheduler
  -> TaskService
  -> TaskRepository
  -> Database
  -> ReminderNotification
  -> TaskManagerBot.sendMessage()
  -> Telegram
```

## 10. Cara Run Project

Build project:

```bash
mvn clean package
```

Jalankan bot:

```bash
./run.sh
```

`run.sh` akan otomatis membaca `.env`.

## 11. Cara Melihat Database SQLite

Masuk ke SQLite:

```bash
sqlite3 taskbot.db
```

Command SQLite:

```sql
.tables
.schema
.headers on
.mode column
SELECT * FROM users;
SELECT * FROM tasks;
SELECT * FROM subtasks;
SELECT * FROM recurring_tasks;
```

Keluar:

```sql
.quit
```

## 12. Catatan Presentasi PBO

Konsep OOP yang bisa dijelaskan:

- Encapsulation: field private di model dan akses lewat getter/setter.
- Abstraction: `AiTaskParser` sebagai interface.
- Layering: bot, handler, service, repository, model.
- Single Responsibility Principle:
  - Bot hanya urus Telegram.
  - Handler urus command.
  - Service urus bisnis.
  - Repository urus database.
  - Util urus helper umum.
- Polymorphism future-ready: AI parser bisa diganti dari `StubAiTaskParser` ke parser GPT/Gemini tanpa mengubah banyak kode.
