class Task {
  constructor(taskId, description, category, assignedUser) {
    this.taskId = taskId;
    this.description = description;
    this.category = category;
    this.assignedUser = assignedUser;
    this.completed = false;
    this.createdAt = new Date();
    this.updatedAt = new Date();
  }

  setDescription(newDescription) {
    this.description = newDescription;
    this.updatedAt = new Date();
  }

  setCategory(newCategory) {
    this.category = newCategory;
    this.updatedAt = new Date();
  }

  setCompleted(status) {
    this.completed = status;
    this.updatedAt = new Date();
  }

  getStatus() {
    return this.completed ? "Completed" : "Pending";
  }

  formatDate(date) {
    return date.toISOString().replace("T", " ").substring(0, 19);
  }

  toDetailedString() {
    return `
--------------------------------------------
Task ID     : ${this.taskId}
User        : ${this.assignedUser}
Description : ${this.description}
Category    : ${this.category}
Status      : ${this.getStatus()}
Created     : ${this.formatDate(this.createdAt)}
Updated     : ${this.formatDate(this.updatedAt)}
--------------------------------------------`;
  }

  toString() {
    return `Task[ID=${this.taskId}, User=${this.assignedUser}, Description=${this.description}, Category=${this.category}, Status=${this.getStatus()}]`;
  }
}

class User {
  constructor(username) {
    this.username = username;
    this.taskIds = [];
    this.createdAt = new Date();
  }

  addTaskId(taskId) {
    if (!this.taskIds.includes(taskId)) this.taskIds.push(taskId);
  }

  removeTaskId(taskId) {
    this.taskIds = this.taskIds.filter(id => id !== taskId);
  }

  hasTask(taskId) {
    return this.taskIds.includes(taskId);
  }

  getTaskCount() {
    return this.taskIds.length;
  }

  toString() {
    return `User[username=${this.username}, tasks=${this.taskIds.length}]`;
  }
}

class TaskManager {
  constructor() {
    this.tasks = new Map();
    this.users = new Map();
    this.taskCounter = 1;
  }

  async registerUser(username) {
    if (this.users.has(username)) {
      console.log(`User '${username}' already exists.`);
      return null;
    }
    const user = new User(username);
    this.users.set(username, user);
    console.log(`User '${username}' registered successfully.`);
    return user;
  }

  async createTask(username, description, category) {
    const user = this.users.get(username);
    if (!user) {
      console.log(`User '${username}' not found. Please register first.`);
      return null;
    }

    const taskId = `TASK-${String(this.taskCounter++).padStart(4, "0")}`;
    const task = new Task(taskId, description, category, username);
    this.tasks.set(taskId, task);
    user.addTaskId(taskId);

    console.log(`Task '${taskId}' created for user '${username}'.`);
    return task;
  }

  async updateTaskDescription(taskId, newDescription) {
    const task = this.tasks.get(taskId);
    if (!task) {
      console.log(`Task '${taskId}' not found.`);
      return false;
    }
    task.setDescription(newDescription);
    console.log(`Task '${taskId}' description updated.`);
    return true;
  }

  async updateTaskCategory(taskId, newCategory) {
    const task = this.tasks.get(taskId);
    if (!task) {
      console.log(`Task '${taskId}' not found.`);
      return false;
    }
    task.setCategory(newCategory);
    console.log(`Task '${taskId}' category updated to '${newCategory}'.`);
    return true;
  }

  async completeTask(taskId) {
    const task = this.tasks.get(taskId);
    if (!task) {
      console.log(`Task '${taskId}' not found.`);
      return false;
    }
    task.setCompleted(true);
    console.log(`Task '${taskId}' marked as completed.`);
    return true;
  }

  async uncompleteTask(taskId) {
    const task = this.tasks.get(taskId);
    if (!task) {
      console.log(`Task '${taskId}' not found.`);
      return false;
    }
    task.setCompleted(false);
    console.log(`Task '${taskId}' marked as pending.`);
    return true;
  }

  async deleteTask(taskId) {
    const task = this.tasks.get(taskId);
    if (!task) {
      console.log(`Task '${taskId}' not found.`);
      return false;
    }
    this.tasks.delete(taskId);

    const user = this.users.get(task.assignedUser);
    if (user) user.removeTaskId(taskId);

    console.log(`Task '${taskId}' deleted.`);
    return true;
  }

