# TaskHero

Project Specifications: **TaskHero - Gamified Task Manager**

This document describes the specifications for the final project of the Mobile Device Programming course.

## 1. Group Members
- Miguel dos Santos Flores

## 2. Software Overview
TaskHero is an Android application designed to help users manage their daily tasks in a more motivating and interactive way. The main difference from a common task manager is the introduction of "gamification" elements: users earn points (score) for each completed task, turning productivity into a personal challenge. The application will require registration and login, and each user will have their own profile with a photo and score.

## 3. Roles
- **User**: The only role in the system. After registering and logging in, the user can create, view, edit, mark as completed, and delete their own tasks. They can also view their profile, including their photo and total accumulated score.

## 4. Functional Requirements
- **Allowed Users**:
  - Only registered users will be able to access the main area of the application to manage tasks.

- **What each user can do**:
  - Register in the system by providing name, email, password, and a profile picture.
  - Log in with email and password.
  - Create new tasks (with title, description, and due date/time).
  - View the list of pending and completed tasks.
  - Edit the details of an existing task.
  - Mark a task as "completed," which will generate points.
  - Delete a task.
  - View and edit their profile (photo, name and email).

- **Required Inputs**:
  - **Registration**: Name, email, password, and user photo.
  - **Login**: Email and password.
  - **Task Creation**: Title (text), description (text), due date and time.

- **Processing performed by the app**:
  - The application will store the user's **password hash** to ensure security.
  - The app will **store the user's photo** associated with their profile.
  - The system will **associate activities (tasks) with the users** who created them.
  - When a task is marked as completed, the app will **calculate and store the user's score**, updating their total score.
  - The app will use **alarms and notifications** to alert the user about task deadlines.

- **Application Reports and Outputs**:
  - Main screen with the user's task list, separated by pending and completed.
  - Profile screen displaying the user's photo, name, and total score.
  - Device notifications to remind the user of a task that is nearing its due date.
  - Error messages for invalid inputs (e.g., empty login/registration fields).
