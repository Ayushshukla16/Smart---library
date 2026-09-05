# Library Management System

A **Java swing desktop application** which is a **CSE2006 – Programming in Java, 2nd Year B.Tech Project**.

The Library Management System that provides **Librarians** and **Members** a platform to manage book and member operations such as  searching, adding ,removing, issuing or returning books, and storing of information in text file.


## 📌 Project Title: **Library Management System**


## 📖 Overview

The **Library Management System** is a Java Swing based desktop application that carries out library operations through a Graphical User Interface(GUI).

The application has two types of users namely:

### 👨‍💼 Librarian

A Librarian is able to:

- Manage books
- Manage members
- Search books
- Add a book
- Remove a book
- View issued books
- Check in returned books

### 👨‍🎓 Member

A Member is able to:

- View available books
- View all books with availability status
- Search books
- Issue books
- View issued books
- View profile

The  java application has no external dependencies making it a standalone desktop application.However,the application stores information in text files namely:

- `books.txt`
- `members.txt`
- `issues.txt`


## 🚀 Java Concepts

The Library Management System project demonstrates the following Java Programming  concepts:

- Object-oriented
- Inheritance
- Abstraction
- Encapsulation
- Interfaces
- Polymorphism
- Method overriding
- Enums
- Collections
- Custom Exceptions
- File I/O
- Threads
- Synchronization


# ✨ Features

## 1. Welcome Screen

The welcome screen enables:

- Librarian Login
- Member Login
- Exit


## 2. Librarian Login

### The table below shows the default Librarian Login Details:

| Username | Password |
|---|---|
| `admin` | `admin123` |


## 3. Librarian Dashboard

The Librarian Dashboard enables:

- View all books
- Add a book
- Remove a book
- Search books
- Search by ID, title, author, or category
- View all members
- Register a new member
- View issued books
- Check-in a returned book
- Log out

A Book has the following attributes:

- Book ID
- Title
- Author
- Category
- Status

A member has the following attributes:

- Member ID 
- Name 
- Password
- Course/Department
- Issued Books

The Status of a book is either

- AVAILABLE
- ISSUED

## 4. Member Login

### The table belows shows existing Member Login Details

#### Member 1:

| Member ID | Password |
|---|---|
| `M001` | `member123` |

#### Member 2:

| Member ID | Password |
|---|---|
| `M002` | `member456` |

---

## 5. Member Dashboard

The Member dashboard enables:

- View available books
- View all books with availability status
- Search books
- Issue a book
- View issued books
- View profile
- Log out



# 📋 Borrowing Policies

The following policies govern the issuance of books:

- Only available books can be issued
- A member may only issue a maximum of **3 books**
- Issued books cannot be removed from the system
- Issued books can only be check-in(returned) through the **Check-in** option
- Exceptions are thrown when any of the policies are violated


# 💾 Storing of Information

Information is stored in the following files:

```text
books.txt
members.txt
issues.txt
```

However,in the case where the mentioned files are not found(on first launch),the application will create book.txt,members.txt and issues.txt with demo data.


# 🎨 GUI

The application uses Java Swing to implement a mordern Graphical User Interface(GUI).Some of the features of the GUI include:
### GUI Features

- Larger fonts for easier reading
- Different themes for the Librarian and Member dashboards
- Rounded buttons
- Hover and click animations
- Focus animations for text fields
- Zebra-stripe tables for easy reading
- Availability indicator
- Tabs for switching dashboard views


# 🧠 Concepts Demonstrated

The following concepts are demonstrated in the Library 
Management System:

- Classes and objects
- Constructors
- Encapsulation
- Abstraction
- Inheritance
- Polymorphism
- Method overriding
- Interfaces
- Enums
- `ArrayList` and List
- Custom Exceptions
- Exception Handling
- File I/O
- LocalDate
- Multithreading
- Synchronization
- Java Swing GUI


# 🛠 Technologies and Tools

The following Technologies and Tools were used to develop the 
Library Management System:

| Technology / Tool | Description |
|---|---|
| Java | Programming language used to develop the application |
| Java Swing | Library is used to implement Graphical User Interface(GUI) |
| AWT | Abstract Window Toolkit(AWT) is a GUI toolkit for Java|
| Java Collections | Contains `ArrayList` and `List` |
| File I/O | Reading and writing data/to files |
| LocalDate | Used to store the date of issue of the book |
| Multithreading | Used to carry out background audit tasks |
| Synchronization | Used to control concurrency of threads |
| Visual Studio Code |Default Text Editor for writing Java code|
| Extension Pack for Java |Used to develop Java applications in Visual Studio Code|
| External Dependencies | Not used |
| Database | Not used|


# 📁 Project Structure

The project structure is as described below:

```text
Library Management System/
│
├── LibraryManagementSystem.java
├── books.txt       # (generated)

├── members.txt     # (generated)

└── issues.txt      # (generated)
```
There is a need to ensure that the name of the public class matches the name of the Java file.

In this case,the name of the public class is 

```text
LibraryManagementSystem.java
```


# ✅ Requirements

The following are required to build and run the project:

1. **Java JDK**
2. **Visual Studio Code**
3. **Extension Pack for Java for Visual Studio Code**


# ▶️ Installing and Running the Project

## Step 1 — Open the Project

Open the project folder in **Visual Studio Code**.

