package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.pet.cutetaskbot.model.BotUser;
import ru.pet.cutetaskbot.repository.BotUserRepository;

import java.time.LocalDate;

@Component
public class Util {
    @Value("${bot.perf_id}")
    public Long PERF_ID;

    private final BotUserRepository repo;

    @Autowired
    private CuteTaskBot sender;

    Logger log = LoggerFactory.getLogger(CuteTaskBotApp.class);

    @Autowired
    public Util(BotUserRepository repo) {
        this.repo = repo;
    }

    public String getUserState(Long userId){
        log.debug("Get user " + userId + " state");
        BotUser user = repo.findById(userId).orElse(null);
        if (user != null) return user.getState();
        return "changeContactsMenu";
    }

    public void setUserState(Long userId, String state){
        setUserState(userId, null, state);
    }

    public void setUserState(Long userId, Long chatId, String state){
        BotUser user = repo.findById(userId).orElse(new BotUser());
        if (user.getId() == null) log.debug("User " + userId + " not found");
        user.setId(userId);
        if (chatId != null) user.setChatId(chatId);
        user.setState(state);
        repo.save(user);
    }


    public void sendAnswer(Long chatId, String text) {
        sendAnswer(chatId, text, null);
    }

    public void sendAnswer(Long chatId, InlineKeyboardMarkup markup){
        sendAnswer(chatId, null, markup);
    }

    public void sendAnswer(Long chatId, String text, InlineKeyboardMarkup markup) {
        sendAnswer(null, chatId, text, markup);
    }

    public void sendAnswer(Update update, Long chatId, String text, InlineKeyboardMarkup markup){
        SendMessage sendMessage = null;
        EditMessageText editMessage = null;
        if (update != null && update.hasCallbackQuery()) {
            editMessage = new EditMessageText();
            if (text != null) editMessage.setText(text);
            if (markup != null) editMessage.setReplyMarkup(markup);
            editMessage.setChatId(chatId.toString());
            editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        } else {
            sendMessage = new SendMessage();
            sendMessage.setChatId(chatId.toString());
            if (text != null) sendMessage.setText(text);
            if (markup != null) sendMessage.setReplyMarkup(markup);
        };
        try {
            log.info("Sending message " + text + ((markup != null) ? " with markup..." : "..."));
            if (editMessage!= null) sender.execute(editMessage);
            else sender.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteMessage(Integer msgId, Long chatId){
        DeleteMessage msg = DeleteMessage.builder().messageId(msgId).chatId(chatId.toString()).build();
        try {
            log.info("Deleting message " + msgId + "...");
            sender.execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteMessageMarkup(Integer msgId, Long chatId){
        EditMessageReplyMarkup msg = EditMessageReplyMarkup.builder()
                .chatId(chatId.toString()).messageId(msgId).build();
        try {
            log.info("Deleting message markup " + msgId + "...");
            sender.execute(msg);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendNotify(Long userId, String text){
        repo.findById(userId).ifPresent(user -> sendAnswer(user.getChatId(), text));
    }

    public static String stripDate(LocalDate date){
        String[] weekDay = {"Вс", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

        LocalDate now = LocalDate.now();

        if (now.equals(date)) return "Сегодня";
        if (now.toEpochDay() - date.toEpochDay() == 1) return "Вчера";
        if (date.isBefore(now) && date.plusDays(7).isAfter(now))
            if (date.getDayOfWeek().getValue() <= now.getDayOfWeek().getValue()){
                return weekDay[date.getDayOfWeek().getValue()];
            }
        String result = "";
        result += date.getDayOfMonth() + "." + date.getMonthValue();
        if (now.getYear() != date.getYear())
            result += "." + date.getYear();
        return result;
    }


    public static void main(String[] args) {
        System.out.println(stripDate(LocalDate.now()));
        System.out.println(stripDate(LocalDate.parse("2021-12-04")));
        System.out.println(stripDate(LocalDate.parse("2021-12-03")));
        System.out.println(stripDate(LocalDate.parse("2021-12-02")));
        System.out.println(stripDate(LocalDate.parse("2021-11-30")));
        System.out.println(stripDate(LocalDate.parse("2021-11-29")));
        System.out.println(stripDate(LocalDate.parse("2021-11-28")));
        System.out.println(stripDate(LocalDate.parse("2021-12-06")));
        System.out.println(stripDate(LocalDate.parse("2021-12-07")));
        System.out.println(stripDate(LocalDate.parse("2020-12-07")));


    }
}
