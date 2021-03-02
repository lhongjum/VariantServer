package cn.granitech.web.controller;

import cn.granitech.util.ResponseHelper;
import cn.granitech.web.pojo.ResponseBean;
import cn.granitech.web.pojo.UploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/12/18 16:10
 * @Version: 1.0
 */

@RestController
@RequestMapping("/picture")
public class PictureUploadController extends BaseController {

    //TODO: 是否需要检查用户上传、下载权限？？


    private final ResourceLoader resourceLoader;

    private final UploadDir uploadDir;

    @Autowired
    public PictureUploadController(ResourceLoader resourceLoader, UploadDir uploadDir) {
        this.resourceLoader = resourceLoader;
        this.uploadDir = uploadDir;
    }

    //显示图片
    @RequestMapping(method = RequestMethod.GET, value = "/get/{filename:.+}", produces = {"image/jpeg;image/png;image/gif;"})
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        String ROOT = uploadDir.getImgDir();
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //上传图片
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseBean<String> upload(MultipartFile file) throws IOException {
        String ROOT = uploadDir.getImgDir();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        fileName = fileName.substring(fileName.lastIndexOf("."));
        fileName = UUID.randomUUID().toString().replace("-", "") + fileName;

        File dir = new File(ROOT);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(Paths.get(ROOT, fileName));
        return ResponseHelper.ok(fileName, "success");
    }

}
