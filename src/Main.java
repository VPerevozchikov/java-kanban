import model.Status;
import service.Manager;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        //Если Вы создаете новую задачу, в поле id введите null.
        //Если Вы обновляете задачу, в поле id введите соответсвующий номер.

        //При создании Epic статус задачи установить "NEW".
        //В ходе выполнения программы статус обновится в зависмости от статуса Подзадач.

        manager.createOrUpdateTask(null,"Покосить траву", "Газонокосилкой, за забором", Status.DONE);
        manager.createOrUpdateTask(null,"Сделать защелку", "На дверь калитки", Status.IN_PROGRESS);
        manager.createOrUpdateEpic(null,"Построить дом", "Двухэтажный, деревянный",Status.NEW);
        manager.createOrUpdateSubTask(null,"Вырыть фунтамент", "глубина 1,8 м", Status.IN_PROGRESS, 3);
        manager.createOrUpdateSubTask(null,"Навязать арматуру", "диаметр 10мм, шаг 200мм", Status.DONE, 3);
        manager.createOrUpdateEpic(null,"Сбросить 10 кг", "за 6 месяцев",Status.NEW);
        manager.createOrUpdateSubTask(null,"Заниматься спортом", "бег 3 р/нед по 2 км", Status.NEW, 6);
        manager.getListAllTasks();
        System.out.println("***********");
        manager.createOrUpdateTask(2,"Сделать защелку", "На дверь калитки", Status.DONE);
        manager.createOrUpdateSubTask(4,"Вырыть фунтамент", "глубина 1,8 м", Status.DONE, 3);
        manager.getListAllTasks();
        System.out.println("***********");
        manager.deleteTaskById(1);
        manager.deleteTaskById(3);
        manager.getListAllTasks();
        System.out.println("***********");
    }
}
