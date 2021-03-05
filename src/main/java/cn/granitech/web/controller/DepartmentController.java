package cn.granitech.web.controller;

import cn.granitech.business.service.DepartmentService;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.pojo.QuerySchema;
import cn.granitech.web.pojo.Department;
import cn.granitech.web.pojo.FormQueryResult;
import cn.granitech.web.pojo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/11 17:15
 * @Version: 1.0
 */

@RestController
@RequestMapping("/department")
public class DepartmentController extends CrudController {

    @Autowired
    DepartmentService departmentService;

    @RequestMapping("/treeData")
    public ResponseBean<List<Map<String, Object>>> getDepartmentTree() throws InstantiationException, IllegalAccessException {
        List<Map<String, Object>> resultList = departmentService.getDepartmentTree();
        return new ResponseBean<>(200, null, "success", resultList);
    }

    @RequestMapping(value= "/saveDepartment", method = RequestMethod.POST)
    public ResponseBean<FormQueryResult> saveDepartment(@RequestParam("entity") String entityName,
                                                  @RequestParam("id") String recordId,
                                                  @RequestBody Map<String, Object> dataMap)
            throws IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = departmentService.saveDepartment(entityName, recordId, dataMap);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping(value= "/deleteDepartment", method = RequestMethod.POST)
    public ResponseBean<Boolean> deleteDepartment(@RequestParam String departmentId) {
        Boolean result = departmentService.deleteDepartment(ID.valueOf(departmentId) );
        return ResponseHelper.ok(result, "success");
    }

}
