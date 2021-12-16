package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.pet.cutetaskbot.model.BotUser;
import ru.pet.cutetaskbot.model.Task;
import ru.pet.cutetaskbot.repository.BotUserRepository;
import ru.pet.cutetaskbot.repository.TaskRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class Menu {
    private final BotUserRepository repo;
    private final TaskRepository taskRepo;

    @Autowired
    private Util util;

    Logger log = LoggerFactory.getLogger(CuteTaskBotApp.class);

    @Autowired
    public Menu(BotUserRepository repo, TaskRepository taskRepo) {
        this.repo = repo;
        this.taskRepo = taskRepo;
    }

    void notAuthorized(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered notAuthorized");
        util.sendAnswer(chatId, "Для использования сервиса нужно получить приглашение. Ваш userId = " + userId);
    }

    void userNotFound(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered userNotFound");
        String[] split = update.getMessage().getText().split(" ");
        if (split.length == 2) {
            String invite = update.getMessage().getText().split(" ")[1];
            if (util.checkInvite(Integer.parseInt(invite))) changeContactsMenu(update, userId, chatId);
        } else notAuthorized(update, userId, chatId);
    }

    void changeContactsMenu(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered changeContactsMenu");

        String reply = /*"Ваш ID " + userId + */"Введите своё ФИО и контактную информацию!";
        util.setUserState(userId, chatId, "inputUserContacts");
        util.sendAnswer(update, chatId, reply, null);
    }

    void inputUserContacts(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered inputUserContacts state");
        String reply = "Информация сохранена";
        BotUser user = repo.findById(userId).get();
        user.setState("mainMenu");
        user.setName(update.getMessage().getText());
        User telegramUser = update.getMessage().getFrom();
        user.setUserName(telegramUser.getUserName());
        user.setFirstName(telegramUser.getFirstName());
        user.setLastName(telegramUser.getLastName());
        if (userId.equals(util.ADMIN_ID)) user.setAdmin(true);
        repo.save(user);
        util.sendAnswer(chatId, reply);

        mainMenu(update, userId, chatId);
    }

    void mainMenu(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered mainMenu state");
        String reply = "Здравствуйте,\n" + repo.findById(userId).get().getName() + "\nГлавное меню:";
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Список задач").callbackData("taskListNotFinished").build(),
                InlineKeyboardButton.builder().text("Добавить задачу").callbackData("taskAddMenu").build()
        ));
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Изменить контактную информацию").callbackData("changeContactsMenu").build()
        ));
        if (util.isAdmin(userId))
            buttonRows.add(List.of(
                    InlineKeyboardButton.builder().text("Пользователи").callbackData("userListAll").build()
            ));

        markup.setKeyboard(buttonRows);

        util.setUserState(userId, chatId, "mainMenu");
        util.sendAnswer(update, chatId, reply, markup);
    }

    void taskListNotFinished(Update update, Long userId, Long chatId){
        taskList(update, userId, chatId, false);
    }

    void taskListFinished(Update update, Long userId, Long chatId){
        taskList(update, userId, chatId, true);
    }

    void taskList(Update update, Long userId, Long chatId, boolean finished){
        log.info("User " + userId + " entered" + (finished ?" finished" : " active") + " taskList menu");
        List<Task> tasks;
        if (util.isPerformer(userId)) {
            log.info("User get ALL tasks");
            tasks = taskRepo.findAllByFinished(finished);
        } else {
            log.info("User get his tasks");
            tasks = taskRepo.findAllByCreatedByIdAndFinished(userId, finished);
        }
        String reply;
        if (tasks.isEmpty()) {
            reply = "Список " + (finished ? "завершённых" : "активных") + " задач пуст";
            util.setUserState(userId, "mainMenu");
        } else reply = "Список" + (finished ? " завершённых" : " активных") + " задач: ";

        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();

        for (Task t : tasks){
            String taskId = t.getId().toString();
            buttonRows.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(taskId + ": " + Util.stripDate(t.getCreateDate()) + " - " + t.getDescription() + "\n")
                            .callbackData("taskDetails_" + taskId).build()
            ));
        }
        if (finished) {
            buttonRows.add(List.of(
                    InlineKeyboardButton.builder().text("Незавершённые").callbackData("taskListNotFinished").build(),
                    InlineKeyboardButton.builder().text("Добавить задачу").callbackData("taskAddMenu").build()
            ));
            buttonRows.add(List.of(
                    InlineKeyboardButton.builder().text("Очистить завершённые").callbackData("taskDeleteAllFinished").build()
            ));
        } else {
            buttonRows.add(List.of(
                    InlineKeyboardButton.builder().text("Завершённые").callbackData("taskListFinished").build(),
                    InlineKeyboardButton.builder().text("Добавить задачу").callbackData("taskAddMenu").build()
            ));
        }

        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Вернуться в главное меню").callbackData("mainMenu").build()
        ));

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(buttonRows).build();

        util.sendAnswer(update, chatId, reply, markup);
    }

    void taskAddMenu(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered taskAddMenu");
        String reply = "Введите описание задачи";
        util.sendAnswer(update, chatId, reply, null);
        util.setUserState(userId, "taskAdd");
    }

    void taskAdd(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered taskAdd");
        String reply = "Задача добавлена";
        String task = update.getMessage().getText();
        taskRepo.save(new Task(task, repo.getById(userId)));
        util.notifyPerformers("Для вас новая задача\n\"" + task
                + "\"\n Добавлена " + repo.findById(userId).get().getName());
        util.sendAnswer(chatId, reply);
        mainMenu(update, userId, chatId);
    }

    void taskDetails(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered taskDetails");
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Сведения о задаче " + taskId;
        Task task = taskRepo.findById(taskId).get();
        Boolean finished = task.getFinished();
        reply += taskId + "\n" +
                "Дата создания: " + task.getCreateDate() + "\n" +
                "Описание: " + task.getDescription() + "\n" +
//                "Завершить до: " + task.getMaxDate() + "\n" +
                "Завершена: " + (task.getFinishDate() == null? "Нет" : task.getFinishDate()) + "\n" +
                "Создал: " + task.getCreatedBy().getName();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text(finished ? "Возобновить" : "Выполнена")
                        .callbackData((finished ? "taskResume_" : "taskFinish_") + taskId).build(),
                InlineKeyboardButton.builder().text("Удалить")
                        .callbackData("taskDelete_" + taskId).build()
        ));
        if (finished) buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Вернуться к завершённым")
                .callbackData("taskListFinished").build()
        ));
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Вернуться к списку задач")
                .callbackData("taskListNotFinished").build()
        ));
        markup.setKeyboard(buttonRows);
        util.sendAnswer(update, chatId, reply, markup);
    }

    void taskDelete(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered taskDelete");
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Задача " + taskId + " удалена";
        taskRepo.deleteById(taskId);
        util.deleteMessageMarkup(update.getCallbackQuery().getMessage().getMessageId(), chatId);
        util.sendAnswer(chatId, reply);
        taskListNotFinished(null, userId, chatId);
    }

    void taskDeleteAllFinished(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered taskDeleteAllFinished");
        String reply = "Завершённые задачи удалены";
        if (util.isAdmin(userId)) taskRepo.deleteAllByFinished(true);
        else taskRepo.deleteAllByCreatedByIdAndFinished(userId, true);
        util.deleteMessageMarkup(update.getCallbackQuery().getMessage().getMessageId(), chatId);
        util.sendAnswer(chatId, reply);
        taskListNotFinished(null, userId, chatId);
    }

    void taskResume(Update update, Long userId, Long chatId){
        taskSetFinish(update, userId, chatId, false);
    }

    void taskFinish(Update update, Long userId, Long chatId){
        taskSetFinish(update, userId, chatId, true);
    }

    void taskSetFinish(Update update, Long userId, Long chatId, boolean finish){
        log.info("User " + userId + " entered taskSetFinish");
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Задача " + taskId + (finish ? " выполнена" : " возобновлена");
        Task task = taskRepo.findById(taskId).get();
        task.setFinished(finish);
        if (finish) task.setFinishDate(LocalDate.now());
        else task.setFinishDate(null);
        taskRepo.save(task);
        util.deleteMessageMarkup(update.getCallbackQuery().getMessage().getMessageId(), chatId);
        util.sendNotify(task.getCreatedBy().getChatId(), "Ваша задача:\n\"" + task.getDescription()
                + (finish ? "\"\nвыполнена" : "\"\nвозобновлена"));
        util.sendAnswer(chatId, reply);
        taskListNotFinished(null, userId, chatId);
    }


    public void userListAll(Update update, Long userId, Long chatId){
        userList(update, userId, chatId, repo.findAll());
    }

    public void userListAdmins(Update update, Long userId, Long chatId){
        userList(update, userId, chatId, repo.findAllByAdmin(true));
    }

    public void userListPerformers(Update update, Long userId, Long chatId){
        userList(update, userId, chatId, repo.findAllByPerformer(true));
    }


    public void userList(Update update, Long userId, Long chatId, List<BotUser> users){
        log.info("User " + userId + " entered userList state");
        String reply = "Список пользователей (ваш id = " + userId + ")";
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();

        for (BotUser u : users){
            String uId = u.getId().toString();
            buttonRows.add(List.of(InlineKeyboardButton.builder().text(
                    uId + ": " + u.getUserName()).callbackData("userDetails_" + uId).build()));
        }

        if (users.isEmpty()) {
            reply = "Список пользователей пуст";
        }

        buttonRows.add(List.of(InlineKeyboardButton.builder().text("Админы")
                        .callbackData("userListAdmins").build(),
                InlineKeyboardButton.builder().text("Исполнители")
                        .callbackData("userListPerformers").build(),
                InlineKeyboardButton.builder().text("Все")
                        .callbackData("userListAll").build()));
        buttonRows.add(List.of(InlineKeyboardButton.builder().text("Создать инвайт").callbackData("newInvite").build()));
        buttonRows.add(List.of(InlineKeyboardButton.builder().text("Вернуться в главное меню").callbackData("mainMenu").build()));

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(buttonRows).build();
        util.sendAnswer(update, chatId, reply, markup);
    }

    void userDetails(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered userDetails");
        String reply = "Сведения о пользователе ";
        Long uId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        BotUser user = repo.findById(uId).get();
        Boolean performer = user.getPerformer();
        Boolean admin = user.getAdmin();

        reply += uId + "\n" +
                "userName: " + user.getUserName() + "\n" +
                "firstName: " + user.getFirstName() + "\n" +
                "lastName: " + user.getLastName() + "\n" +
                "Инфо: " + user.getName() + "\n" +
                "Исполнитель: " + (performer?"Да":"Нет") + "\n" +
                "Администратор: " + (admin?"Да":"Нет") + "\n" +
                "Статус: " + user.getState();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttonRows = new ArrayList<>();
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text(performer? "Не исполнитель" : "Исполнитель")
                        .callbackData((performer ? "userUnSetPerformer_" : "userSetPerformer_") + uId).build(),
                InlineKeyboardButton.builder().text(admin? "Не админ" : "Админ")
                        .callbackData((admin? "userUnSetAdmin_" : "userSetAdmin_") + uId).build(),
                InlineKeyboardButton.builder().text("Удалить")
                        .callbackData("userDelete_" + uId).build()
        ));
        buttonRows.add(List.of(
                InlineKeyboardButton.builder().text("Вернуться к списку пользователей")
                .callbackData("userListAll").build()
        ));
        markup.setKeyboard(buttonRows);
        util.sendAnswer(update, chatId, reply, markup);
    }

    void userDelete(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered userDelete");
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Пользователь " + taskId + " удален";
        repo.deleteById(taskId);
        util.deleteMessageMarkup(update.getCallbackQuery().getMessage().getMessageId(), chatId);
        util.sendAnswer(chatId, reply);
        userListAll(null, userId, chatId);
    }

    void userSetPerformer(Update update, Long userId, Long chatId){
        userPerformer(update, userId, chatId, true);
    }

    void userUnSetPerformer(Update update, Long userId, Long chatId){
        userPerformer(update, userId, chatId, false);
    }

    void userPerformer(Update update, Long userId, Long chatId, boolean performer){
        log.info("User " + userId + " entered userPerformer");
        Long uId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        BotUser user = repo.findById(uId).get();
        user.setPerformer(performer);
        repo.save(user);
        userDetails(update, userId, chatId);
    }

    void userSetAdmin(Update update, Long userId, Long chatId){
        userAdmin(update, userId, chatId, true);
    }

    void userUnSetAdmin(Update update, Long userId, Long chatId){
        userAdmin(update, userId, chatId, false);
    }

    void userAdmin(Update update, Long userId, Long chatId, boolean admin){
        log.info("User " + userId + " entered userAdmin");
        Long uId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        BotUser user = repo.findById(uId).get();
        user.setAdmin(admin);
        repo.save(user);
        userDetails(update, userId, chatId);
    }

    void newInvite(Update update, Long userId, Long chatId){
        log.info("User " + userId + " entered newInvite");
        util.deleteMessage(update.getCallbackQuery().getMessage().getMessageId(), chatId);
        util.sendAnswer(chatId, util.createInvite());
        userListAll(null, userId, chatId);
    }
}
