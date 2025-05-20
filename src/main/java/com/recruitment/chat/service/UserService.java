package com.recruitment.chat.service;

import com.recruitment.chat.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    
    private final Map<String, User> onlineUsers = new HashMap<>();
    
    public void addUser(User user) {
        onlineUsers.put(user.getId(), user);
    }
    
    public void removeUser(String userId) {
        onlineUsers.remove(userId);
    }
    
    public List<User> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }
    
    public User getUserById(String userId) {
        return onlineUsers.get(userId);
    }
}  