package cn.granitech.web.pojo;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/11 15:41
 * @Version: 1.0
 */

public class ListQueryRequestBody {

    private String mainEntity;

    private String fieldsList;

    private String filter;

    private Integer pageSize;

    private Integer pageNo;

    public String getMainEntity() {
        return mainEntity;
    }

    public void setMainEntity(String mainEntity) {
        this.mainEntity = mainEntity;
    }

    public String getFieldsList() {
        return fieldsList;
    }

    public void setFieldsList(String fieldsList) {
        this.fieldsList = fieldsList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
}
