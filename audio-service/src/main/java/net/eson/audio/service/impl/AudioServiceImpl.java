package net.eson.audio.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.eson.audio.entity.Audio;
import net.eson.audio.mapper.AudioMapper;
import net.eson.audio.service.AudioService;
import org.springframework.stereotype.Service;

/**
 * @author Eson
 * @date 2025年03月31日 17:35
 */
@Service
public class AudioServiceImpl extends ServiceImpl<AudioMapper, Audio> implements AudioService {
}
    