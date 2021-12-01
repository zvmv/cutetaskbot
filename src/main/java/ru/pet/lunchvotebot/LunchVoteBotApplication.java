package ru.pet.lunchvotebot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;

public class LunchVoteBotApplication {
    final static Logger log = Logger.getLogger(LunchVoteBotApplication.class);
    private static final Map<String, String> getenv = System.getenv();

    public static void main(String[] args) {
        try {
            log.info("Starting app");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new LunchVoteBot(getenv.get("BOT_NAME"), getenv.get("BOT_TOKEN")));
        } catch (TelegramApiException e){
            log.error("Telegram exception");
        }
    }
}
