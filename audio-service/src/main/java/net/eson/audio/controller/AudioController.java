package net.eson.audio.controller;

import net.eson.audio.model.Audio;
import net.eson.audio.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * @author Eson
 * @date 2025年03月31日 17:29
 */

@RestController
@RequestMapping("/audio")
public class AudioController {
    @Autowired
    private AudioService audioService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 查询所有音频记录
    @GetMapping
    public List<Audio> list() {
        return audioService.list();
    }

    // 根据ID查询单个音频记录
    @GetMapping("/{id}")
    public Audio getById(@PathVariable Long id) {
        return audioService.getById(id);
    }

    // 新增音频记录
    @PostMapping
    public boolean save(@RequestBody Audio audio) {
        return audioService.save(audio);
    }

    // 更新音频记录
    @PutMapping("/{id}")
    public boolean update(@PathVariable Long id, @RequestBody Audio audio) {
        audio.setId(id);  // 注意：确保实体类中有setId方法
        return audioService.updateById(audio);
    }

    @PutMapping("/play/{id}")
    public ResponseEntity<?> playAudio(@PathVariable Long id) {
        Audio audio = audioService.getById(id);
        if (audio == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("音频不存在");
        }

        // 获取当前用户 ID（假设从 JWT 解析）
        Long userId = getCurrentUserId();
        String playKey = "play_limit:user:" + userId;

        // 限制每日播放次数（例：每天最多 5 次）
//        Integer playCount = redisTemplate.opsForValue().get(playKey);
//        if (playCount != null && playCount >= 5) {
//            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("今日播放次数已达上限");
//        }

        // 更新播放次数（存入 Redis）
        redisTemplate.opsForValue().increment(playKey, 1);
        redisTemplate.expire(playKey, Duration.ofDays(1));

        // 记录总播放次数（存入 MySQL）
        String key = "play_count:" + id;
        redisTemplate.opsForValue().increment(key, 1); // Redis 累加
//        audioService.incrementPlayCount(id);
        // 返回音频 URL
        return ResponseEntity.ok(Map.of("url", audio.getUrl()));
    }

    // 删除音频记录
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return audioService.removeById(id);

    }

    /**
     * 获取最热音频排行榜
     */
    @GetMapping("/top")
    public ResponseEntity<List<Audio>> getTopAudios(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(audioService.getTopHotAudios(limit));
    }

    private Long getCurrentUserId() {
        // 假设从 JWT 解析用户 ID，临时用 1L 代替
        return 1L;
    }


}
