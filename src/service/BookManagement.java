package service;

import java.util.List;
import java.util.Scanner;

import dao.BookDAO;
import model.Book;

public class BookManagement {

    private Scanner sc = new Scanner(System.in);
    private BookDAO bookDAO = new BookDAO();

    // GUI delegator methods
    public List<Book> getAllBooks() {
        return bookDAO.getAllBooksList();
    }

    public List<Book> searchBooks(String search) {
        return bookDAO.searchBooksList(search);
    }

    public Book getBookById(int id) {
        return bookDAO.searchBook(id);
    }

    public void addBook(Book book) {
        bookDAO.addBook(book);
    }

    public void updateBook(Book book) {
        bookDAO.updateBook(book);
    }

    public void deleteBook(int id) {
        bookDAO.deleteBook(id);
    }

    // Add Book (Console version)
    public void addBook() {
        Book book = new Book();
        System.out.print("Enter Book Title: ");
        book.setTitle(sc.nextLine());

        System.out.print("Enter Author Name: ");
        book.setAuthor(sc.nextLine());

        System.out.print("Enter Category: ");
        book.setCategory(sc.nextLine());

        System.out.print("Enter Quantity: ");
        book.setQuantity(sc.nextInt());
        sc.nextLine(); // Consume newline

        bookDAO.addBook(book);
    }

    // View Books
    public void viewBooks() {
        bookDAO.viewBooks();
    }

    // Search Book
    public void searchBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        Book book = bookDAO.searchBook(id);
        if (book != null) {
            System.out.println("\n========== BOOK DETAILS ==========");
            System.out.println(book);
        } else {
            System.out.println("Book Not Found.");
        }
    }

    // Update Book
    public void updateBook() {
        Book book = new Book();
        System.out.print("Enter Book ID: ");
        book.setBookId(sc.nextInt());
        sc.nextLine();

        System.out.print("Enter New Title: ");
        book.setTitle(sc.nextLine());

        System.out.print("Enter New Author: ");
        book.setAuthor(sc.nextLine());

        System.out.print("Enter New Category: ");
        book.setCategory(sc.nextLine());

        System.out.print("Enter New Quantity: ");
        book.setQuantity(sc.nextInt());
        sc.nextLine();

        bookDAO.updateBook(book);
    }

    // Delete Book
    public void deleteBook() {
        System.out.print("Enter Book ID: ");
        int id = sc.nextInt();
        sc.nextLine();

        bookDAO.deleteBook(id);
    }

    public void showAvailableBooks() {
        bookDAO.showAvailableBooks();
    }
    
    public void showBookStatus() {
        bookDAO.showBookStatus();
    }
}