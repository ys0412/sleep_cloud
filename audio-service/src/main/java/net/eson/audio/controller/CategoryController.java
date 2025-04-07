package net.eson.audio.controller;

import net.eson.audio.model.Audio;
import net.eson.audio.model.Category;
import net.eson.audio.service.AudioService;
import net.eson.audio.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Eson
 * @date 2025年04月02日 14:21
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    // 查询所有分类
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.list();
    }

    // 根据 ID 查询分类
    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Integer id) {
        return categoryService.getById(id);
    }

    // 根据 categoryId 查询该分类下所有的 audio
    @GetMapping("/{categoryId}/audios")
    public ResponseEntity<List<Audio>> getAudiosByCategoryId(@PathVariable("categoryId") Long categoryId) {
        List<Audio> audios = categoryService.getAudiosByCategoryIdOrderByPlayCountDesc(categoryId);

        if (audios.isEmpty()) {
            return ResponseEntity.noContent().build();  // 如果没有音频，返回 204
        }

        return ResponseEntity.ok(audios);  // 返回 200 和音频列表
    }

    // 添加分类
    @PostMapping
    public boolean addCategory(@RequestBody Category category) {
        return categoryService.save(category);
    }

    // 更新分类
    @PutMapping
    public boolean updateCategory(@RequestBody Category category) {
        return categoryService.updateById(category);
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public boolean deleteCategory(@PathVariable Integer id) {
        return categoryService.removeById(id);
    }

}
    