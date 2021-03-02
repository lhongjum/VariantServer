package cn.granitech.web.pojo;

import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.web.pojo.layout.FieldProps;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/24 10:15
 * @Version: 1.0
 */

public class FormQueryResult {

    private String layoutJson;

    private Map<String, FieldProps> fieldPropsMap;

    private EntityRecord formData;

    private Map<String, String> labelData;

    private List<String> deletedFields;

    public FormQueryResult(String layoutJson, Map<String, FieldProps> fieldPropsMap, EntityRecord formData,
                           Map<String, String> labelsMap, List<String> deletedFields) {
        this.layoutJson = layoutJson;
        this.fieldPropsMap = fieldPropsMap;
        this.formData = formData;
        this.labelData = labelsMap;
        this.deletedFields = deletedFields;
    }

    public String getLayoutJson() {
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
    }

    public Map<String, FieldProps> getFieldPropsMap() {
        return fieldPropsMap;
    }

    public void setFieldPropsMap(Map<String, FieldProps> fieldPropsMap) {
        this.fieldPropsMap = fieldPropsMap;
    }

    public EntityRecord getFormData() {
        return formData;
    }

    public void setFormData(EntityRecord formData) {
        this.formData = formData;
    }

    public Map<String, String> getLabelData() {
        return labelData;
    }

    public void setLabelData(Map<String, String> labelData) {
        this.labelData = labelData;
    }

    public List<String> getDeletedFields() {
        return deletedFields;
    }

    public void setDeletedFields(List<String> deletedFields) {
        this.deletedFields = deletedFields;
    }

}
