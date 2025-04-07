package net.eson.audio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.eson.audio.model.Audio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

/**
 * @author Eson
 * @date 2025年04月01日 10:37
 */
@Configuration
public class RedisConfig {

    /**
     * 适用于存储 String 类型的 RedisTemplate
     */
    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

    /**
     * 适用于存储 Object 类型的 RedisTemplate
     */
    @Bean(name = "objectRedisTemplate")
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 解决 LocalDateTime 不能序列化的问题
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 让 Jackson 处理 LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // 避免时间戳格式

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Key 采用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());
        // Value 采用 JSON 序列化（支持 LocalDateTime）
        template.setValueSerializer(serializer);
        // Hash Key 采用 String 序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        // Hash Value 采用 JSON 序列化（支持 LocalDateTime）
        template.setHashValueSerializer(serializer);

        return template;
    }


    /**
     * 适用于存储 Integer 类型的 RedisTemplate
     */
    @Bean(name = "integerRedisTemplate")
    public RedisTemplate<String, Integer> integerRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 采用 String 序列化
        template.setKeySerializer(new StringRedisSerializer());
        // Value 采用 String 序列化（整数存储时也可以用 JSON，但这里用 String）
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }

//    @Bean
//    public RedisTemplate<String, List<Audio>> audioRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, List<Audio>> template = new RedisTemplate<>();
//        template.setConnectionFactory(redisConnectionFactory);
//
//        // 使用 JSON 序列化
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//
//        template.afterPropertiesSet();
//        return template;
//    }
}
    