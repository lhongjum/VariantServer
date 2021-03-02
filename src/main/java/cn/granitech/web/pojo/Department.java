package cn.granitech.web.pojo;

import cn.granitech.variantorm.metadata.ID;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/26 13:58
 * @Version: 1.0
 */

public class Department extends BasePojo {

    private ID departmentId;

    private ID parentDepartmentId;

    private String departmentName;

    public ID getDepartmentId() {
        departmentId = (ID) _entityRecord.getFieldValue("departmentId");
        return departmentId;
    }

    public void setDepartmentId(ID departmentId) {
        this.departmentId = departmentId;
        _entityRecord.setFieldValue("departmentId", departmentId);
    }

    public ID getParentDepartmentId() {
        parentDepartmentId = (ID) _entityRecord.getFieldValue("parentDepartmentId");
        return parentDepartmentId;
    }

    public void setParentDepartmentId(ID parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
        _entityRecord.setFieldValue("parentDepartmentId", parentDepartmentId);
    }

    public String getDepartmentName() {
        departmentName = (String) _entityRecord.getFieldValue("departmentName");
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
        _entityRecord.setFieldValue("departmentName", departmentName);
    }
}
