#!/bin/bash
set -e

if [ -f ".env" ]; then
  set -a
  . ./.env
  set +a
fi

if [ -z "$BOT_USERNAME" ] || [ -z "$BOT_TOKEN" ]; then
  echo "BOT_USERNAME dan BOT_TOKEN harus diset lewat environment variable atau file .env."
  echo "Lihat config.example.env untuk contoh."
  exit 1
fi

java -jar target/telegram-task-bot-1.0-SNAPSHOT.jar
