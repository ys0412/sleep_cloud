package net.eson.audio.service;

import net.eson.audio.model.Audio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eson
 * @date 2025年03月31日 17:50
 */
@Service
public class TaskService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    @Qualifier("objectRedisTemplate")
    private RedisTemplate<String, Object> objectRedisTemplate;

    @Autowired
    AudioService audioService;

    private static final String HOT_AUDIO_KEY = "hot_audio";

    @Scheduled(fixedRate = 300000) // 每 5 分钟执行一次
    public void syncPlayCountToDB() {
        Set<String> keys = redisTemplate.keys("play_count:*");
        if (keys == null || keys.isEmpty()) return;

        Map<Long, Integer> playCountMap = new HashMap<>();

        for (String key : keys) {
            String[] parts = key.split(":");
            if (parts.length < 2) continue; // 避免 key 不符合规范

            Long id;
            try {
                id = Long.valueOf(parts[1]); // 获取 ID
            } catch (NumberFormatException e) {
                continue; // 忽略不合法 key
            }

            Object value = redisTemplate.opsForValue().get(key);
            int count = 0;
            if (value instanceof Number) {
                count = ((Number) value).intValue();
            } else if (value instanceof String) {
                try {
                    count = Integer.parseInt((String) value);
                } catch (NumberFormatException ignored) {
                    continue; // 忽略非数值数据
                }
            }

            playCountMap.put(id, playCountMap.getOrDefault(id, 0) + count); // 累加相同 ID
        }
        if (!playCountMap.isEmpty()) {
            for (Map.Entry<Long, Integer> entry : playCountMap.entrySet()) {
                audioService.lambdaUpdate()
                        .setSql("play_count = play_count + " + entry.getValue())
                        .eq(Audio::getId, entry.getKey())
                        .update();
            }

            // 删除 Redis 里的缓存
            redisTemplate.delete(keys);
        }
    }

    // 每小时刷新最热音频排行缓存
    @Scheduled(fixedRate = 3600000) // 每小时刷新一次，单位是毫秒
    public void refreshHotAudioCache() {
        System.out.println("Refreshing hot audio cache...");

        // 查询数据库并更新最热音频缓存
        List<Audio> hotAudios = audioService.getTopHotAudios(10); // 获取前 10 条
        objectRedisTemplate.opsForValue().set(HOT_AUDIO_KEY, hotAudios, Duration.ofMinutes(10));

        System.out.println("Hot audio cache refreshed.");
    }

}
    