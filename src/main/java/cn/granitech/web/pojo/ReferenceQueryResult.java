package cn.granitech.web.pojo;

import cn.granitech.variantorm.pojo.Pagination;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/18 11:30
 * @Version: 1.0
 */

public class ReferenceQueryResult {

    private String idFieldName;

    private String nameFieldName;

    private List<Map<String, String>> columnList;

    private List<Map<String, Object>> dataList;

    private Pagination pagination;

    public ReferenceQueryResult(List<Map<String, String>> columnList, List<Map<String, Object>> dataList,
                                Pagination pagination, String idFieldName, String nameFieldName) {
        this.columnList = columnList;
        this.dataList = dataList;
        this.pagination = pagination;
        this.idFieldName = idFieldName;
        this.nameFieldName = nameFieldName;
    }

    public String getIdFieldName() {
        return idFieldName;
    }

    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public String getNameFieldName() {
        return nameFieldName;
    }

    public void setNameFieldName(String nameFieldName) {
        this.nameFieldName = nameFieldName;
    }

    public List<Map<String, String>> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Map<String, String>> columnList) {
        this.columnList = columnList;
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
}
