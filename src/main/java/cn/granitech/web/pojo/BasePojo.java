package cn.granitech.web.pojo;

import cn.granitech.variantorm.persistence.EntityRecord;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/28 9:40
 * @Version: 1.0
 */

public class BasePojo {

    protected EntityRecord _entityRecord;

    public EntityRecord getEntityRecord() {
        return _entityRecord;
    }

    public void setEntityRecord(EntityRecord entityRecord) {
        this._entityRecord = entityRecord;
    }

    public void setFieldValue(String fieldName, Object value) {
        if (_entityRecord == null) {
            throw new IllegalArgumentException("entityRecord prop is null");
        }

        _entityRecord.setFieldValue(fieldName, value);
    }

    public Object getFieldValue(String fieldName) {
        if (_entityRecord == null) {
            throw new IllegalArgumentException("entityRecord prop is null");
        }

        return _entityRecord.getFieldValue(fieldName);
    }

    public void setFieldLabel(String fieldName, String labelValue) {
        if (_entityRecord == null) {
            throw new IllegalArgumentException("entityRecord prop is null");
        }

        _entityRecord.setFieldLabel(fieldName, labelValue);
    }

    public String getFieldLabel(String fieldName) {
        if (_entityRecord == null) {
            throw new IllegalArgumentException("entityRecord prop is null");
        }

        return _entityRecord.getFieldLabel(fieldName);
    }

}