## Step 2 — Check the File Name

Ensure that the Java file is named:

```text
LibraryManagementSystem.java
```

The file name should match the name of the public class.

## Step 3 — Open the Java File

Open the following in Visual Studio Code:

```text
LibraryManagementSystem.java
```


## Step 4 — Run the Application

Click on the **Run** button as shown below the `main()`

Alternatively,one can use the following key combinations:

- `F5`
- `Ctrl + F5`

After launching the application, the **Library Management System GUI** should appear.

> **Note:** When using Visual Studio Code with the Java Extension Pack, you do not need to manually run the `javac` and `java` commands.


# 🔐 Default Credentials

## Librarian

| Username | Password |
|---|---|
| `admin` | `admin123` |

## Member 1

| Member ID | Password |
|---|---|
| `M001` | `member123` |

## Member 2

| Member ID | Password |
|---|---|
| `M002` | `member456` |


# 🧪 Testing Procedure

## Test 1 — Successful Login as Librarian

### Procedure

1. Launch the application.
2. Click **Librarian Login** on the welcome screen.
3. Enter:
   - Username: `admin`
   - Password: `admin123`
4. Click **Login**.

### Expected Result

The Librarian dashboard should be displayed.


## Test 2 — Failed Login as Librarian

### Procedure

1. Open the Librarian Login screen.
2. Enter invalid credentials.
3. Click **Login**.

### Expected Result

An invalid login message should be displayed.


## Test 3 — View Books

### Procedure

1. Log in as a Librarian.
2. Open the **All Books** tab.
3. View the data displayed in the table.

### Expected Result

The table should display information about each book, including:

- Book ID
- Title
- Author
- Category
- Availability Status


## Test 4 — Add Book

### Procedure

1. Log in as a Librarian.
2. Open the **Add Book** panel.
3. Provide the following inputs:
   - Book ID: `B101`
   - Title: `The Great Gatsby`
   - Author: `F. Scott Fitzgerald`
   - Category: `Classics`
4. Click **Add Book**.

### Expected Result

The book should be added to the book list.


## Test 5 — Add Book with Duplicate ID

### Procedure

Try to add a book with an existing Book ID.

### Expected Result

The book should not be added, and an appropriate message should be displayed.


## Test 6 — Register Member

### Procedure

1. Open the Members dashboard.
2. Click **Register New Member**.
3. Provide the following inputs:
   - Member ID: `M003`
   - Name: `John Doe`
   - Password: `secret`
   - Department: `Computer Science`
4. Click **Register Member**.

### Expected Result

The new member should be successfully registered.


## Test 7 — Login as Member

### Procedure

1. Log out of the Librarian dashboard.
2. Open the Member Login screen.
3. Enter:
   - Member ID: `M001`
   - Password: `member123`
4. Click **Login**.

### Expected Result

The Member dashboard should be displayed.


## Test 8 — Search Book

### Procedure

1. Log in as either a Member or Librarian.
2. Enter a search query.
3. Click the search button.
4. Search using:
   - Book ID
   - Title
   - Author
   - Category

### Expected Result

Only books matching the search query should be displayed.


## Test 9 — Issue Book

### Procedure

1. Log in as Member 1.
2. Open the **All Books** tab.
3. Select an available book.
4. Click **Issue Selected Book**.
5. Open the **My Issued Books** tab.
6. Verify that the issued book appears in the table.

### Expected Result

The book status should change to:

```text
ISSUED
```



## Test 10 — Issue More Than 3 Books

### Procedure

Issue a total of 3 books as Member 1 and attempt to issue a 4th book.

### Expected Result

The 4th book should not be issued, and an appropriate message should be displayed.



## Test 11 — Return / Check-in Issued Book

### Procedure

1. Log in as a Librarian.
2. Open the **Issued Books** tab.
3. Select an issued book.
4. Click **Check In (Return)**.
5. Observe the status of the book.

### Expected Result

The status of the returned book should change to:

```text
AVAILABLE
```


## Test 12 — Remove Book

### Procedure

1. Log in as a Librarian.
2. Open the **All Books** tab.
3. Select an available book.
4. Click **Remove Book**.
5. Observe the contents of the table.
6. Repeat the process for an issued book.

### Expected Result

- Available books that are removed should no longer appear in the All Books table.
- Issued books should remain in the Issued Books table until they are returned.


## Test 13 — Persist Data

### Procedure

1. Add a book or register a new member.
2. Exit the application.
3. Run the application again.
4. Check the following files:

```text
books.txt
members.txt
issues.txt
```

### Expected Result

The files should reflect the data that was added or updated.


# 🎯 Expected Result

The application should provide a complete GUI-based Library Management System offering:

- Book management
- Member management
- Book search
- Book issuing
- Book returning
- Profile viewing
- Viewing issued books
- Borrowing policy enforcement
- Data persistence

Invalid operations should be handled using appropriate exceptions and user-friendly message displays.

# 📝 Notes

The following notes should be considered when usinv the application:

- Text files are used to store information instead of a database.
- The application has no external JAR files or database dependencies
- The source code has been implemented as a **single-file Java desktop application**.
- Exceptions are used to demonstrate handling of invalid operations.
- Threads have been used to provide concurrency support when issuing, returning, and removing books.
- Demo data is generated when the application is launched for the first time if the data file is not found



