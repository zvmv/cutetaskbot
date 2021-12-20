# cutetaskbot
Простой бот Telegram для постановки задач исполнителям.

## Установка
Создайте своего бота Telegram через бота @BotFather. Вам понадобится имя бота и его токен.

- Скачайте дистрибутив cutetaskbot:  

      $ git clone http://github.com/zvmv/cutetaskbot

- Зарегистрируйтесь на http://heroku.com
- Устанавливаем приложение heroku:
  
      $ curl https://cli-assets.heroku.com/install.sh | sh
- В папке с ботом выполняем и заходим в аккаунт хероку в открывшемся браузере 
  
      $ heroku login
- Создаём приложение heroku
  
      $ heroku create <имя приложения>
- Устанавливаем базу данных
  
      $ heroku addons:create heroku-postgresql:hobby-dev
- Инициализируем базу данных (должна быть установлена клиентская часть postgresql)

      $ cat src/main/resources/initDb.sql | heroku cmd:psql
- Устанавливаем имя и токен бота:
  
      $ heroku config:set BOT_NAME=<имя_бота>  
      $ heroku config:set BOT_TOKEN=<токен_бота>  
      $ heroku config:set BOT_ADMIN_ID=0
- публикуем и запускаем нашего бота
  
      $ git push heroku master
      $ heroku ps:scale worker=1

- Находим по имени бота в Telegram. В первом сообщении узнаём свой userID.
- Устанавлиаем переменную окружения администратора

      $ heroku config:set BOT_ADMIN_ID=<ID вашего пользователя>
     
- Наслаждаемся!

- Для отключения бота достаточно выполнить

      $ heroku ps:scale worker=0


## Возможности:
- Добавлять пользователей можно, прислав им инвайт (действует однократно в течение 7 дней)
- Назначьте пользователя исполнителем
- Исполнителю видны все задачи и будут приходить оповещения об их создании
- После выполнения задачи постановщику отсылается оповещение
