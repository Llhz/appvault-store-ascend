package com.appvault.controller;

import com.appvault.model.Notification;
import com.appvault.model.User;
import com.appvault.service.NotificationService;
import com.appvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/user/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<?> getUnreadCount(Authentication auth) {
        User user = getUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Unauthorized"));
        }
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(Collections.singletonMap("count", count));
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getNotifications(Authentication auth) {
        User user = getUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Unauthorized"));
        }
        List<Notification> all = notificationService.getNotifications(user.getId());
        List<Notification> recent = all.size() > 20 ? all.subList(0, 20) : all;

        List<Map<String, Object>> result = new ArrayList<>();
        for (Notification n : recent) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", n.getId());
            map.put("message", n.getMessage());
            map.put("link", n.getLink());
            map.put("read", n.isRead());
            map.put("createdAt", n.getCreatedAt().toString());
            result.add(map);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/read")
    @ResponseBody
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    @PostMapping("/read-all")
    @ResponseBody
    public ResponseEntity<?> markAllAsRead(Authentication auth) {
        User user = getUser(auth);
        if (user == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Unauthorized"));
        }
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(Collections.singletonMap("success", true));
    }

    private User getUser(Authentication auth) {
        if (auth == null) return null;
        return userService.findByEmail(auth.getName()).orElse(null);
    }
}
