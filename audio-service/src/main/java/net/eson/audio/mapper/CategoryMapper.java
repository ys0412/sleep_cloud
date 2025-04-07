package net.eson.audio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.eson.audio.model.Audio;
import net.eson.audio.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

}
