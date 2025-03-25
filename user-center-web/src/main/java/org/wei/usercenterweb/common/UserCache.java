package org.wei.usercenterweb.common;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.wei.usercenterweb.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * 存储用户数据缓存
 *
 * @author JiangWeiWei
 */
@Component
public class UserCache {
    private final Map<String, User> userMap = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void refreshCache(List<User> users) {
        lock.writeLock().lock();
        try {
            userMap.clear();
            users.forEach(u -> userMap.put(u.getUserId(), u));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public User getUserById(String id) {
        lock.readLock().lock();
        try {
            return userMap.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 获取除当前用户的全部用户信息
     *
     * @param currentUserId 用户id
     * @return 除当前用户的全部用户信息
     */
    public List<User> getUserNotIdList(String currentUserId) {
        lock.readLock().lock();
        try {
            if (StringUtils.isBlank(currentUserId)) {
                return new ArrayList<>(userMap.values());
            }

            return userMap.values().stream().
                    filter(user -> !user.getUserId().equals(currentUserId))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}