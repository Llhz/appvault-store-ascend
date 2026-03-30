package com.appvault.service;

import com.appvault.model.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Long userId, String message, String link);
    List<Notification> getNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId);
    void markAllAsRead(Long userId);
}
