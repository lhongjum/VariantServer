package cn.granitech.web.pojo;

import cn.granitech.variantorm.pojo.Field;

import java.util.List;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/14 14:21
 * @Version: 1.0
 */

public class OptionRequestBody {

    private Field field;

    private List<KeyValueEntry<String, Integer>> optionList;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public List<KeyValueEntry<String, Integer>> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<KeyValueEntry<String, Integer>> optionList) {
        this.optionList = optionList;
    }
}
