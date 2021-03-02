package cn.granitech.web.controller;

import cn.granitech.business.service.DepartmentService;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.pojo.QuerySchema;
import cn.granitech.web.pojo.Department;
import cn.granitech.web.pojo.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseBean<List<Map<String, String>>> getDepartmentTree() throws InstantiationException, IllegalAccessException {
        List<Map<String, String>> resultList = departmentService.getDepartmentTree();
        return new ResponseBean<>(200, null, "success", resultList);
    }

}
