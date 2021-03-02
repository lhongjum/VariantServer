package cn.granitech.web.pojo.layout;

import cn.granitech.variantorm.pojo.FieldViewModel;
import cn.granitech.variantorm.pojo.OptionModel;
import cn.granitech.variantorm.pojo.TagModel;

import java.util.List;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/25 15:13
 * @Version: 1.0
 */

public class FieldProps {

    private String name;
    private String label;
    private String type;

    /**
     * 是否允许为空值
     */
    private boolean nullable;

    /**
     * 是否允许创建实体时赋值
     */
    private boolean creatable;

    /**
     * 是否允许修改实体时赋值
     */
    private boolean updatable;

    /**
     * 字段视图模型（字段长度、精度、最大值、最小值等等）
     */
    private FieldViewModel fieldViewModel;

    private List<OptionModel> optionList;

    private List<TagModel> tagList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isCreatable() {
        return creatable;
    }

    public void setCreatable(boolean creatable) {
        this.creatable = creatable;
    }

    public boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public FieldViewModel getFieldViewModel() {
        return fieldViewModel;
    }

    public void setFieldViewModel(FieldViewModel fieldViewModel) {
        this.fieldViewModel = fieldViewModel;
    }

    public List<OptionModel> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<OptionModel> optionList) {
        this.optionList = optionList;
    }

    public List<TagModel> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagModel> tagList) {
        this.tagList = tagList;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
