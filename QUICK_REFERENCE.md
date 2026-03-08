# ⚡ Quick Reference - Command Cheat Sheet

## 🔧 Installation Commands

```cmd
REM Install Java 11 - via GUI installer from oracle.com

REM Install Maven - download and extract to C:\Maven

REM Verify installations
java -version
mvn -version

REM Set environment variables (temporary, session only)
set JAVA_HOME=C:\Program Files\Java\jdk-11.x.x
set MAVEN_HOME=C:\Maven\apache-maven-3.8.x
set BOT_USERNAME=your_bot_username
set BOT_TOKEN=your_bot_token
```

---

## 🤖 Bot Setup Commands

```cmd
REM Navigate to project directory
cd c:\Users\F MUSA\PBO\LATIHA

REM Build project (first time, downloads dependencies)
mvn clean install

REM Run bot (via Maven)
mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"

REM Run bot (via JAR, after build)
java -jar target/telegram-task-bot-1.0-SNAPSHOT.jar

REM Stop bot
Ctrl + C
```

---

## 📝 Bot Commands (In Telegram)

```
/start                          → Welcome message & quick start
/help                           → Show all commands & help

/add_task                       → Add new task
/list_tasks                     → View all tasks
/edit_task                      → Edit existing task
/complete_task <task_id>        → Mark task as done
/delete_task <task_id>          → Delete task
```

---

## 📋 Task Input Format

### Add Task

```
<title> | <description> | <due_date> | <priority>

Example:
Finish project report | Complete Q1 analysis | 15/03/2025 14:30 | 3
```

### Edit Task

```
<task_id> | <new_title> | <new_description> | <new_due_date> | <new_priority>

Example:
1 | Updated Title | New description | 20/03/2025 10:00 | 2
```

### Complete Task

```
/complete_task 1
```

### Delete Task

```
/delete_task 1
```

---

## 🎯 Priority Levels

```
1 = 🟢 Low
2 = 🟡 Medium  (default)
3 = 🔴 High
```

---

## 📅 Date Format

```
dd/MM/yyyy HH:mm

Examples:
15/03/2025 14:30
01/04/2025 09:00
31/12/2025 23:59
```

---

## 🗂️ Project Structure

```
c:\Users\F MUSA\PBO\LATIHA\
├── pom.xml                                    (Maven configuration)
├── README.md                                  (Full documentation)
├── SETUP_TUTORIAL.md                          (This tutorial)
├── taskbot.db                                 (Database - auto created)
├── src/
│   └── main/
│       ├── java/com/taskbot/
│       │   ├── TaskBotApplication.java        (Entry point)
│       │   ├── TaskManagerBot.java            (Bot logic)
│       │   ├── database/
│       │   │   └── DatabaseManager.java       (Database operations)
│       │   ├── handler/
│       │   │   └── CommandHandler.java        (Command processing)
│       │   └── model/
│       │       └── Task.java                  (Task data class)
│       └── resources/
│           └── logback.xml                    (Logging config)
└── target/
    └── telegram-task-bot-1.0-SNAPSHOT.jar    (Built JAR)
```

---

## 🐛 Common Issues & Fixes

| Issue                     | Cause                                   | Solution                                           |
| ------------------------- | --------------------------------------- | -------------------------------------------------- |
| `mvn: command not found`  | Maven not installed/PATH wrong          | Reinstall Maven, check PATH                        |
| `java: command not found` | Java not installed/PATH wrong           | Reinstall Java, check JAVA_HOME                    |
| `BOT_TOKEN not set`       | Environment variable missing            | Set `BOT_USERNAME` & `BOT_TOKEN`                   |
| `BUILD FAILURE`           | Missing dependencies/wrong Java version | Ensure Java 11, run `mvn clean install`            |
| Database error            | Permission denied on .db file           | Delete `taskbot.db`, restart bot                   |
| Bot not responding        | Bot not running or token invalid        | Check if bot is running, verify token at BotFather |

---

## 💡 Pro Tips

1. **Long polling**: Bot uses polling (tidak perlu webhook/port) - lebih simple untuk development
2. **Per-user isolation**: Setiap Telegram user punya task list sendiri (by user_id)
3. **State management**: Bot track user state (adding/editing) untuk better UX
4. **Offline-first**: SQLite jadi bot work offline (local database)
5. **Markdown formatting**: Bot support Markdown untuk message formatting

---

## 🚀 Next Steps

1. Run bot & test di Telegram
2. Customize messages di `CommandHandler.java`
3. Add more features (reminders, categories, export, etc.)
4. Deploy 24/7 menggunakan Docker or VPS

---

## 📚 Useful Links

- Java: https://www.oracle.com/java/
- Maven: https://maven.apache.org/
- Telegram Bot API: https://core.telegram.org/bots
- TelegramBots Library: https://github.com/rubenlagus/TelegramBots
- SQLite: https://www.sqlite.org/

---

**Siap deploy? 🚀**
