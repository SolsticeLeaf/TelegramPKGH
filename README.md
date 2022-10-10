<h1 align="center">
  <img width=250 height=250 src="https://raw.githubusercontent.com/kiinse/TelegramPKGH/main/.github/pkghlogo.jpg"  alt=""/>
  <br>TelegramPKGH<br>
</h1>

<p align="center">
  <b>Ну да... Бот по расписанию ПКГХ👉👈</b><br><br>

  <a href="https://app.codacy.com/gh/kiinse/TelegramPKGH/dashboard">
    <img src="https://app.codacy.com/project/badge/Grade/04669f7c982b4ec8ba4783493dfb1ca9" alt="codacy"/>
  </a>
  <a href="https://github.com/kiinse/TelegramPKGH">
    <img src="https://img.shields.io/github/repo-size/kiinse/TelegramPKGH?style=flat-square" alt="size"> 
  </a>
  <a href="https://github.com/kiinse/TelegramPKGH/issues">
    <img src="https://img.shields.io/github/issues/kiinse/TelegramPKGH?style=flat-square" alt="issues"> 
  </a>
  <a href="https://github.com/kiinse/TelegramPKGH/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/kiinse/TelegramPKGH?style=flat-square" alt="licence"> 
  </a><br><br>
  <a href="#помощь">Помощь</a> •
  <a href="#особенности">Особенности</a> •
  <a href="#конфиг">Конфиг</a>
</p>

## Донаты

Если вы хотите поддержать проект, то просто поставьте на него звезду и подпишитесь на обновления =3

## Помощь

Заходите в чат [Telegram](https://t.me/podslyshanopkgh_chat) и зовите администрацию если у вас есть вопросы.

## Особенности

- Просмотр расписания

**Для установки требуется JDK 17+, а так же версия Gradle 6+**
**Рекомендую использовать [GraalVM](https://www.graalvm.org/)** 

## Конфиг

```properties
###     Основные настройки    ###

bot.token=Токен бота
bot.username=Юзернейм бота

###     Основные настройки БД   ###

# Выбранный движлк БД. SQLite / MySQL
db.selected=SQLite

# SQLite
# Путь к файлам .db sqlite
# ВНИМАНИЕ: Не ставьте "/" в конце
db.sqlite.path=Path to SQLite databases directory

# MySQL
db.mysql.host=MySQL host
db.mysql.port=MySQL port
db.mysql.user=MySQL User
db.mysql.password=MySQL Password
```
