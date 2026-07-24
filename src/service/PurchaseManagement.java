package service;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import dao.PurchaseDAO;
import model.Purchase;

public class PurchaseManagement {

    Scanner sc = new Scanner(System.in);
    private PurchaseDAO dao = new PurchaseDAO();

    // GUI delegators
    public List<Purchase> getAllPurchases() {
        return dao.getAllPurchasesList();
    }

    public void purchaseBook(Purchase purchase) {
        dao.purchaseBook(purchase);
    }

    // Console methods
    public void purchaseBook() {
        Purchase purchase = new Purchase();
        System.out.print("Enter Book ID : ");
        purchase.setBookId(sc.nextInt());
        sc.nextLine();

        System.out.print("Enter Supplier Name : ");
        purchase.setSupplierName(sc.nextLine());

        System.out.print("Enter Quantity : ");
        purchase.setQuantity(sc.nextInt());

        System.out.print("Enter Price Per Book : ");
        purchase.setPricePerBook(sc.nextDouble());

        purchase.setTotalAmount(purchase.getQuantity() * purchase.getPricePerBook());
        purchase.setPurchaseDate(new Date(System.currentTimeMillis()));

        dao.purchaseBook(purchase);
    }

    public void viewPurchaseHistory() {
        dao.viewPurchaseHistory();
    }
}