package cn.granitech.business.service;

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.persistence.RecordQuery;
import cn.granitech.variantorm.pojo.IDName;
import cn.granitech.variantorm.pojo.ReferenceListCacheEO;
import cn.granitech.web.pojo.FormQueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/26 18:02
 * @Version: 1.0
 */

@Service
//@Transactional
public class UserService extends BaseService {

    private static final String UserId = "userId";
    private static final String UserName = "userName";
    private static final String DepartmentId = "departmentId";
    private static final String DepartmentName = "departmentName";

    @Autowired
    PersistenceManager pm;

    @Autowired
    CrudService crudService;

    @Autowired
    ApplicationContext applicationContext;

    private final Map<ID, EntityRecord> _userCache = new LinkedHashMap<>();
    private final Map<ID, EntityRecord> _departmentCache = new LinkedHashMap<>();

    private void loadUserCache() {
        RecordQuery recordQuery = pm.createRecordQuery();
        List<EntityRecord> userList = recordQuery.query(SystemEntities.User, null, null, null, null);
        _userCache.clear();
        for (EntityRecord user : userList) {
            _userCache.put((ID) user.getFieldValue(UserId), user);
        }
    }

    private void loadDepartCache() {
        RecordQuery recordQuery = pm.createRecordQuery();
        List<EntityRecord> departmentList = recordQuery.query(SystemEntities.Department, null, null, null, null);
        _departmentCache.clear();
        for (EntityRecord department : departmentList) {
            _departmentCache.put((ID) department.getFieldValue(DepartmentId), department);
        }
    }

    @Transactional
    public void reloadCache() {
        loadUserCache();
        loadDepartCache();
    }

    @Transactional
    public String getUserName(ID userId) {
        if (userId == null) {
            return null;
        }

        if (_userCache.size() <= 0) {
            loadUserCache();
        }

        if (_userCache.containsKey(userId)) {
            return (String) _userCache.get(userId).getFieldValue(UserName);
        }

        return null;
    }

    @Transactional
    public ID getDepartmentIdOfUser(ID userId) {
        if (_userCache.size() <= 0) {
            loadUserCache();
        }

        if (_userCache.containsKey(userId)) {
            return (ID) _userCache.get(userId).getFieldValue(DepartmentId);
        }

        return null;
    }

    @Transactional
    public String getDepartmentNameOfUser(ID userId) {
        ID departmentId = getDepartmentIdOfUser(userId);

        if (departmentId == null) {
            return null;
        }

        if (_departmentCache.size() <= 0) {
            loadDepartCache();
        }

        if (_departmentCache.containsKey(departmentId)) {
            return (String) _departmentCache.get(departmentId).getFieldValue(DepartmentName);
        }

        return null;
    }

    @Transactional
    public List<ID> getRoleIDListOfUser(ID userId) {
        if (_userCache.size() <= 0) {
            loadUserCache();
        }

        EntityRecord user = _userCache.get(userId);
        Object userRoles = user.getFieldValue("role");
        if (userRoles == null) {
            return new ArrayList<>();
        }

        List<IDName> idNameList = (List<IDName>) userRoles;
        List<ID> resutList = new ArrayList<>();
        idNameList.forEach(idName -> {
            resutList.add(idName.getId());
        });
        return resutList;
    }

    private ReferenceListCacheEO buildEventObject() {
        return new ReferenceListCacheEO(this, SystemEntities.User, "roles");
    }

    @Transactional
    public FormQueryResult saveUser(String entityName, String recordId, Map<String, Object> dataMap) throws IllegalAccessException,
            IOException, InstantiationException {
        //TODO: 需要用户密码进行MD5加密处理！！！
        FormQueryResult result = crudService.saveRecord(entityName, recordId, dataMap);
        // 更新roles字段缓存
        applicationContext.publishEvent( buildEventObject() );

        return result;
    }

    @Transactional
    public Boolean deleteUser(ID userId) {
        Boolean result = crudService.delete(userId, SystemEntities.User);
        if (result) {
            loadUserCache();
            // 更新roles字段缓存
            applicationContext.publishEvent( buildEventObject() );
        }

        return result;
    }

}
