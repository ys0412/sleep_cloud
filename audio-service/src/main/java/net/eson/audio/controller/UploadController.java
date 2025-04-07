package net.eson.audio.controller;

import net.eson.audio.model.Audio;
import net.eson.audio.service.AudioService;
import org.apache.commons.imaging.ImageReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

import static net.eson.audio.utils.ColorsUtil.calculateDominantAndAccentColors;

/**
 * @author Eson
 * @date 2025年03月24日 13:29
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final String IMAGES_UPLOAD_DIR = "D:/sleep/upload/images/"; // 图片存储路径
    private static final String MP3_UPLOAD_DIR = "D:/sleep/upload/mp3/"; // mp3存储路径
    private static final String URL_IMG_UPLOAD_PREFIX = "http://127.0.0.1:8084/uploads/images/";
    private static final String URL_MP3_UPLOAD_PREFIX = "http://127.0.0.1:8084/uploads/mp3/";
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);


    @Autowired
    private AudioService audioService;

    @PostMapping("/audioCover")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "id", required = false) Integer audioId) throws IOException, ImageReadException {
        logger.info("请求上传图片, audioId={}", audioId);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        File destinationFile = new File(IMAGES_UPLOAD_DIR + newFilename);
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs(); // 创建上传目录
        }
        //transferTo() 方法会将上传的文件内容写入到 destinationFile 指定的文件中。
        file.transferTo(destinationFile);
        //计算封面颜色
        String[] colors = calculateDominantAndAccentColors(destinationFile);
        String resColors = Arrays.toString(colors);
        String resUrl = URL_IMG_UPLOAD_PREFIX + newFilename;
        // **新增操作：仅返回 `coverUrl`，不绑定到音频**
        if (audioId == null || audioId <= 0) {
            return ResponseEntity.ok().body("{\"url\": \"" + resUrl + "\", \"coverColor\": \"" + resColors + "\"}");
        }

        // **更新操作：绑定 `coverUrl` 到已有书籍**
        Audio audio = audioService.getById(audioId);
        if (audio != null) {
            audio.setCoverUrl(newFilename);
            audio.setCoverRgb(resColors); // 存入主色调
            audioService.saveOrUpdate(audio);
        }

        return ResponseEntity.ok().body("{\"url\": \"" + resUrl + "\", \"coverColor\": \"" + resColors + "\"}");
    }

    @PostMapping("/mp3")
    public ResponseEntity<?> uploadMp3(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "audioId", required = false) Integer audioId) throws IOException {
        logger.info("请求上传MP3, audioId={}", audioId);

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("文件不能为空");
        }

        // 生成唯一文件名
        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        File destinationFile = new File(MP3_UPLOAD_DIR + newFilename);
        if (!destinationFile.getParentFile().exists()) {
            destinationFile.getParentFile().mkdirs(); // 创建上传目录
        }
        file.transferTo(destinationFile);


        String resUrl = URL_MP3_UPLOAD_PREFIX + newFilename;
        // **新增操作：仅返回 `coverUrl`，不绑定到audio**
        if (audioId == null || audioId <= 0) {
            return ResponseEntity.ok().body("{\"url\": \"" + resUrl + "\"}");
        }

        // **更新操作：绑定 `coverUrl` 到已有audio**
        Audio audio = audioService.getById(audioId);
        if (audio != null) {
            audio.setMp3Url(resUrl);
            audioService.saveOrUpdate(audio);
        }

        return ResponseEntity.ok().body("{\"url\": \"" + resUrl + "\"}");
    }

}
    