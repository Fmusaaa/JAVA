# 📋 Telegram Task Manager Bot

A productivity bot untuk manage tasks via Telegram dengan Java. Supports task scheduling, priority levels, dan full CRUD operations.

## 🚀 Features

✅ **Task Management**

- Add new tasks dengan title, description, due date, dan priority
- List all tasks dengan sorting by priority dan due date
- Edit existing tasks
- Mark tasks as completed
- Delete tasks

✅ **Priority System**

- 🟢 Low (1)
- 🟡 Medium (2)
- 🔴 High (3)

✅ **Database**

- SQLite untuk persistent storage
- Per-user task isolation
- Automatic database initialization

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6+
- A Telegram bot (create via @BotFather)

## 🔧 Setup Instructions

### 1. Create Your Telegram Bot

1. Open Telegram dan search untuk **@BotFather**
2. Send `/newbot` command
3. Follow the prompts:
   - Bot name: "Task Manager Bot" (atau nama apapun)
   - Bot username: "your_task_manager_bot" (harus unique)
4. **Save the bot token** (Anda akan membutuhkannya)

### 2. Clone/Setup Project

```bash
cd c:\Users\F MUSA\PBO\LATIHA
mvn clean install
```

### 3. Set Environment Variables

**Windows (Command Prompt):**

```cmd
set BOT_USERNAME=your_bot_username
set BOT_TOKEN=your_bot_token
```

**Windows (PowerShell):**

```powershell
$env:BOT_USERNAME="your_bot_username"
$env:BOT_TOKEN="your_bot_token"
```

**Linux/Mac:**

```bash
export BOT_USERNAME=your_bot_username
export BOT_TOKEN=your_bot_token
```

Replace:

- `your_bot_username` dengan bot username dari BotFather (tanpa @)
- `your_bot_token` dengan token dari BotFather

### 4. Run the Bot

```bash
mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"
```

Atau jika sudah built:

```bash
java -jar target/telegram-task-bot-1.0-SNAPSHOT.jar
```

## 📖 Bot Commands

### /start

Tampilkan welcome message dan quick start guide

### /help

Tampilkan semua available commands dan format

### /add_task

Tambah task baru. Format:

```
<title> | <description> | <due_date> | <priority>
```

**Example:**

```
Finish project report | Complete Q1 analysis | 15/03/2025 14:30 | 3
```

**Notes:**

- Due date dan priority optional
- Format date: `dd/MM/yyyy HH:mm`
- Priority: 1 (Low), 2 (Medium), 3 (High)

### /list_tasks

Tampilkan semua tasks Anda, sorted by priority dan due date

### /edit_task

Edit existing task. Format:

```
<task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>
```

**Example:**

```
1 | Updated Title | New description | 20/03/2025 10:00 | 2
```

**Notes:**

- Task ID wajib
- Field lain bersifat optional (tidak diisi = tetap sama)

### /complete_task <task_id>

Mark task sebagai done

**Example:**

```
/complete_task 1
```

### /delete_task <task_id>

Delete task permanently

**Example:**

```
/delete_task 1
```

## 📁 Project Structure

```
src/main/java/com/taskbot/
├── TaskBotApplication.java          # Entry point
├── TaskManagerBot.java              # Main bot handler
├── database/
│   └── DatabaseManager.java         # SQLite operations
├── handler/
│   └── CommandHandler.java          # Command processing
└── model/
    └── Task.java                    # Task data model

src/main/resources/
└── logback.xml                      # Logging configuration

pom.xml                              # Maven configuration
taskbot.db                           # SQLite database (auto-created)
```

## 🗄️ Database Schema

**tasks table:**

```sql
CREATE TABLE tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    due_date TEXT,
    priority INTEGER DEFAULT 2,
    completed BOOLEAN DEFAULT 0,
    created_at TEXT NOT NULL
);
```

## 🐛 Troubleshooting

### Bot tidak connect

- **Solution:** Pastikan environment variables sudah set dengan benar
- Cek apakah bot token valid di @BotFather
- Pastikan username sesuai (tanpa @)

### Database error

- **Solution:** Delete `taskbot.db` file dan restart bot
- Bot akan auto-create database baru

### Port conflict

- Bot ini menggunakan long polling, tidak perlu port tertentu
- Jika ada connection issues, cek internet connection Anda

## 📝 Example Usage

1. **Mulai bot:**

   ```
   /start
   ```

2. **Tambah task:**

   ```
   /add_task
   Prepare presentation | Kumpulin data untuk Q1 meeting | 10/03/2025 09:00 | 3
   ```

3. **Lihat tasks:**

   ```
   /list_tasks
   ```

4. **Edit task (if needed):**

   ```
   /edit_task
   1 | Updated presentation | Dengan chart dan visualisasi | 12/03/2025 09:00 | 3
   ```

5. **Mark sebagai done:**
   ```
   /complete_task 1
   ```

## 🔄 Continuous Running

Untuk running bot 24/7, gunakan tools seperti:

- **Docker** (recommended)
- **PM2** (Node.js similar)
- **screen** atau **tmux** (Linux)
- **NSSM** (Windows service)

## 🤝 Contributing

Improvements welcome! Beberapa ideas:

- Add recurring tasks
- Category/Tags untuk tasks
- Reminder notifications
- Analytics/Statistics
- Multi-language support

## 📄 License

This project is open source and available for personal use.

---

**Happy task managing! 🚀**
