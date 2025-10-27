# CollaborativeToDoApp


**Collaborative To-Do App
**Project Overview


Developed for MSCS-632 – Advanced Programming Languages

Implemented in Java and JavaScript

Purpose: Compare how both languages handle concurrency, data management, and code structure

Focuses on:

Multithreading in Java

Asynchronous programming in JavaScript

Object-oriented design principles

Project Objectives

Demonstrate the same application logic using two languages

Compare static and dynamic typing systems

Apply object-oriented programming principles

Evaluate concurrency models and their performance

Observe syntax and implementation differences

Core Features

User registration and tracking

Task creation, update, and deletion

Task assignment by user

Categorization of tasks (Work, Personal, Shopping)

Marking tasks as completed or pending

Viewing and managing user-specific tasks

Displaying system statistics (users, tasks, completion status)

Simulating multiple users performing actions concurrently

Concurrency Models

Java:

Uses threads for real concurrency

Implements synchronization with synchronized methods

Utilizes ExecutorService and CountDownLatch for thread coordination

JavaScript:

Uses async/await and Promises for concurrency

Runs asynchronously on a single thread using the event loop

Simulates parallel execution using non-blocking I/O

Languages and Tools

Java JDK 8 or higher

Node.js runtime environment

Command-line interface for both versions

GitHub repository for collaboration and version control

Setup and Execution
Java Version

Install JDK 8 or later

Open terminal and navigate to the project folder

Compile using javac CollaborativeToDoApp.java

Run using java CollaborativeToDoApp

Features displayed:

User registration

CRUD operations

Multithreading demo

Statistics display

JavaScript Version

Install Node.js

Open terminal and navigate to the project directory

Run using node CollaborativeToDoApp.js

Features displayed:

User creation

Asynchronous task operations

Simulated concurrency

System summary output

Architecture Overview
Java Implementation

Task.java – Defines task details and structure

User.java – Manages user data and task references

TaskManager.java – Controls CRUD operations and concurrency

UserTaskWorker.java – Simulates user activity using threads

CollaborativeToDoApp.java – Main program entry point

JavaScript Implementation

Task – Defines individual tasks with timestamps

User – Stores user information and task list

TaskManager – Handles task creation and management using async functions

simulateUserAction() – Simulates concurrent operations

main() – Runs program flow and output

Example Operations

Register users (e.g., Alice, Bob, Charlie)

Create multiple tasks for each user

Update and mark tasks as completed

Delete unnecessary tasks

View pending and completed tasks separately

Display overall system statistics

Run simulated concurrent operations

Key Learnings

Java’s thread-based model provides real parallelism and strict typing

JavaScript’s asynchronous model offers simplicity and flexibility

Both languages support object-oriented principles effectively

Typing discipline influences error handling and code design

Similar functionality achieved through different paradigms

Understanding concurrency helps design scalable applications

Project Deliverables

CollaborativeToDoApp.java – Java code implementation

CollaborativeToDoApp.js – JavaScript code implementation

CollaborativeToDoApp_Report

README.md – Documentation file

GitHub repository containing both versions
