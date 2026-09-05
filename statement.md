# Library Management System

## Problem Statement

Maintaining records of the library manually can be a tedious task. Various activities like keeping track of books, maintaining the list of members, searching for a book, issuing and returning books, etc., need to be done in an organized manner.

The Library Management System aims to provide a simple and intuitive Java Swing GUI application to manage the basic tasks needed in a library. It keeps records of books, members, and issued books while managing the availability and limits of borrowing the books. All the data is stored in text files so that the records can be saved and loaded by restarting the app.

## Scope of the Project

The Library Management System will include the following features within its scope:

- Keeping records of the books including adding and removing books
- Keeping track of the library members
- Adding new members to the library
- Searching for books by the book ID, title, author, and category
- Issuing the available books to the members
- Returning the issued books and updating their status
- Keeping track of issued books and their issue date
- Limiting the number of books that can be borrowed by a member
- The login and dashboard system for both the librarian and members
- Storing and loading the books, members, and issued books data in text files

## Target Users

The target users of the Library Management System are as follows:

### Librarian

A person managing the library. They have the following features:

- Logging in to the system
- Adding new books
- Deleting available books
- Registering new members
- Viewing the records of books and their availability
- Searching for books
- Viewing the details of members
- Viewing the issued book records
- Returning issued books

### Library Members

Any person registered to take a book from the library. They have the following features:

- Log in to the system with their member ID and password
- View the list of all books that are available and issued
- Search for books
- Issue the available books limited to the book issuing limit
- View their issued books and issue dates
- View their profile including the department and book issuing limit

## High-Level Features

- **User Authentication:** A separate login system for both the librarian and members.
- **Book Management:** Add books, delete books, search books, and view books.
- **Member Management:** Add members, delete members, and view members.
- **Book Search:** Search books by ID, title, author, and category.
- **Book Issuing:** Issue books to the members.
- **Book Return:** Return the issued books and update their status to be available for issue.
- **Borrowing Limit:** Limiting the number of books issued to a member.
- **Issue Tracking:** View issued books and the date they were issued.
- **Book Status Management:** Keeping track of the availability of books.
- **Data Persistence:** Storing the books, members, and issued books data in separate text files and loading them back.
- **Graphical User Interface:** Providing a Java Swing interface with different dashboards for the librarian and members.
- **Error Handling:** Handling errors that occur while logging in, book searching, issuing books, returning books, etc.
