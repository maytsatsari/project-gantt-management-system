package test;  
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;   

public class TaskSorterTest {
    private TaskSorter taskSorter;
    private List<Task> abstractTaskList;     
    @Before
    public void setUp() {
        taskSorter = new TaskSorter();
        abstractTaskList = new ArrayList<>();
    }       
    @Test
    public void testSortTasksHappyDayScenario() {
        Task task1 = new Task(3, "Task 1");
        Task task2 = new Task(1, "Task 2");
        Task task3 = new Task(3, "Task 3");          
        abstractTaskList.addAll(Arrays.asList(task1, task2, task3));
        List<Task> sortedTasks = taskSorter.sortTasks(abstractTaskList);
        List<Task> expectedTasks = Arrays.asList(task2, task1, task3);
        assertEquals("Τα Tasks έχουν ταξινομηθεί σωστά", expectedTasks, sortedTasks);
    }      
@Test
    public void testSortTasksRainyDayScenario() {
        abstractTaskList.clear();
        List<Task> sortedTasks = taskSorter.sortTasks(abstractTaskList);
        assertTrue("Η λίστα είναι κενή", sortedTasks.isEmpty());
    }       
@Test
    public void testSortTasksWithDuplicates() {
        Task task1 = new Task(1, "Task A");
        Task task2 = new Task(1, "Task A"); // Ίδιο με task1
        Task task3 = new Task(2, "Task B");          
        abstractTaskList.addAll(Arrays.asList(task1, task2, task3));
        List<Task> sortedTasks = taskSorter.sortTasks(abstractTaskList);
        List<Task> expectedTasks = Arrays.asList(task1, task2, task3);
        assertEquals("Τα διπλότυπα tasks είναι μη αποδεκτά", expectedTasks, sortedTasks);
    }           
@Test
    public void testSortTasksSingleTask() {
        Task task1 = new Task(1, "Task A");           abstractTaskList.add(task1);
        List<Task> sortedTasks = taskSorter.sortTasks(abstractTaskList);
        List<Task> expectedTasks = Collections.singletonList(task1);
        assertEquals("Εργασία με ένα Task", expectedTasks, sortedTasks);
    }      
    @Test
    public void testSortTasksWithNullTasks() {
        Task task1 = new Task(1, "Task A");
        abstractTaskList.addAll(Arrays.asList(task1, null)); // Η λίστα περιέχει null          
        List<Task> sortedTasks = taskSorter.sortTasks(abstractTaskList);
        assertFalse("Η λίστα δεν πρέπει να περιέχει null tasks", sortedTasks.contains(null));
    }      
    // Mock κλάση Task για τις ανάγκες των τεστ
    static class Task implements Comparable<Task> {
        private int start;
        private String taskId;          
        public Task(int start, String taskId) {
            this.start = start;
            this.taskId = taskId;
        }           public int getStart() {
            return start;
        }           public String getTaskId() {
            return taskId;
        }          
        @Override
        public int compareTo(Task other) {
            int compareStart = Integer.compare(this.start, other.start);
            return compareStart != 0 ? compareStart : this.taskId.compareTo(other.taskId);
        }           
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Task task = (Task) o;
            if (start != task.start) return false;
            return taskId != null ? taskId.equals(task.taskId) : task.taskId == null;
        }          
        @Override
        public int hashCode() {
            int result = start;
            result = 31 * result + (taskId != null ? taskId.hashCode() : 0);
            return result;
        }          
        @Override
        public String toString() {
            return "Task{" +
                    "start=" + start +
                    ", taskId='" + taskId + '\'' +
                    '}';
        }
    }       
    // Mock κλάση TaskSorter για τις ανάγκες των τεστ
    static class TaskSorter {
        public List<Task> sortTasks(List<Task> tasks) {
            // Επιστρέφει μια νέα ταξινομημένη λίστα
            List<Task> sortedTasks = new ArrayList<>(tasks);
            sortedTasks.removeAll(Collections.singleton(null)); // Αφαιρεί τα null αν υπάρχουν
            Collections.sort(sortedTasks);
            return sortedTasks;
        }
    }
}