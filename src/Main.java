import model.Status;
import service.Managers;
import service.TaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();

        //Если Вы создаете новую задачу, в поле id введите null.
        //Если Вы обновляете задачу, в поле id введите соответсвующий номер.

        //При создании Epic статус задачи установить "NEW".
        //В ходе выполнения программы статус обновится в зависмости от статуса Подзадач.

        taskManager.createOrUpdateEpic(null,"Построить дом", "Двухэтажный, деревянный",Status.NEW);
        taskManager.createOrUpdateSubTask(null,"Вырыть фунтамент", "глубина 1,8 м", Status.IN_PROGRESS, 1);
        taskManager.createOrUpdateSubTask(null,"Навязать арматуру", "диаметр 10мм, шаг 200мм", Status.NEW, 1);
        taskManager.createOrUpdateSubTask(null,"Залить бетоном", "М350", Status.NEW, 1);
        taskManager.createOrUpdateEpic(null,"Сбросить 10 кг", "за 6 месяцев",Status.NEW);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(5);
        taskManager.getTaskById(4);
        taskManager.getHistory();
        taskManager.deleteTaskById(5);
        taskManager.getHistory();
        taskManager.deleteAllTasks();
        taskManager.getHistory();
    }
}
