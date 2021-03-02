package cn.granitech.web.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/12/29 12:21
 * @Version: 1.0
 */

@Component
public class UploadDir {

    @Value("${upload.img-dir}")
    private String imgDir;

    @Value("${upload.file-dir}")
    private String fileDir;

    public String getImgDir() {
        return imgDir;
    }

    public String getFileDir() {
        return fileDir;
    }
}
