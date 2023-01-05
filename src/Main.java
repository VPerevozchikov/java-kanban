import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;

import static model.Status.IN_PROGRESS;
import static model.Status.NEW;

public class Main {

    public static void main(String[] args) throws IOException {

        TaskManager taskManager = Managers.getDefault();

        Epic epic1 = taskManager.createOrUpdateEpic(null,"Построить дом", "Двухэтажный_деревянный",Status.NEW);

        SubTask subTask1 = taskManager.createOrUpdateSubTask(null,"Вырыть фундамент", "глубина 1.8 м",
                Status.DONE, 8000L,
                LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0).plusMinutes(10),
                1);

        SubTask subTask2 = taskManager.createOrUpdateSubTask(null,"Навязать арматуру", "диаметр 10мм_шаг 200мм",
                Status.DONE, 10000L,
                LocalDateTime.of(2100, 1, 1, 0, 0, 0, 0).plusMinutes(400),
                1);

        SubTask subTask3 = taskManager.createOrUpdateSubTask(null,"Залить бетон", "М300",
                Status.NEW, 6000L,
                LocalDateTime.of(2200, 1, 1, 0, 0, 0, 0).plusMinutes(1000),
                1);
        SubTask subTask4 = taskManager.createOrUpdateSubTask(4,"Залить бетон_UPDATE", "М400_UPDATE",
                Status.DONE, 6000L,
                LocalDateTime.of(2200, 1, 1, 0, 0, 0, 0).plusMinutes(1000),
                1);

        Task task1 = taskManager.createOrUpdateTask(null,"Сходить в парикмахерскую", "Подстричься",
                Status.NEW,
                90L,
                LocalDateTime.of(2010, 1, 1, 0, 0, 0, 0).plusMinutes(100));

        taskManager.getListAllTasks();
        taskManager.printAllTasksAndSubTasksSortedByStartTime();

    }
}
