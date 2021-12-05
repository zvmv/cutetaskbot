package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

@SpringBootApplication
@Component
public class CuteTaskBotApp {
    static ApplicationContext ctx;

    @Autowired
    public void setContext(ApplicationContext ctx){
        this.ctx = ctx;
    }

    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(CuteTaskBotApp.class);
        SpringApplication.run(CuteTaskBotApp.class);
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ctx.getBean(CuteTaskBot.class));
            log.info("Registered bot...");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
