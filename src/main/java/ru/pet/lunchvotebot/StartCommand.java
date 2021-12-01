package ru.pet.lunchvotebot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends ServiceCommand {
    Logger log = Logger.getLogger(StartCommand.class);
    StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        log.info("Enter StartCommand processMessage");
        User user = message.getFrom();
        String userName = (user.getUserName() != null) ? user.getUserName()
                : String.format("%s %s", user.getFirstName(), user.getLastName());

        sendAnswer(absSender, message.getChatId(), this.getCommand(), userName,
                "Start command ok from " + userName);
    }
}
