import model.Status;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        //Если Вы создаете новую задачу, в поле id введите null.
        //Если Вы обновляете задачу, в поле id введите соответсвующий номер.

        //При создании Epic статус задачи установить "NEW".
        //В ходе выполнения программы статус обновится в зависмости от статуса Подзадач.

        taskManager.createOrUpdateTask(null,"Покосить траву", "Газонокосилкой, за забором", Status.DONE);
        taskManager.createOrUpdateTask(null,"Сделать защелку", "На дверь калитки", Status.IN_PROGRESS);
        taskManager.createOrUpdateEpic(null,"Построить дом", "Двухэтажный, деревянный",Status.NEW);
        taskManager.createOrUpdateSubTask(null,"Вырыть фунтамент", "глубина 1,8 м", Status.IN_PROGRESS, 3);
        taskManager.createOrUpdateSubTask(null,"Навязать арматуру", "диаметр 10мм, шаг 200мм", Status.DONE, 3);
        taskManager.createOrUpdateEpic(null,"Сбросить 10 кг", "за 6 месяцев",Status.NEW);
        taskManager.createOrUpdateSubTask(null,"Заниматься спортом", "бег 3 р/нед по 2 км", Status.NEW, 6);
      /*taskManager.getListAllTasks();
        System.out.println("***********");
        taskManager.createOrUpdateTask(2,"Сделать защелку", "На дверь калитки", Status.DONE);
        taskManager.createOrUpdateSubTask(4,"Вырыть фунтамент", "глубина 1,8 м", Status.DONE, 3);
        taskManager.getListAllTasks();
        System.out.println("***********");
        taskManager.printMapSubTasksOfEpic(3);
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(4);
        taskManager.createOrUpdateTask(2,"Сделать защелку", "На дверь калитки", Status.DONE);
        taskManager.getListAllTasks();
        System.out.println("***********");*/
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getTaskById(6);
        taskManager.getTaskById(7);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getHistory();
    }
}
