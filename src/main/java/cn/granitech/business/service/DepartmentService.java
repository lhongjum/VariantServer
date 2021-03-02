package cn.granitech.business.service;

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.pojo.QuerySchema;
import cn.granitech.web.pojo.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/3/2 17:47
 * @Version: 1.0
 */

@Service
public class DepartmentService {

    @Autowired
    CrudService crudService;

    @Transactional
    public List<Map<String, String>> getDepartmentTree() throws InstantiationException, IllegalAccessException {
        QuerySchema querySchema = new QuerySchema();
        querySchema.setMainEntity(SystemEntities.Department);
        querySchema.setSelectFields("");
        querySchema.setFilter("(1=1)");
        List<Department> departmentList = crudService.queryListPojo(Department.class, SystemEntities.Department,
                null, null, null, null,
                "departmentId", "parentDepartmentId", "departmentName");
        Map<String, String> treeMap = new TreeMap<>();
        for (Department dpt : departmentList) {
            if (SystemEntityCodes.ROOT_DEPARTMENT_ID.equals(dpt.getDepartmentId().toString())) {
                treeMap.put("id", dpt.getDepartmentId().toString());
                treeMap.put("label", dpt.getDepartmentName());
                break;
            }
        }
        //TODO: 处理其他根节点数据

        List<Map<String, String>> resultList = new ArrayList<>();
        resultList.add(treeMap);

        return resultList;
    }

}
