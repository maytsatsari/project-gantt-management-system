# Java Gantt Management System

A software engineering university project developed in Java, focusing on project task scheduling, hierarchy, and Gantt chart representation with Excel export.

>  This was a team project developed by two members.  
> The code, architecture, and implementation shown here reflect the **significant contribution** of the maintainer of this repository.

---

##  Project Features

- Load tasks from `.txt`, `.xls`, `.xlsx`
- Support for top-level and subtask hierarchies
- Automatic calculation of duration, cost, and effort
- Gantt output via Excel (Apache POI)
- Predefined and custom style formatting
- CLI-based testing via naive clients

---

##  Project Structure

```
src/
├── main/
│   └── java/
│       ├── app/         # Application layer (entry point, controllers)
│       │   └── naive/   # Sample clients for testing
│       ├── dom/         # Domain model (Task, Style, etc.)
│       ├── service/     # Business logic and operations
│       └── util/        # Shared helpers/utilities
│
├── test/                # Unit test code
│   └── java/test/       # JUnit or console-based tests
│
└── resources/
    ├── input/           # Sample input files
    └── output/          # Generated output files
```

##  Notes

- `MainController.java`: Lines **522** and **815** contain output paths as comments for specific run outputs.
- Unit tests or helper clients live in `src/test/java/test/`.


---

##  Author

GitHub repo maintained by **[maytsatsari](https://github.com/maytsatsari)**  
This repository reflects her personal contributions in architecture, logic, and code delivery.

