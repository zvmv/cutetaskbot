package ru.pet.lunchvotebot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

abstract class ServiceCommand extends BotCommand implements IBotCommand{
    Logger log = Logger.getLogger(ServiceCommand.class);
    ServiceCommand(String identifier, String description) {
        this.setCommand(identifier);
        this.setDescription(description);
    }

    @Override
    public String getCommandIdentifier() {
        return this.getCommand();
    }

    void sendAnswer(AbsSender absSender, Long chatId, String commandName, String userName, String text){
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId.toString());
        message.setText(text);
        log.info("Try to send answer on " + commandName + " command");
        try {
            absSender.execute(message);
        } catch (TelegramApiException e){
            log.info("Error sending reply in " + commandName + " command");
        }
    }

}
