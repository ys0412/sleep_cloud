package net.eson.audio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.eson.audio.model.Audio;

import java.util.List;

/**
 * @author Eson
 * @date 2025年03月31日 17:35
 */
public interface AudioService extends IService<Audio> {
    public void incrementPlayCount(long id);

    public List<Audio> getTopHotAudios(int limit);

}
    