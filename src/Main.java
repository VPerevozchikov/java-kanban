import service.Manager;

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();
        String[] status = {"NEW", "IN_PROGRESS", "DONE"};

        //Если Вы создаете новую задачу, в поле id введите null.
        //Если Вы обновляете задачу, в поле id введите соответсвующий номер.

        //При создании Epic статус задачи установить "NEW".
        //В ходе выполнения программы статус обновится в зависмости от статуса Подзадач.

        manager.createOrUpdateTask(null,"Покосить траву", "Газонокосилкой, за забором",status[2]);
        manager.createOrUpdateTask(null,"Сделать защелку", "На дверь калитки", status[1]);
        manager.createOrUpdateEpic(null,"Построить дом", "Двухэтажный, деревянный",status[0]);
        manager.createOrUpdateSubTask(null,"Вырыть фунтамент", "глубина 1,8 м", status[1], 3);
        manager.createOrUpdateSubTask(null,"Навязать арматуру", "диаметр 10мм, шаг 200мм", status[2], 3);
        manager.createOrUpdateEpic(null,"Сбросить 10 кг", "за 6 месяцев",status[0]);
        manager.createOrUpdateSubTask(null,"Заниматься спортом", "бег 3 р/нед по 2 км", status[0], 6);
        manager.getListAllTasks();
        System.out.println("***********");
        manager.createOrUpdateTask(2,"Сделать защелку", "На дверь калитки", status[2]);
        manager.createOrUpdateSubTask(4,"Вырыть фунтамент", "глубина 1,8 м", status[2], 3);
        manager.getListAllTasks();
        System.out.println("***********");
        manager.deleteTaskById(1);
        manager.deleteTaskById(3);
        manager.getListAllTasks();
        System.out.println("***********");
    }
}
