package com.sms.sender.service;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.HashSet;

@Service
public class RedisService {

    private Set<String> blockedUsers = new HashSet<>();

    public RedisService() {
        blockedUsers.add("blocked-user"); // example blocked user
    }

    public boolean isBlocked(String userId) {
        return blockedUsers.contains(userId);
    }
}