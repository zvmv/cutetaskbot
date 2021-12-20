# cutetaskbot
Простой бот для Telegram для создания задач для исполнителя.

## Установка
Создайте своего бота Telegram через бота @BotFather. Вам понадибится имя бота и его токен.

- Скачайте дистрибутив cutetaskbot и соберите бота:  

      $ git clone http://github.com/zvmv/cutetaskbot

- Зарегистрируйтесь на http://heroku.com
- Устанвалием приложение heroku:
  
      $ curl https://cli-assets.heroku.com/install.sh | sh
- В папке с ботом выполняем  
  
      $ heroku login
- Откроется браузер, где надо залогиниться на heroku своими данными
- Создаём приложение heroku
  
      $ heroku create <имя приложения>
- Устанавливаем аддон базы данных
  
      $ heroku addons:create heroku-postgresql:hobby-dev
- Инициализируем базу данных (должна быть установленна клиентская часть postgresql)

      $ cat src/main/resources/initDb.sql | heroku cmd:psql
- Устанавливаем имя и токен бота:
  
      $ heroku config:set BOT_NAME=<имя_бота>  
      $ heroku config:set BOT_TOKEN=<токен_бота>  
      $ heroku config:set BOT_ADMIN_ID=0
- Загружаем и запускаем нашего бота
  
      $ git push heroku master
      $ heroku ps:scale worker=1

- Находим по имени бота в Telegram. В первом сообщении узнаём свой userID.
- Устанавлиаем переменную окружения администратора

      $ heroku config:set BOT_ADMIN_ID=<ID вашего пользователя>
      $ heroku ps:scale worker=0
      $ heroku ps:scale worker=1
     
- Наслаждаемся!

## Возможности:
- Добавить пользователя, прислав ему инвайт
- Назначьте пользователя исполнителем
- Все новые задачи от всех пользователей будут ему видны и будут приходит оповещения о создании
- После выполнения им задачи постановщику отсылается оповещение
