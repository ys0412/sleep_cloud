package net.eson.audio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Eson
 * @date 2025年03月31日 17:50
 */
@Service
public class TaskService {

    @Autowired

    @Scheduled(fixedRate = 300000) // 每 5 分钟执行一次
    public void syncPlayCountToDB() {
        Set<String> keys = redisTemplate.keys("play_count:*");
        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long id = Long.valueOf(key.split(":")[1]);
            int count = redisTemplate.opsForValue().get(key);

            // 批量更新数据库
            audioService.lambdaUpdate()
                    .setSql("play_count = play_count + " + count)
                    .eq(Audio::getId, id)
                    .update();

            // 删除 Redis 里的缓存
            redisTemplate.delete(key);
        }
    }
}
    