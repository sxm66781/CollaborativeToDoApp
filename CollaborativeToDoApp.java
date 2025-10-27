import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

class Task {
    private final String taskId;
    private String description;
    private String category;
    private boolean completed;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final String assignedUser;

    public Task(String taskId, String description, String category, String assignedUser) {
        this.taskId = taskId;
        this.description = description;
        this.category = category;
        this.assignedUser = assignedUser;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getTaskId() { 
        return taskId; 
    }
    
    public String getDescription() { 
        return description; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public boolean isCompleted() { 
        return completed; 
    }
    
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public String getAssignedUser() { 
        return assignedUser; 
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public String getStatus() {
        return completed ? "Completed" : "Pending";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Task ID: ").append(taskId).append("\n");
        sb.append("User: ").append(assignedUser).append("\n");
        sb.append("Description: ").append(description).append("\n");
        sb.append("Category: ").append(category).append("\n");
        sb.append("Status: ").append(getStatus()).append("\n");
        sb.append("Created: ").append(formatDateTime(createdAt)).append("\n");
        sb.append("Updated: ").append(formatDateTime(updatedAt)).append("\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return "Task[ID=" + taskId + ", User=" + assignedUser + 
               ", Description=" + description + ", Category=" + category + 
               ", Status=" + getStatus() + "]";
    }
}

class User {
    private final String username;
    private final List<String> taskIds;
    private final long createdAt;

    public User(String username) {
        this.username = username;
        this.taskIds = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public String getUsername() {
        return username;
    }

    public synchronized List<String> getTaskIds() {
        return new ArrayList<>(taskIds);
    }

    public synchronized void addTaskId(String taskId) {
        if (!taskIds.contains(taskId)) {
            taskIds.add(taskId);
        }
    }

    public synchronized boolean removeTaskId(String taskId) {
        return taskIds.remove(taskId);
    }

    public synchronized int getTaskCount() {
        return taskIds.size();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public synchronized boolean hasTask(String taskId) {
        return taskIds.contains(taskId);
    }

    @Override
    public String toString() {
        return "User[username=" + username + ", tasks=" + taskIds.size() + "]";
    }
}

class TaskManager {
    private final ConcurrentHashMap<String, Task> tasks;
    private final ConcurrentHashMap<String, User> users;
    private final AtomicInteger taskCounter;

    public TaskManager() {
        this.tasks = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.taskCounter = new AtomicInteger(1);
    }

    public synchronized User registerUser(String username) {
        if (users.containsKey(username)) {
            System.out.println("User '" + username + "' already exists.");
            return null;
        }
        User user = new User(username);
        users.put(username, user);
        System.out.println("User '" + username + "' registered successfully.");
        return user;
    }

    public User getUser(String username) {
        return users.get(username);
    }

    public synchronized Task createTask(String username, String description, String category) {
        User user = users.get(username);
        if (user == null) {
            System.out.println("User '" + username + "' not found. Please register first.");
            return null;
        }

        String taskId = "TASK-" + String.format("%04d", taskCounter.getAndIncrement());
        Task task = new Task(taskId, description, category, username);
        tasks.put(taskId, task);
        user.addTaskId(taskId);

        System.out.println("Task '" + taskId + "' created for user '" + username + "'.");
        return task;
    }

    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public synchronized boolean updateTaskDescription(String taskId, String newDescription) {
        Task task = tasks.get(taskId);
        if (task == null) {
            System.out.println("Task '" + taskId + "' not found.");
            return false;
        }
        task.setDescription(newDescription);
        System.out.println("Task '" + taskId + "' description updated.");
        return true;
    }

    public synchronized boolean updateTaskCategory(String taskId, String newCategory) {
        Task task = tasks.get(taskId);
        if (task == null) {
            System.out.println("Task '" + taskId + "' not found.");
            return false;
        }
        task.setCategory(newCategory);
        System.out.println("Task '" + taskId + "' category updated to '" + newCategory + "'.");
        return true;
    }

    public synchronized boolean completeTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            System.out.println("Task '" + taskId + "' not found.");
            return false;
        }
        task.setCompleted(true);
        System.out.println("Task '" + taskId + "' marked as completed.");
        return true;
    }

    public synchronized boolean uncompleteTask(String taskId) {
        Task task = tasks.get(taskId);
        if (task == null) {
            System.out.println("Task '" + taskId + "' not found.");
            return false;
        }
        task.setCompleted(false);
        System.out.println("Task '" + taskId + "' marked as pending.");
        return true;
    }

    public synchronized boolean deleteTask(String taskId) {
        Task task = tasks.remove(taskId);
        if (task == null) {
            System.out.println("Task '" + taskId + "' not found.");
            return false;
        }
        
        User user = users.get(task.getAssignedUser());
        if (user != null) {
            user.removeTaskId(taskId);
        }
        
        System.out.println("Task '" + taskId + "' deleted.");
        return true;
    }

    public List<Task> getUserTasks(String username) {
        User user = users.get(username);
        if (user == null) {
            return new ArrayList<>();
        }
        
        return user.getTaskIds().stream()
                .map(tasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByCategory(String category) {
        return tasks.values().stream()
                .filter(task -> task.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Task> getPendingTasks(String username) {
        return getUserTasks(username).stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

    public List<Task> getCompletedTasks(String username) {
        return getUserTasks(username).stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public void displayUserTasks(String username) {
        List<Task> userTasks = getUserTasks(username);
        
        if (userTasks.isEmpty()) {
            System.out.println("\nNo tasks found for user '" + username + "'.\n");
            return;
        }

        System.out.println("\n==================================================");
        System.out.println("Tasks for User: " + username);
        System.out.println("==================================================");
        
        for (Task task : userTasks) {
            System.out.println(task.toDetailedString());
        }
    }

    public void displayStatistics() {
        System.out.println("\n==================================================");
        System.out.println("System Statistics");
        System.out.println("==================================================");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Tasks: " + tasks.size());
        System.out.println("Completed Tasks: " + 
            tasks.values().stream().filter(Task::isCompleted).count());
        System.out.println("Pending Tasks: " + 
            tasks.values().stream().filter(task -> !task.isCompleted()).count());
        System.out.println("==================================================\n");
    }
}

class UserTaskWorker implements Runnable {
    private final TaskManager taskManager;
    private final String username;
    private final String description;
    private final String category;
    private final CountDownLatch latch;
    private final Random random;

    public UserTaskWorker(TaskManager taskManager, String username, 
                        String description, String category, CountDownLatch latch) {
        this.taskManager = taskManager;
        this.username = username;
        this.description = description;
        this.category = category;
        this.latch = latch;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(random.nextInt(500));
            Task task = taskManager.createTask(username, description, category);
            Thread.sleep(random.nextInt(300));
            
            if (task != null && random.nextBoolean()) {
                Thread.sleep(random.nextInt(200));
                taskManager.completeTask(task.getTaskId());
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }
}

public class CollaborativeToDoApp {
    
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Collaborative To-Do List Application (Java)");
        System.out.println("==================================================\n");

        TaskManager taskManager = new TaskManager();

        System.out.println("PART 1: Basic CRUD Operations Demo\n");
        demonstrateBasicOperations(taskManager);

        System.out.println("\nPART 2: Concurrent Operations Demo\n");
        demonstrateConcurrency(taskManager);

        taskManager.displayStatistics();
        
        System.out.println("Application completed successfully.");
    }

    private static void demonstrateBasicOperations(TaskManager taskManager) {
        System.out.println("User Registration");
        taskManager.registerUser("Alice");
        taskManager.registerUser("Bob");
        taskManager.registerUser("Charlie");

        System.out.println("\nCreating Tasks");
        taskManager.createTask("Alice", "Complete project proposal", "Work");
        taskManager.createTask("Alice", "Review code changes", "Work");
        taskManager.createTask("Alice", "Buy groceries", "Personal");
        taskManager.createTask("Bob", "Prepare presentation", "Work");
        taskManager.createTask("Bob", "Call dentist", "Personal");
        taskManager.createTask("Charlie", "Fix bug in authentication", "Work");

        System.out.println("\nViewing Tasks");
        taskManager.displayUserTasks("Alice");

        System.out.println("\nUpdating Tasks");
        taskManager.updateTaskDescription("TASK-0001", "Complete and submit project proposal");
        taskManager.updateTaskCategory("TASK-0003", "Shopping");

        System.out.println("\nCompleting Tasks");
        taskManager.completeTask("TASK-0001");
        taskManager.completeTask("TASK-0004");

        System.out.println("\nViewing Updated Tasks");
        taskManager.displayUserTasks("Alice");

        System.out.println("\nDeleting a Task");
        taskManager.deleteTask("TASK-0003");

        System.out.println("\nFinal Task List for Alice");
        taskManager.displayUserTasks("Alice");
    }

    private static void demonstrateConcurrency(TaskManager taskManager) {
        System.out.println("Starting concurrent operations with 5 threads\n");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        String[][] userTasks = {
            {"Alice", "Implement user authentication", "Work"},
            {"Bob", "Write unit tests", "Work"},
            {"Charlie", "Update documentation", "Work"},
            {"Alice", "Schedule team meeting", "Work"},
            {"Bob", "Code review for PR #123", "Work"}
        };

        for (int i = 0; i < userTasks.length; i++) {
            final int index = i;
            executor.submit(new UserTaskWorker(
                taskManager, 
                userTasks[index][0], 
                userTasks[index][1], 
                userTasks[index][2],
                latch
            ));
        }

        try {
            latch.await();
            System.out.println("\nAll concurrent operations completed successfully\n");
        } catch (InterruptedException e) {
            System.err.println("Error waiting for threads: " + e.getMessage());
        }

        executor.shutdown();

        System.out.println("Demonstrating Concurrent Updates");
        demonstrateConcurrentUpdates(taskManager);
    }

    private static void demonstrateConcurrentUpdates(TaskManager taskManager) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        executor.submit(() -> {
            try {
                Thread.sleep(100);
                taskManager.completeTask("TASK-0007");
                taskManager.completeTask("TASK-0009");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(150);
                taskManager.updateTaskDescription("TASK-0008", "Write comprehensive unit tests");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                Thread.sleep(200);
                taskManager.updateTaskCategory("TASK-0010", "Meeting");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
            System.out.println("\nConcurrent updates completed\n");
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }

        executor.shutdown();

        System.out.println("Tasks After Concurrent Updates");
        taskManager.displayUserTasks("Alice");
        taskManager.displayUserTasks("Bob");
    }
}