package ru.pet.lunchvotebot;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class StartCommand extends ServiceCommand {
    StartCommand(String identifier, String description) {
        super(identifier, description);
    }

    void execute(AbsSender absSender, Chat chat, String commandName, User user, String text) {
       String userName = (user.getUserName() != null) ? user.getUserName()
               : String.format("%s %s", user.getFirstName(), user.getLastName());

       sendAnswer(absSender, chat.getId(), this.getCommand(), userName,
               "Start command ok");
    }
}
