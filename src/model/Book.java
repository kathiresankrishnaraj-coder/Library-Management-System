package model;

public class Book {

    // Instance Variables
    private int bookId;
    private String title;
    private String author;
    private String category;
    private int quantity;
    private double price;

    // Default Constructor
    public Book() {

    }

    // Parameterized Constructor
    public Book(int bookId, String title, String author, String category, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.quantity = quantity;
    }

    // Getter Methods
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    // Setter Methods
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Display Book Information
    @Override
    public String toString() {
        return "Book ID : " + bookId +
               "\nTitle : " + title +
               "\nAuthor : " + author +
               "\nCategory : " + category +
               "\nQuantity : " + quantity;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}