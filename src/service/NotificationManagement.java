package service;

import java.util.List;
import dao.NotificationDAO;
import model.Notification;

public class NotificationManagement {

    NotificationDAO dao = new NotificationDAO();

    // GUI delegators
    public List<Notification> getAllNotifications() {
        return dao.getAllNotificationsList();
    }

    public int getUnreadCount() {
        return dao.getUnreadCount();
    }

    public void addNotification(String message) {
        dao.addNotification(message);
    }

    public void deleteNotification(int id) {
        dao.deleteNotification(id);
    }

    public void markAllAsRead() {
        dao.markAllAsRead();
    }

    // Console method
    public void showNotifications() {
        dao.viewNotifications();
        dao.markAllAsRead();
    }
}