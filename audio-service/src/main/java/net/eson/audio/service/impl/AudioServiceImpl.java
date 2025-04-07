package net.eson.audio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.eson.audio.model.Audio;
import net.eson.audio.mapper.AudioMapper;
import net.eson.audio.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * @author Eson
 * @date 2025年03月31日 17:35
 */
@Service
public class AudioServiceImpl extends ServiceImpl<AudioMapper, Audio> implements AudioService {

    @Autowired
    @Qualifier("objectRedisTemplate")
    private RedisTemplate<String, Object> objectRedisTemplate;

    private static final String HOT_AUDIO_KEY = "hot_audio";

    @Override
    public void incrementPlayCount(long id) {
        this.lambdaUpdate()
                .setSql("play_cout = play_count + 1")
                .eq(Audio::getId,id)
                .update();

    }

    @Override
    public List<Audio> getTopHotAudios(int limit) {
        Object cachedData = objectRedisTemplate.opsForValue().get(HOT_AUDIO_KEY);
        List<Audio> audioList = cachedData instanceof List ? (List<Audio>) cachedData : null;
        if (audioList == null){
            List<Audio> resList = this.lambdaQuery()
                    .orderByDesc(Audio::getPlayCount)
                    .last("LIMIT " + limit)
                    .list();
            objectRedisTemplate.opsForValue().set(HOT_AUDIO_KEY,resList, Duration.ofMinutes(10));
        }
        return audioList;
    }
}
    