package cn.granitech.business.service;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/2/24 13:53
 * @Version: 1.0
 */

import cn.granitech.interceptor.CallerContext;
import cn.granitech.variantorm.metadata.ID;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 权限管理Service类
 */

@Service
public class RightManager {

    @Autowired
    CallerContext callerContext;

    @Autowired
    RoleService roleService;

    private Map<String, Object> getRightValueMap() throws JsonProcessingException {
        ID callerId = ID.valueOf( callerContext.getCallerId() );
        return roleService.getRightMapOfUser(callerId);
    }

    public Boolean getFunctionRight(int rightCode) throws JsonProcessingException {
        Map<String, Object> rightValueMap = getRightValueMap();
        if (!rightValueMap.containsKey("r" + rightCode))
            return false;

        return (Boolean) rightValueMap.get("r" + rightCode);
    }

    public Integer getEntityRight(int entityCode, int rightType) throws JsonProcessingException {
        Map<String, Object> rightValueMap = getRightValueMap();
        String mapKey = "r" + entityCode + '-' + rightType;
        if (!rightValueMap.containsKey(mapKey))
            return 0;

        return (Integer) rightValueMap.get(mapKey);
    }

    public String getReadRightFilter(int entityCode) {
        //TODO

        return null;
    }

}
