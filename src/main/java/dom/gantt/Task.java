
package dom.gantt;
import java.util.ArrayList;
import java.util.List;

public class Task extends TaskAbstract {
    private int taskStart;
    private int taskEnd;
    private int cost;
    private int effort;
    private boolean isSimple;
    private List<TaskAbstract> subtasks = new ArrayList<>(); // Λίστα subtask

    
    public Task(int taskId, int containerTaskId, String taskText, int taskStart, int taskEnd, int cost, int effort, boolean isSimple) {
        super(taskId, taskText, containerTaskId);
        this.taskStart = taskStart;
        this.taskEnd = taskEnd;
        this.cost = cost;
        this.effort = effort;
        this.isSimple = isSimple;
    }

    // Υλοποίηση αφηρημένων μεθόδων
    @Override
    public List<TaskAbstract> getSubtasks() {
        return subtasks; // Επιστροφή της λίστας subtask
    }

    @Override
    public int getMamaId() {
        return getContainerTaskId();
    }

    @Override
    public boolean isTopLevel() {
        return getContainerTaskId() == 0;
    }

    // Προσθήκη μεθόδου για διαχείριση subtask
    public void addSubtask(TaskAbstract subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(TaskAbstract subtask) {
        subtasks.remove(subtask);
    }

  
    public int getTaskStart() {
        return taskStart;
    }

    public void setTaskStart(int taskStart) {
        this.taskStart = taskStart;
    }

    public int getTaskEnd() {
        return taskEnd;
    }

    public void setTaskEnd(int taskEnd) {
        this.taskEnd = taskEnd;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public double getEffort() {
        return effort;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public void setSimple(boolean simple) {
        isSimple = simple;
    }

    @Override
    public String toString() {
        return String.format(
            "Task{id=%d, text='%s', containerTaskId=%d, start=%d, end=%d, cost=%d, effort=%d, isSimple=%b, subtasks=%d}",
            getTaskId(), getTaskText(), getContainerTaskId(), taskStart, taskEnd, cost, effort, isSimple, subtasks.size()
        );
    }
}
