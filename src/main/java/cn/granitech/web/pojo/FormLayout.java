package cn.granitech.web.pojo;

import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.metadata.fieldtype.PrimaryKeyField;
import cn.granitech.variantorm.persistence.EntityRecord;

import java.util.Date;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/26 13:58
 * @Version: 1.0
 */

public class FormLayout extends BasePojo {

    private ID formLayoutId;

    private String layoutName;

    private Integer entityCode;

    private String layoutJson;

    private Date createdOn;

    private ID createdBy;

    private Date modifiedOn;

    private ID modifiedBy;

    public ID getFormLayoutId() {
        formLayoutId = (ID) _entityRecord.getFieldValue("formLayoutId");
        return formLayoutId;
    }

    public void setFormLayoutId(ID formLayoutId) {
        this.formLayoutId = formLayoutId;
        _entityRecord.setFieldValue("formLayoutId", formLayoutId);
    }

    public String getLayoutName() {
        layoutName = (String) _entityRecord.getFieldValue("layoutName");
        return layoutName;
    }

    public void setLayoutName(String layoutName) {
        this.layoutName = layoutName;
        _entityRecord.setFieldValue("layoutName", layoutName);
    }

    public Integer getEntityCode() {
        entityCode = (Integer) _entityRecord.getFieldValue("entityCode");
        return entityCode;
    }

    public void setEntityCode(Integer entityCode) {
        this.entityCode = entityCode;
        _entityRecord.setFieldValue("entityCode", entityCode);
    }

    public String getLayoutJson() {
        layoutJson = (String) _entityRecord.getFieldValue("layoutJson");
        return layoutJson;
    }

    public void setLayoutJson(String layoutJson) {
        this.layoutJson = layoutJson;
        _entityRecord.setFieldValue("layoutJson", layoutJson);
    }

    public Date getCreatedOn() {
        createdOn = (Date) _entityRecord.getFieldValue("createdOn");
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
        _entityRecord.setFieldValue("createdOn", createdOn);
    }

    public ID getCreatedBy() {
        createdBy = (ID) _entityRecord.getFieldValue("createdBy");
        return createdBy;
    }

    public void setCreatedBy(ID createdBy) {
        this.createdBy = createdBy;
        _entityRecord.setFieldValue("createdBy", createdBy);
    }

    public Date getModifiedOn() {
        modifiedOn = (Date) _entityRecord.getFieldValue("modifiedOn");
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
        _entityRecord.setFieldValue("modifiedOn", modifiedOn);
    }

    public ID getModifiedBy() {
        modifiedBy = (ID) _entityRecord.getFieldValue("modifiedBy");
        return modifiedBy;
    }

    public void setModifiedBy(ID modifiedBy) {
        this.modifiedBy = modifiedBy;
        _entityRecord.setFieldValue("modifiedBy", modifiedBy);
    }

}
