package test;

import static org.junit.Assert.*;   
import org.junit.Before;
import org.junit.Test;   
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors; 


public class TaskRetrieverTest {
	
	private TaskRetriever taskRetriever; 
    private List<Task> abstractTaskList;     
    
    @Before
    public void setUp() {
        taskRetriever = new TaskRetriever();
        abstractTaskList = new ArrayList<>();
    }
 
    @Test
    public void testRetrieveTasksHappyDayScenario() {
        //Δημιουργία λίστας εργασιών
        Task task1 = new Task(2, 1, 0); // Top-level task, start = 2
        Task task2 = new Task(1, 2, 0); // Top-level task, start = 1
        Task task3 = new Task(3, 3, 0); // Top-level task, start = 3
        abstractTaskList.addAll(Arrays.asList(task1, task2, task3));

        //Ανάκτηση όλων των εργασιών
        List<Task> retrievedTasks = taskRetriever.retrieveTasks(abstractTaskList, "ALL", 0, 0);
        retrievedTasks.sort(Comparator.comparingInt(Task::getTaskStart));

        // Όλες οι εργασίες ταξινομημένες
        List<Task> expectedTasks = Arrays.asList(task2, task1, task3);
        expectedTasks.sort(Comparator.comparingInt(Task::getTaskStart));
        assertEquals(expectedTasks, retrievedTasks);
    }       
    
    @Test
    public void testRetrieveTopLevelTasksHappyDayScenario() {
        // Λίστα με top-level και sub-tasks
        Task topTask1 = new Task(2, 1, 0); // Top-level task
        Task topTask2 = new Task(1, 2, 0); // Top-level task
        Task subTask1 = new Task(3, 3, 1); // Sub-task of topTask1
        Task subTask2 = new Task(4, 4, 1); // Sub-task of topTask1           
        abstractTaskList.addAll(Arrays.asList(topTask1, topTask2, subTask1, subTask2));       
        List<Task> retrievedTasks = taskRetriever.retrieveTasks(abstractTaskList, "TOP_LEVEL", 0, 0);           
        List<Task> expectedTasks = Arrays.asList(topTask2, topTask1);         
        assertEquals(expectedTasks, retrievedTasks);
    }      

    @Test
    public void testRetrieveTasksInRangeHappyDayScenario() {
        // Λίστα εργασιών με διαφορετικά TaskId
        Task task1 = new Task(2, 1, 0); 
        Task task2 = new Task(1, 2, 0); 
        Task task3 = new Task(3, 3, 0);         
        abstractTaskList.addAll(Arrays.asList(task1, task2, task3));          
        List<Task> retrievedTasks = taskRetriever.retrieveTasks(abstractTaskList, "RANGE", 1, 2);           
        List<Task> expectedTasks = Arrays.asList(task2, task1);          
        assertEquals(expectedTasks, retrievedTasks);
    }       
    
    @Test
    public void testRetrieveTasksRainyDayScenarioNoResults() {
        //Λίστα εργασιών χωρίς TaskId στο εύρος
        Task task1 = new Task(2, 1, 0); 
        Task task2 = new Task(1, 2, 0); 
        abstractTaskList.addAll(Arrays.asList(task1, task2));           
        List<Task> retrievedTasks = taskRetriever.retrieveTasks(abstractTaskList, "RANGE", 3, 4);           
        assertTrue(retrievedTasks.isEmpty());
    }       
    
    @Test
    public void testRetrieveTasksRainyDayScenarioInvalidInput() {
        //Λίστα εργασιών
        Task task1 = new Task(2, 1, 0); 
        Task task2 = new Task(1, 2, 0); 
        abstractTaskList.addAll(Arrays.asList(task1, task2));           
        try {
            // Ανάκτηση εργασιών με μη έγκυρα όρια
            taskRetriever.retrieveTasks(abstractTaskList, "RANGE", -1, -5);
            fail("Expected IllegalArgumentException for invalid input range");
        } 
        catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid range"));
        }
    }       
    
    // Mock κλάση TaskRetriever για τις ανάγκες των τεστ
    static class TaskRetriever {
    	public List<Task> retrieveTasks(List<Task> tasks, String filter, int rangeStart, int rangeEnd) {
    	    switch (filter) {
    	        case "ALL":
    	            return new ArrayList<>(tasks);
    	        case "TOP_LEVEL":
    	            return tasks.stream()
    	                    .filter(task -> task.getMamaId() == 0)
    	                    .sorted(Comparator.comparingInt(Task::getTaskStart).thenComparingInt(Task::getTaskId))
    	                    .collect(Collectors.toList());
    	        case "RANGE":
    	            if (rangeStart < 0 || rangeEnd < 0 || rangeStart > rangeEnd) {
    	                throw new IllegalArgumentException("Invalid range");
    	            }
    	            return tasks.stream()
    	                    .filter(task -> task.getTaskId() >= rangeStart && task.getTaskId() <= rangeEnd)
    	                    .sorted(Comparator.comparingInt(Task::getTaskStart).thenComparingInt(Task::getTaskId))
    	                    .collect(Collectors.toList());
    	        default:
    	            throw new IllegalArgumentException("Unsupported filter: " + filter);
    	    }
    	}
    }       
    
    // Mock κλάση Task για τις ανάγκες των τεστ
    static class Task {
        private int taskStart;
        private int taskId;
        private int mamaId; 
        
        public Task(int taskStart, int taskId, int mamaId) {
            this.taskStart = taskStart;
            this.taskId = taskId;
            this.mamaId = mamaId;
        }           
        public int getTaskStart() {
            return taskStart;
        }           
        public int getTaskId() {
            return taskId;
        }           
        public int getMamaId() {
            return mamaId;
        }           
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;               
            Task task = (Task) o;               
            if (taskStart != task.taskStart) return false;
            if (taskId != task.taskId) return false;
            return mamaId == task.mamaId;
        }           
        
        @Override
        public int hashCode() {
            int result = taskStart;
            result = 31 * result + taskId;
            result = 31 * result + mamaId;
            return result;
        }           
        
        @Override
        public String toString() {
            return "Task{" +
                    "taskStart=" + taskStart +
                    ", taskId=" + taskId +
                    ", mamaId=" + mamaId +
                    '}';
        }
    }
}