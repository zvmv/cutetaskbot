package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class CuteTaskBot extends TelegramLongPollingBot {
    Logger log = LoggerFactory.getLogger(CuteTaskBotApp.class);

    @Value("${bot.name}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;

    @Autowired
    private Menu menuInstance;

    @Autowired
    private Util util;

    @Autowired
    public CuteTaskBot() {
        log.info("TaskBot started...");
    }

    @Override
    public void onUpdateReceived(Update update) {
        String methodToInvoke = null;
        Long userId = null;
        Long chatId = null;
        if (update.hasMessage() && update.getMessage().hasText()) {
            userId = update.getMessage().getFrom().getId();
            log.info("Received message from " + userId);
            chatId  = update.getMessage().getChatId();
            methodToInvoke = util.getUserState(userId);
        } else if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
            log.info("Received query from " + userId);
            chatId = update.getCallbackQuery().getMessage().getChatId();
            methodToInvoke = update.getCallbackQuery().getData().split("_")[0];
        } else {
            log.info("Update has message that could not be processed");
            return;
        }
        try {
            log.info("Try to invoke method " + methodToInvoke);
            Method method = Menu.class.getDeclaredMethod(methodToInvoke, Update.class, Long.class, Long.class);
            method.invoke(menuInstance, update, userId, chatId);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error(e.toString());
            if (e.getCause() != null) log.error(e.getCause().toString());
        }
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }
}
