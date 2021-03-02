package cn.granitech.web.controller;

import cn.granitech.business.service.RoleService;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.web.pojo.ResponseBean;
import cn.granitech.web.pojo.RoleDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/2/18 17:15
 * @Version: 1.0
 */

@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Autowired
    RoleService roleService;

    @RequestMapping("/getBlankRoleData")
    public ResponseBean<RoleDTO> getBlankRoleData() {
        RoleDTO roleDTO = roleService.getBlankRoleData();
        return ResponseHelper.ok(roleDTO, "success");
    }

    @RequestMapping("/getRoleData")
    public ResponseBean<RoleDTO> getRoleData(@RequestParam String roleId) throws JsonProcessingException {
        RoleDTO roleDTO = roleService.getRoleData(ID.valueOf(roleId) );
        return ResponseHelper.ok(roleDTO, "success");
    }

    @RequestMapping("/saveRole")
    public ResponseBean<RoleDTO> saveRole(@RequestBody RoleDTO roleDTO) throws JsonProcessingException {
        RoleDTO savedRoleDTO = roleService.saveRole(roleDTO);
        return ResponseHelper.ok(savedRoleDTO, "success");
    }

    @RequestMapping("/deleteRole")
    public ResponseBean<Boolean> deleteRole(@RequestParam String roleId) throws JsonProcessingException {
        Boolean result = roleService.deleteRole( ID.valueOf(roleId) );
        return ResponseHelper.ok(result, "success");
    }

    @RequestMapping("/listRole")
    public ResponseBean<List<Map<String, Object>>> listRole() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<EntityRecord> roleList = roleService.listRole();
        for (EntityRecord roleRecord : roleList) {
            Map<String, Object> roleMap = new HashMap<>();
            roleMap.put("roleId", roleRecord.getFieldValue("roleId").toString());
            roleMap.put("roleName", roleRecord.getFieldValue("roleName"));
            resultList.add(roleMap);
        }

        return ResponseHelper.ok(resultList, "success");
    }

}
