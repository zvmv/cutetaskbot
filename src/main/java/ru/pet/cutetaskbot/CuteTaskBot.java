package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.pet.cutetaskbot.repository.BotUserRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class CuteTaskBot extends TelegramLongPollingBot {
    Logger log = LoggerFactory.getLogger(CuteTaskBotApp.class);

    @Value("${bot.name}")
    private String BOT_NAME;
    @Value("${bot.token}")
    private String BOT_TOKEN;

    private final BotUserRepository repo;

    @Autowired
    private Menu menuInstance;

    @Autowired
    private Util util;

    @Autowired
    public CuteTaskBot(BotUserRepository repo) {
        this.repo = repo;
        log.info("TaskBot started...");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long userId = update.getMessage().getFrom().getId();
            log.info("Received message from " + userId);
            Message msg = update.getMessage();

            String userState = util.getUserState(userId);

            try {
                log.info("Try to invoke method " + userState);
                Method method = Menu.class.getDeclaredMethod(userState, Update.class, Long.class, Long.class);
                method.invoke(menuInstance, update, userId, msg.getChatId());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.toString());
                if (e.getCause() != null) log.error(e.getCause().toString());
            }
        } else if (update.hasCallbackQuery()) {
            String callback = update.getCallbackQuery().getData().split("_")[0];
            Long userId = update.getCallbackQuery().getFrom().getId();
            log.info("Received query from " + userId);
            String userState = util.getUserState(userId);
            Message message = update.getCallbackQuery().getMessage();

            try {
                log.info("Try to invoke method " + userState);
                Method method = Menu.class.getDeclaredMethod(callback, Update.class, Long.class, Long.class);
                method.invoke(menuInstance, update, userId, message.getChatId());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                log.error(e.toString());
                if (e.getCause() != null) log.error(e.getCause().toString());
            }

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
