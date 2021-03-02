package cn.granitech.web.pojo;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/1/18 16:51
 * @Version: 1.0
 */

public class TableHeaderColumn {

    private String prop;

    private String label;

    private String width;

    private String align;

    private String type;

    public TableHeaderColumn() {
        //
    }

    public TableHeaderColumn(String width, String align) {
        this.width = width;
        this.align = align;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
