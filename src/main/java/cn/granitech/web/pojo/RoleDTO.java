package cn.granitech.web.pojo;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/2/20 14:35
 * @Version: 1.0
 */

public class RoleDTO {

    private String roleId;

    private String roleName;

    private Boolean disabled;

    private String description;

    private Map<String, Object> rightValueMap;

    private List<Map<String, Object>> rightEntityList;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getRightValueMap() {
        return rightValueMap;
    }

    public void setRightValueMap(Map<String, Object> rightValueMap) {
        this.rightValueMap = rightValueMap;
    }

    public List<Map<String, Object>> getRightEntityList() {
        return rightEntityList;
    }

    public void setRightEntityList(List<Map<String, Object>> rightEntityList) {
        this.rightEntityList = rightEntityList;
    }
}
