package cn.granitech.web.pojo;

import cn.granitech.variantorm.pojo.Field;

import java.util.List;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/14 14:21
 * @Version: 1.0
 */

public class TagRequestBody {

    private Field field;

    private List<String> tagList;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }
}
