# cutetaskbot
Lightweight task bot for Telegram on Java

## Installing
Create your new Telegram bot through BotFather

Deploy cutetaskbot (for example to Heroku) amd create db (resources/initDb.sql)

Set environment variables:  
`BOT_NAME = <your bot name>`  
`BOT_TOKEN = <bot token>`  
`BOT_ADMIN_ID = <your Telegram userId>`   
You can get userId in start message when first time starts the bot 
`DATABASE_URL = <url to you postgres DB with user and pass. Starts with postgresql://>`  

Enjoy!

## Features:
You can add users by sending them invites.
Make some users task performers
New tasks are visible to all performers
Notification will be send to user, that created task, when his task will be completed
