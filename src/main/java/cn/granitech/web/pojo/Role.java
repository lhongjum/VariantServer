package cn.granitech.web.pojo;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/2/18 11:21
 * @Version: 1.0
 */

public class Role extends BasePojo {

    private String roleName;

    private Boolean disabled;

    private String description;

    private RightJsonObject rightJson;

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

    public RightJsonObject getRightJson() {
        return rightJson;
    }

    public void setRightJson(RightJsonObject rightJson) {
        this.rightJson = rightJson;
    }
}
