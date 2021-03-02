package cn.granitech.web.controller;

import cn.granitech.util.ResponseHelper;
import cn.granitech.web.pojo.ResponseBean;
import cn.granitech.web.pojo.UploadDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/12/18 16:11
 * @Version: 1.0
 */

@RestController
@RequestMapping("/file")
public class FileUploadController extends BaseController {

    //TODO: 是否需要检查用户上传、下载权限？？


    private final ResourceLoader resourceLoader;

    private final UploadDir uploadDir;

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader, UploadDir uploadDir) {
        this.resourceLoader = resourceLoader;
        this.uploadDir = uploadDir;
    }

    //下载
    @RequestMapping(method = RequestMethod.GET, value = "/get/{filename:.+}", produces = {"application/octet-stream"})
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        String ROOT = uploadDir.getFileDir();
        try {
            //return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" +
                    URLEncoder.encode(filename, "UTF-8"));  /* 解决中文文件名乱码！！ */
            Resource fileResource = resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString());
            return ResponseEntity.ok().headers(headers).body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    //上传图片
    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseBean<String> upload(MultipartFile file) throws IOException {
        String ROOT = uploadDir.getFileDir();
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileExtName = fileName.substring(fileName.lastIndexOf("."));
        fileName = fileName.substring(0, fileName.lastIndexOf(".")) + randomFileString() + fileExtName;
        File dir = new File(ROOT);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        file.transferTo(Paths.get(ROOT, fileName));
        return ResponseHelper.ok(fileName, "success");
    }

    private String randomFileString() {
        String msPart = new SimpleDateFormat("mmss").format(new Date());
        String randomPart = String.format("%02d", new Random().nextInt(99));
        return "_" + randomPart + msPart;
    }

}
