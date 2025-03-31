package net.eson.audio.controller;

import net.eson.audio.entity.Audio;
import net.eson.audio.service.AudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Eson
 * @date 2025年03月31日 17:29
 */

@RestController
@RequestMapping("/audio")
public class AudioController {
    @Autowired
    private AudioService audioService;

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

    // 删除音频记录
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return audioService.removeById(id);
    }
}
