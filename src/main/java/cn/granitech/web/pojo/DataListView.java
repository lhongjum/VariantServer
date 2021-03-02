package cn.granitech.web.pojo;

import cn.granitech.variantorm.metadata.ID;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/1/20 16:22
 * @Version: 1.0
 */

public class DataListView extends BasePojo {

    private ID dataListViewId;

    private Integer entityCode;

    private String viewName;

    private String headerJson;

    private String filterJson;

    private String paginationJson;

    public ID getDataListViewId() {
        return dataListViewId;
    }

    public void setDataListViewId(ID dataListViewId) {
        this.dataListViewId = dataListViewId;
    }

    public Integer getEntityCode() {
        return entityCode;
    }

    public void setEntityCode(Integer entityCode) {
        this.entityCode = entityCode;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getHeaderJson() {
        return headerJson;
    }

    public void setHeaderJson(String headerJson) {
        this.headerJson = headerJson;
    }

    public String getFilterJson() {
        return filterJson;
    }

    public void setFilterJson(String filterJson) {
        this.filterJson = filterJson;
    }

    public String getPaginationJson() {
        return paginationJson;
    }

    public void setPaginationJson(String paginationJson) {
        this.paginationJson = paginationJson;
    }
}
