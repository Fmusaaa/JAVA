# 🚀 Complete Setup Tutorial - Telegram Task Manager Bot

Step-by-step guide untuk install Maven dan setup Telegram bot dari zero.

---

## 📋 Table of Contents

1. [Install Java 11 JDK](#1-install-java-11-jdk)
2. [Install Maven](#2-install-maven)
3. [Verify Installation](#3-verify-installation)
4. [Create Telegram Bot](#4-create-telegram-bot)
5. [Build Project](#5-build-project)
6. [Run Bot](#6-run-bot)
7. [Troubleshooting](#7-troubleshooting)

---

## 1. Install Java 11 JDK

### Step 1.1: Download Java 11

1. Buka browser dan go ke: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
2. Scroll down cari "JDK 11.0.x" (versi latest)
3. Click download untuk **Windows x64 Installer** (file `.exe`)
4. Accept license agreement
5. Download akan start

### Step 1.2: Install Java

1. Buka file installer yang sudah didownload (double-click)
2. Click **Next** pada welcome screen
3. Click **Next** untuk accept default installation path
   - Usually: `C:\Program Files\Java\jdk-11.x.x`
4. Click **Next** dan **Install**
5. Click **Close** ketika selesai

### Step 1.3: Verify Java Installation

1. Buka Command Prompt (tekan `Windows + R`, ketik `cmd`, tekan Enter)
2. Ketik command:
   ```cmd
   java -version
   ```
3. Harus keluar output seperti:
   ```
   java version "11.0.x" 2021-xx-xx LTS
   Java(TM) SE Runtime Environment 18.9 (build 11.0.x+xx-LTS-xxx)
   Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.x+xx-LTS-xxx, mixed mode)
   ```

---

## 2. Install Maven

### Step 2.1: Download Maven

1. Buka browser dan go ke: https://maven.apache.org/download.cgi
2. Di bagian "Files", cari **Apache Maven 3.8.x** atau versi terbaru
3. Download file: **apache-maven-3.8.x-bin.zip** (binary archive)
4. Extract file ke folder yang mudah diakses, misal: `C:\Maven`
   - Hasil akhir struktur: `C:\Maven\apache-maven-3.8.x\`

### Step 2.2: Setup Environment Variables

#### Option A: Windows 10/11 GUI (Mudah)

**Step 1: Buka Environment Variables**

1. Tekan `Windows + X`
2. Pilih **System**
3. Click **Advanced system settings** (di sidebar kiri)
4. Click tab **Environment Variables**

**Step 2: Add JAVA_HOME (jika belum)**

1. Click **New** (di bagian System variables)
2. Variable name: `JAVA_HOME`
3. Variable value: `C:\Program Files\Java\jdk-11.x.x` (sesuaikan versi)
4. Click **OK**

**Step 3: Add MAVEN_HOME**

1. Click **New** (System variables)
2. Variable name: `MAVEN_HOME`
3. Variable value: `C:\Maven\apache-maven-3.8.x` (sesuaikan versi)
4. Click **OK**

**Step 4: Add Maven ke PATH**

1. Cari variable `Path` di System variables, click **Edit**
2. Click **New**
3. Ketik: `%MAVEN_HOME%\bin`
4. Click **OK**
5. Click **OK** lagi untuk close semua window

#### Option B: Command Prompt (Alternative)

Buka Command Prompt as Administrator:

```cmd
setx JAVA_HOME "C:\Program Files\Java\jdk-11.x.x"
setx MAVEN_HOME "C:\Maven\apache-maven-3.8.x"
setx PATH "%PATH%;%MAVEN_HOME%\bin"
```

---

## 3. Verify Installation

### Step 3.1: Close dan Reopen Command Prompt

Tutup semua Command Prompt window, terus buka yang baru (important!)

### Step 3.2: Check Java

```cmd
java -version
```

Output:

```
java version "11.0.x" ...
```

### Step 3.3: Check Maven

```cmd
mvn -version
```

Output harus seperti:

```
Apache Maven 3.8.x (xxxxxxx)
Maven home: C:\Maven\apache-maven-3.8.x
Java version: 11.0.x, vendor: Oracle Corporation
Java home: C:\Program Files\Java\jdk-11.x.x\jre
Default locale: ...
OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
```

✅ Jika kedua command menghasilkan output di atas = Installation berhasil!

---

## 4. Create Telegram Bot

### Step 4.1: Open Telegram

1. Buka aplikasi Telegram (download dari telegram.org jika belum)
2. Login dengan akun Telegram kamu

### Step 4.2: Create Bot via BotFather

1. Di search box Telegram, cari: **@BotFather** (official Telegram bot)
2. Click **BotFather** dan click **Start**
3. Send message: `/newbot`
4. BotFather akan ask:
   - **"Alright, a new bot. How are we going to call it?"**
     - Reply dengan nama bot (contoh: `My Task Manager Bot`)
   - **"Good. Now let's choose a username for your bot..."**
     - Reply dengan username (harus unique, contoh: `my_task_manager_bot_2025`)
     - **PENTING:** Username harus end dengan `_bot` atau `Bot`

### Step 4.3: Save Bot Token

BotFather akan send message dengan bot token, seperti:

```
Done! Congratulations on your new bot. You will find it at t.me/my_task_manager_bot_2025.
You can now add a description, about section and profile picture for your bot, see /help for a list of commands. By the way, when you've finished creating your bot and it handles any incoming messages, just "/setprivacy" to specify which users are allowed to see your bot's replies.

Here's your bot token:
123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
```

**SAVE TOKEN INI!** Gunakan untuk setup bot nanti.

---

## 5. Build Project

### Step 5.1: Navigate to Project Directory

```cmd
cd c:\Users\F MUSA\PBO\LATIHA
```

### Step 5.2: Clean dan Build

```cmd
mvn clean install
```

Output akan panjang, tapi tunggu sampai selesai. Kalo berhasil akan ada:

```
BUILD SUCCESS
```

Kalo ada error dengan message tentang "missing" files, berarti perlu download dependencies (first time akan ambil dari internet, bersabar aja).

### Step 5.3: Verify Build Success

Check apakah folder `target` sudah ada:

```cmd
dir target
```

Harus ada file `telegram-task-bot-1.0-SNAPSHOT.jar`

---

## 6. Run Bot

### Step 6.1: Set Environment Variables

**Option A: Temporary (Command Prompt Session Only)**

```cmd
set BOT_USERNAME=my_task_manager_bot_2025
set BOT_TOKEN=123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
```

Ganti:

- `my_task_manager_bot_2025` dengan username bot mu
- `123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11` dengan token dari BotFather

**Option B: Permanent (Set in System)**

Follow Step 2.2 (Environment Variables) tapi buat:

- Variable name: `BOT_USERNAME`
- Variable value: `my_task_manager_bot_2025`

Dan:

- Variable name: `BOT_TOKEN`
- Variable value: `123456:ABC-DEF1234...`

Terus restart Command Prompt!

### Step 6.2: Run Bot

Di folder project (`c:\Users\F MUSA\PBO\LATIHA`), ketik:

```cmd
mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"
```

Atau jika sudah built:

```cmd
java -jar target/telegram-task-bot-1.0-SNAPSHOT.jar
```

### Step 6.3: Verify Bot is Running

Output harus seperti:

```
✅ Task Manager Bot is running!
Bot username: my_task_manager_bot_2025
Press Ctrl+C to stop the bot.
```

🎉 Bot sudah berjalan!

---

## 7. Test Bot

### Step 7.1: Open Telegram Bot

1. Buka Telegram
2. Search username bot kamu (contoh: `my_task_manager_bot_2025`)
3. Click bot itu
4. Click **Start**

### Step 7.2: Test Commands

**Test /start:**

```
/start
```

Bot seharusnya reply dengan welcome message.

**Test /help:**

```
/help
```

Bot akan tampilkan semua available commands.

**Test /add_task:**

```
/add_task
```

Bot akan ask untuk input format:

```
Finish homework | Math and English | 15/03/2025 18:00 | 3
```

(copy-paste contoh di atas)

**Test /list_tasks:**

```
/list_tasks
```

Bot akan tampilkan semua tasks kamu.

---

## 7. Troubleshooting

### ❌ "mvn: command not found"

**Penyebab:** Maven tidak di-install atau PATH tidak set dengan benar

**Solution:**

1. Verify Maven installed di: `C:\Maven\apache-maven-3.8.x`
2. Verify PATH variable include: `%MAVEN_HOME%\bin`
3. Restart Command Prompt
4. Coba `mvn -version` lagi

### ❌ "java: command not found"

**Penyebab:** Java tidak di-install atau PATH tidak benar

**Solution:**

1. Verify Java installed di: `C:\Program Files\Java\jdk-11.x.x`
2. Verify JAVA_HOME variable set benar
3. Restart Command Prompt
4. Coba `java -version` lagi

### ❌ Bot tidak connect / "BOT_TOKEN environment variable not set"

**Penyebab:** Environment variables belum set

**Solution:**

1. Verify `BOT_USERNAME` dan `BOT_TOKEN` environment variables sudah set
2. Test dengan manual set:
   ```cmd
   set BOT_USERNAME=your_username
   set BOT_TOKEN=your_token
   mvn exec:java -Dexec.mainClass="com.taskbot.TaskBotApplication"
   ```

### ❌ "BUILD FAILURE" saat `mvn clean install`

**Penyebab:** Missing dependencies atau Java version tidak sesuai

**Solution:**

1. Verify Java 11: `java -version` (harus 11.0.x)
2. Clean cache:
   ```cmd
   mvn clean
   mvn install
   ```
3. Jika masih error, delete `.m2` folder:
   - Path: `C:\Users\[YourUsername]\.m2`
   - Terus run `mvn install` lagi (akan re-download semua dependencies)

### ❌ Database error / "taskbot.db permission denied"

**Solution:**

1. Close bot (Ctrl+C)
2. Delete `taskbot.db` file
3. Run bot lagi (akan auto-create database baru)

---

## ✅ Checklist

Pastikan lu sudah:

- [ ] Install Java 11 JDK
- [ ] Install Maven 3.8.x
- [ ] Verify `java -version` dan `mvn -version`
- [ ] Create bot di @BotFather
- [ ] Save bot token dan username
- [ ] Set `BOT_USERNAME` dan `BOT_TOKEN` environment variables
- [ ] `mvn clean install` di project folder
- [ ] Run bot dengan `mvn exec:java`
- [ ] Test bot di Telegram dengan `/start` command

---

## 🎉 Selesai!

Bot sudah ready to use! Untuk next steps:

1. **Customize bot** - Edit CommandHandler.java untuk ubah messages
2. **Add more features** - Misal reminders, categories, etc.
3. **Deploy 24/7** - Gunakan Docker atau server hosting

Enjoy! 🚀
