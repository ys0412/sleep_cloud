package net.eson.audio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.eson.audio.mapper.AudioMapper;
import net.eson.audio.mapper.CategoryMapper;
import net.eson.audio.model.Audio;
import net.eson.audio.model.Category;
import net.eson.audio.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Eson
 * @date 2025年04月02日 14:18
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    AudioMapper audioMapper;

    @Override
    public List<Audio> getAudiosByCategoryIdOrderByPlayCountDesc(Long categoryId) {
        QueryWrapper<Audio> queryWrapper = new QueryWrapper<>();
        queryWrapper.inSql("id", "SELECT audio_id FROM audio_category WHERE category_id = " + categoryId);
        queryWrapper.orderByDesc("play_count"); // 按照 play_count 降序排列
        return audioMapper.selectList(queryWrapper);
    }
}
    