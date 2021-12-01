package ru.pet.lunchvotebot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends ServiceCommand {
    StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    @Override
    public String getCommandIdentifier() {
        return null;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] strings) {
        User user = message.getFrom();
        String userName = (user.getUserName() != null) ? user.getUserName()
                : String.format("%s %s", user.getFirstName(), user.getLastName());

        sendAnswer(absSender, message.getChatId(), this.getCommand(), userName,
                "Start command ok from " + userName);
    }
}
