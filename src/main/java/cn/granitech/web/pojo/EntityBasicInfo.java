package cn.granitech.web.pojo;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/1/22 11:31
 * @Version: 1.0
 */

public class EntityBasicInfo {

    private String name;

    private String label;

    private String idField;

    private String nameField;

    public EntityBasicInfo(String name, String label, String idFieldName, String nameFieldName) {
        this.name = name;
        this.label = label;
        this.idField = idFieldName;
        this.nameField = nameFieldName;
    }

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

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getNameField() {
        return nameField;
    }

    public void setNameField(String nameField) {
        this.nameField = nameField;
    }
}
