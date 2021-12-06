package ru.pet.cutetaskbot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
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

    void changeContactsMenu(Update update, Long userId, Long chatId){
        String reply = "Введите своё ФИО и контактную информацию!";
        log.info("User " + userId + " entered changeContactsMenu");
        util.sendAnswer(update, chatId, reply, null);
        util.setUserState(userId, "inputUserContacts");
    }

    void inputUserContacts(Update update, Long userId, Long chatId){
        String reply = "Информация сохранена";
        log.info("User " + userId + " entered inputUserContacts state");
        BotUser user = new BotUser(userId, update.getMessage().getText());
        user.setState("mainMenu");
        repo.save(user);
        util.sendAnswer(chatId, reply);

        mainMenu(update, userId, chatId);
    }

    void mainMenu(Update update, Long userId, Long chatId){
        String reply = "Здравствуйте,\n" + repo.findById(userId)
                .orElse(new BotUser((long)0, "Без имени")).getName() + "\nГлавное меню";
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder().text("Список задач").callbackData("taskListNotFinished").build(),
                        InlineKeyboardButton.builder().text("Добавить задачу").callbackData("taskAddMenu").build()))
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder().text("Изменить контактную информацию").callbackData("changeContactsMenu").build()))
                .build();
        log.info("User " + userId + " entered mainMenu state");
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
        log.info("User " + userId + " entered taskList state");
        List<Task> tasks = null;
        if (userId.equals(util.PERF_ID)) {
            tasks = taskRepo.findAllByFinished(finished);
            log.info("User get ALL tasks");
        } else {
            tasks = taskRepo.findAllByCreatedByIdAndFinished(userId, finished);
            log.info("User get his tasks");
        };
        String reply = "";
        if (tasks.isEmpty()) {
            reply = "Список " + (finished ? "завершённых" : "активных") + " задач пуст";
            util.setUserState(update.getCallbackQuery().getFrom().getId(), "mainMenu");
        } else reply = "Список" + (finished ? " завершённых" : " активных") + " задач: ";

        List<List<InlineKeyboardButton>> taskBtns = new ArrayList<>();

        for (Task t : tasks){
            String taskId = t.getId().toString();
            taskBtns.add(List.of(InlineKeyboardButton.builder().text(
                    taskId + ": " + Util.stripDate(t.getCreateDate()) + " - " + t.getDescription() + "\n").callbackData("taskDetails_" + taskId).build()));
        };
        if (finished) {
            taskBtns.add(List.of(InlineKeyboardButton.builder().text("Показать незавершённые").callbackData("taskListNotFinished").build()));
        } else {
            taskBtns.add(List.of(InlineKeyboardButton.builder().text("Показать завершённые").callbackData("taskListFinished").build()));
        };

        taskBtns.add(List.of(InlineKeyboardButton.builder().text("Вернуться в главное меню").callbackData("mainMenu").build()));

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder().keyboard(taskBtns).build();

        util.sendAnswer(update, chatId, reply, markup);
    }

    void taskAddMenu(Update update, Long userId, Long chatId){
        String reply = "Введите описание задачи";
        log.info("User " + userId + " entered taskAddMenu");
        util.sendAnswer(update, chatId, reply, null);
        util.setUserState(userId, "taskAdd");
    }

    void taskAdd(Update update, Long userId, Long chatId){
        String reply = "Задача добавлена";
        log.info("User " + userId + " entered taskAdd");
        String task = update.getMessage().getText();
        taskRepo.save(new Task(task, repo.getById(userId)));
        util.sendAnswer(chatId, reply);
        util.sendNotify(util.PERF_ID, "Для вас новая задача\n\"" + task
                + "\"\n Добавлена " + repo.findById(userId).get().getName());
        mainMenu(update, userId, chatId);
    }

    void taskDetails(Update update, Long userId, Long chatId){
        String reply = "Сведения о задаче ";
        log.info("User " + userId + " entered taskDetails");
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        Task task = taskRepo.findById(taskId).get();
        Boolean finished = task.getFinished();
        reply += taskId.toString() + "\n" +
                "Дата создания: " + task.getCreateDate() + "\n" +
                "Описание: " + task.getDescription() + "\n" +
//                "Завершить до: " + task.getMaxDate() + "\n" +
                "Завершена: " + task.getFinishDate() + "\n" +
                "Создал: " + task.getCreatedBy().getName();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(List.of(InlineKeyboardButton.builder().text(finished ? "Возобновить" : "Выполнена")
                        .callbackData((finished ? "taskResume_" : "taskFinish_") + taskId).build(),
                InlineKeyboardButton.builder().text("Удалить")
                        .callbackData("taskDelete_" + taskId).build()));
        if (finished) keyboardRows.add(List.of(InlineKeyboardButton.builder().text("Вернуться к завершённым")
                .callbackData("taskListFinished").build()));
        keyboardRows.add(List.of(InlineKeyboardButton.builder().text("Вернуться к списку задач")
                .callbackData("taskListNotFinished").build()));
        markup.setKeyboard(keyboardRows); util.sendAnswer(update, chatId, reply, markup);
    }

    void taskDelete(Update update, Long userId, Long chatId){
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Задача " + taskId + " удалена";
        log.info("User " + userId + " entered taskDelete");
        taskRepo.deleteById(taskId);
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
        Long taskId = Long.parseLong(update.getCallbackQuery().getData().split("_")[1]);
        String reply = "Задача " + taskId + (finish ? " выполнена" : " возобновлена");
        log.info("User " + userId + " entered taskSetFinish");
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
}
