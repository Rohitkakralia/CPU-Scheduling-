# CPU-Scheduling-
# FCFS Scheduling API (Spring Boot + MongoDB)

This project implements a **First Come First Serve (FCFS)** CPU scheduling algorithm using **Spring Boot** and **MongoDB**. It provides a RESTful API to compute scheduling metrics such as **Completion Time**, **Waiting Time**, and **Turnaround Time** for a list of processes.
 
---

## 🔧 Tech Stack

- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **MongoDB**
- **RESTful API**
- **Postman/Frontend App (for testing requests)**

---

## 📦 Project Structure

src/
├── controllers/
│ └── ProcessController.java
├── entities/
│ ├── Process.java
│ └── ScheduleResult.java
├── services/
│ └── SchedulerService.java
  |__ SchedulerServiceForNPSJF.java
  |__ SchedulerServiceForPSJF.java
└── CPUSchedulingApplication.java


---

## 📌 Features

- Accepts a list of processes with **arrival time** and **burst time**
- Calculates and returns:
  - Completion Time (CT)
  - Waiting Time (WT)
  - Turnaround Time (TAT)
- Stores and sorts processes using a PriorityQueue based on arrival time
- CORS enabled for frontend integration

---

## 🧪 API Usage

### Endpoint

POST /api/scheduler/fcfs

### Request Body

```json
[
  {
    "name": "P1",
    "arivalTime": 0,
    "burstTime": 5
  },
  {
    "name": "P2",
    "arivalTime": 1,
    "burstTime": 3
  }
]

Response Body
[
  {
    "name": "P1",
    "arivalTime": 0,
    "burstTime": 5,
    "criticalTime": 5,
    "turnAroundTime": 5,
    "waitingTime": 0
  },
  {
    "name": "P2",
    "arivalTime": 1,
    "burstTime": 3,
    "criticalTime": 8,
    "turnAroundTime": 7,
    "waitingTime": 4
  }
]

▶️ Running the Project
Prerequisites
  1.Java 17 or later
  2.Maven
  3.MongoDB running locally or through MongoDB Atlas

Steps
    1.Clone the repo: git clone https://github.com/your-username/your-repo-name.git
                   cd your-repo-name

    2.Start MongoDB (local or connect to Atlas)

    3.Build and run the application: mvn spring-boot:run
    
    4.Test the API via Postman or your frontend

🧹 To Do
Add other scheduling algorithms (SJF, Round Robin, etc.)

Add database persistence for scheduled results

Add frontend UI for interaction

🧑‍💻 Author
Rohit kakralia

LinkedIn:https://www.linkedin.com/in/rohit-kakralia-a35046251/

GitHub:https://github.com/Rohitkakralia

📄 License
This project is licensed under the MIT License.

---

Would you like me to generate this as a downloadable file or help you include setup instructions for MongoDB as well?

             
