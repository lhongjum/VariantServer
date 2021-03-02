package cn.granitech.web.pojo;

import cn.granitech.variantorm.pojo.Pagination;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/20 17:12
 * @Version: 1.0
 */

public class ListQueryResult {

    private List<Map<String, Object>> dataList;

    private Pagination pagination;

    private List<TableHeaderColumn> columnList;

    private EntityBasicInfo entityBasicInfo;

    public ListQueryResult() {
        this.dataList = null;
        this.pagination = null;
        this.columnList = null;
    }

    public ListQueryResult(List<Map<String, Object>> dataList, Pagination pagination) {
        this.dataList = dataList;
        this.pagination = pagination;
        this.columnList = null;
        this.entityBasicInfo = null;
    }

    public ListQueryResult(List<Map<String, Object>> dataList, Pagination pagination,
                           List<TableHeaderColumn> columnList, EntityBasicInfo entityBasicInfo) {
        this.dataList = dataList;
        this.pagination = pagination;
        this.columnList = columnList;
        this.entityBasicInfo = entityBasicInfo;
    }

    public List<Map<String, Object>> getDataList() {
        return dataList;
    }

    public void setDataList(List<Map<String, Object>> dataList) {
        this.dataList = dataList;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public List<TableHeaderColumn> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<TableHeaderColumn> columnList) {
        this.columnList = columnList;
    }

    public EntityBasicInfo getEntityBasicInfo() {
        return entityBasicInfo;
    }

    public void setEntityBasicInfo(EntityBasicInfo entityBasicInfo) {
        this.entityBasicInfo = entityBasicInfo;
    }
}
