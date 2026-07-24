package service;

import java.sql.Date;
import java.util.List;
import java.util.Scanner;

import dao.ReservationDAO;
import model.Reservation;

public class ReservationManagement {

    Scanner sc = new Scanner(System.in);
    ReservationDAO dao = new ReservationDAO();

    // GUI delegators
    public List<Reservation> getAllReservations() {
        return dao.getAllReservationsList();
    }

    public void reserveBook(Reservation reservation) {
        dao.reserveBook(reservation);
    }

    public void cancelReservation(int id) {
        dao.cancelReservation(id);
    }

    // Console methods
    public void reserveBook(){
        Reservation reservation = new Reservation();
        System.out.print("Enter Student ID : ");
        reservation.setStudentId(sc.nextInt());

        System.out.print("Enter Book ID : ");
        reservation.setBookId(sc.nextInt());

        reservation.setReservationDate(new Date(System.currentTimeMillis()));
        reservation.setStatus("Waiting");

        dao.reserveBook(reservation);
    }

    public void viewReservations(){
        dao.viewReservations();
    }

    public void cancelReservation(){
        System.out.print("Enter Reservation ID : ");
        int id = sc.nextInt();
        dao.cancelReservation(id);
    }
}