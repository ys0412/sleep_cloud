package net.eson.audio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import net.eson.audio.model.Audio;
import net.eson.audio.model.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    // 根据 categoryId 查询所有的 audio
    List<Audio> getAudiosByCategoryIdOrderByPlayCountDesc(Long categoryId);
}
