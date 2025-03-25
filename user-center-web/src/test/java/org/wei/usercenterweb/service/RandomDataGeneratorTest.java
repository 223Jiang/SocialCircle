package org.wei.usercenterweb.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wei.usercenterweb.domain.User;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 随机生成30万条数据
 */
@Slf4j
@SpringBootTest()
public class RandomDataGeneratorTest {

    @Resource
    private UserService userService;

    // 分批大小
    private static final int BATCH_SIZE = 50000;
    private static final int TOTAL_RECORDS = 300000;
    // 预定义的名字列表
    private static final List<String> NAMES = Arrays.asList("小明", "小红", "小刚", "小丽", "阿强", "阿花");
    // 预定义的技能池
    private static final List<String> SKILLS = Arrays.asList("java", "python", "大数据", "机器学习", "前端开发", "后端开发", "算法");
    // 预定义的个人简介模板
    private static final List<String> BIOS = Arrays.asList(
            "热爱编程，喜欢探索新技术。",
            "每天进步一点点，未来会更好。",
            "代码是我的第二语言。",
            "梦想成为一名优秀的工程师。"
    );

    @Test
    public void test() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                4, 4, 60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(4));

        Random random = new Random();
        List<User> lists = new ArrayList<>();
        for (int i = 0; i < TOTAL_RECORDS; i++) {
            // 生成每列的数据
            String username = getRandomElement(NAMES);
            final String passwordHash = "$2a$10$83YqiE0BPLkoFz.28/T.zeGnAF6HNTxv.BKZ9cvrjcxhQ4myvzGtK";
            String email = username + "@mail.com";
            String phone = "131" + String.format("%08d", random.nextInt(100000000));
            String skills = generateRandomSkills(SKILLS, random);
            String bio = getRandomElement(BIOS);
            String createTime = generateRandomTimestamp(random);

            User user = new User();
            user.setUserName(username);
            user.setUserPassword(passwordHash);
            user.setUserEmail(email);
            user.setUserPhone(phone);
            user.setTags(skills);
            user.setUserDescription(bio);
            user.setCreateTime(createTime);

            lists.add(user);
        }

        try {
            for (int i = 0; i < TOTAL_RECORDS; i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, TOTAL_RECORDS);

                List<User> subList = new ArrayList<>(lists.subList(i, end));

                // 提交任务到线程池
                threadPoolExecutor.submit(() -> {
                    try {
                        userService.saveBatch(subList); // 批量插入数据
                    } catch (Exception e) {
                        // 捕获异常并记录日志
                        log.error("Error occurred while saving batch: " + e.getMessage());
                    }
                });
            }
        } finally {
            // 关闭线程池
            threadPoolExecutor.shutdown();
            try {
                if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPoolExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPoolExecutor.shutdownNow();
            }
        }
    }

    // 从列表中随机选择一个元素
    private static <T> T getRandomElement(List<T> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    // 生成随机技能列表（JSON 格式）
    private static String generateRandomSkills(List<String> skills, Random random) {
        int count = random.nextInt(skills.size()) + 1; // 至少选一项
        List<String> selectedSkills = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            selectedSkills.add(getRandomElement(skills));
        }
        return "[" + selectedSkills.stream().map(skill -> "\"" + skill + "\"").collect(Collectors.joining(",")) + "]";
    }

    // 生成随机时间戳
    private static String generateRandomTimestamp(Random random) {
        LocalDateTime start = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 31, 23, 59);
        long startEpoch = start.toEpochSecond(ZoneOffset.UTC);
        long endEpoch = end.toEpochSecond(ZoneOffset.UTC);
        long randomEpoch = startEpoch + (long) (random.nextDouble() * (endEpoch - startEpoch));
        return LocalDateTime.ofEpochSecond(randomEpoch, 0, ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}