  getUserTasks(username) {
    const user = this.users.get(username);
    if (!user) return [];
    return user.taskIds.map(id => this.tasks.get(id)).filter(Boolean);
  }

  getTasksByCategory(category) {
    return [...this.tasks.values()].filter(
      task => task.category.toLowerCase() === category.toLowerCase()
    );
  }

  getPendingTasks(username) {
    return this.getUserTasks(username).filter(task => !task.completed);
  }

  getCompletedTasks(username) {
    return this.getUserTasks(username).filter(task => task.completed);
  }

  getAllTasks() {
    return [...this.tasks.values()];
  }

  getAllUsers() {
    return [...this.users.values()];
  }

  displayUserTasks(username) {
    const userTasks = this.getUserTasks(username);
    if (userTasks.length === 0) {
      console.log(`\nNo tasks found for user '${username}'.\n`);
      return;
    }
    console.log(`\n==================================================`);
    console.log(`Tasks for User: ${username}`);
    console.log(`==================================================`);
    userTasks.forEach(task => console.log(task.toDetailedString()));
  }

  displayStatistics() {
    const totalUsers = this.users.size;
    const totalTasks = this.tasks.size;
    const completed = [...this.tasks.values()].filter(t => t.completed).length;
    const pending = totalTasks - completed;

    console.log(`\n==================================================`);
    console.log(`System Statistics`);
    console.log(`==================================================`);
    console.log(`Total Users: ${totalUsers}`);
    console.log(`Total Tasks: ${totalTasks}`);
    console.log(`Completed Tasks: ${completed}`);
    console.log(`Pending Tasks: ${pending}`);
    console.log(`==================================================\n`);
  }
}

async function simulateUserAction(taskManager, username, description, category, delay) {
  await new Promise(res => setTimeout(res, delay));
  const task = await taskManager.createTask(username, description, category);
  if (task && Math.random() > 0.5) {
    await new Promise(res => setTimeout(res, delay / 2));
    await taskManager.completeTask(task.taskId);
  }
}

async function main() {
  console.log("==================================================");
  console.log("  Collaborative To-Do List Application (JavaScript)");
  console.log("==================================================\n");

  const taskManager = new TaskManager();

  console.log("PART 1: Basic CRUD Operations Demo\n");
  await taskManager.registerUser("Alice");
  await taskManager.registerUser("Bob");
  await taskManager.registerUser("Charlie");

  await taskManager.createTask("Alice", "Complete project proposal", "Work");
  await taskManager.createTask("Alice", "Review code changes", "Work");
  await taskManager.createTask("Alice", "Buy groceries", "Personal");
  await taskManager.createTask("Bob", "Prepare presentation", "Work");
  await taskManager.createTask("Bob", "Call dentist", "Personal");
  await taskManager.createTask("Charlie", "Fix bug in authentication", "Work");

  console.log("\nViewing Tasks");
  taskManager.displayUserTasks("Alice");

  console.log("\nUpdating Tasks");
  await taskManager.updateTaskDescription("TASK-0001", "Complete and submit project proposal");
  await taskManager.updateTaskCategory("TASK-0003", "Shopping");

  console.log("\nCompleting Tasks");
  await taskManager.completeTask("TASK-0001");
  await taskManager.completeTask("TASK-0004");

  console.log("\nViewing Updated Tasks");
  taskManager.displayUserTasks("Alice");

  console.log("\nDeleting a Task");
  await taskManager.deleteTask("TASK-0003");

  console.log("\nFinal Task List for Alice");
  taskManager.displayUserTasks("Alice");

  console.log("\nPART 2: Concurrent Operations Demo\n");

  const tasks = [
    simulateUserAction(taskManager, "Alice", "Implement user authentication", "Work", 500),
    simulateUserAction(taskManager, "Bob", "Write unit tests", "Work", 300),
    simulateUserAction(taskManager, "Charlie", "Update documentation", "Work", 400),
    simulateUserAction(taskManager, "Alice", "Schedule team meeting", "Work", 350),
    simulateUserAction(taskManager, "Bob", "Code review for PR #123", "Work", 250)
  ];

  await Promise.all(tasks);

  console.log("\nAll concurrent actions completed\n");

  console.log("\nTasks After Concurrent Updates");
  taskManager.displayUserTasks("Alice");
  taskManager.displayUserTasks("Bob");

  taskManager.displayStatistics();

  console.log("Application completed successfully.");
}

main();
