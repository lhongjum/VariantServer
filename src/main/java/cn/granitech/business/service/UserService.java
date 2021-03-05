package cn.granitech.business.service;

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.persistence.RecordQuery;
import cn.granitech.variantorm.pojo.IDName;
import cn.granitech.variantorm.pojo.ReferenceListCacheEO;
import cn.granitech.web.pojo.Department;
import cn.granitech.web.pojo.FormQueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

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

    public void loadDepartmentCache() {
        RecordQuery recordQuery = pm.createRecordQuery();
        List<EntityRecord> departmentList = recordQuery.query(SystemEntities.Department, null, null,
                "[departmentId]", null);
        _departmentCache.clear();
        for (EntityRecord department : departmentList) {
            _departmentCache.put((ID) department.getFieldValue(DepartmentId), department);
        }
    }

    @Transactional
    public void reloadCache() {
        loadUserCache();
        loadDepartmentCache();
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
            loadDepartmentCache();
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

    private boolean checkSameUserName(String userName, ID userId) {
        if (_userCache.size() <= 0) {
            loadUserCache();
        }

        for (EntityRecord userRecord : _userCache.values()) {
            String uName = (String) userRecord.getFieldValue("userName");
            if (uName.equalsIgnoreCase(userName)) {
                return !((ID) userRecord.getFieldValue("userId")).equals(userId);
            }
        }

        return false;
    }

    private boolean checkSameLoginName(String loginName, ID userId) {
        if (_userCache.size() <= 0) {
            loadUserCache();
        }

        for (EntityRecord userRecord : _userCache.values()) {
            String lName = (String) userRecord.getFieldValue("loginName");
            if (lName.equalsIgnoreCase(loginName)) {
                return !((ID) userRecord.getFieldValue("userId")).equals(userId);
            }
        }

        return false;
    }

    public boolean checkSameDepartmentName(String dptName, ID dptId) {
        if (_departmentCache.size() <= 0) {
            loadDepartmentCache();
        }

        for (EntityRecord dptRecord : _departmentCache.values()) {
            String dName = (String) dptRecord.getFieldValue("departmentName");
            if (dName.equalsIgnoreCase(dptName)) {
                return !((ID) dptRecord.getFieldValue("departmentId")).equals(dptId);
            }
        }

        return false;
    }

    @Transactional
    public FormQueryResult saveUser(String entityName, String recordId, Map<String, Object> dataMap)
            throws IllegalAccessException, IOException, InstantiationException {
        ID newUserId = StringUtils.isBlank(recordId) ? null : ID.valueOf(recordId);
        String newUserName = (String) dataMap.get("userName");
        if (checkSameUserName(newUserName, newUserId)) {
            String unFieldLabel = pm.getMetadataManager().getEntity(SystemEntities.User).getField("userName").getLabel();
            throw new IllegalArgumentException("存在相同的" + unFieldLabel);
        }

        String newLoginName = (String) dataMap.get("loginName");
        if (checkSameLoginName(newLoginName, newUserId)) {
            String lnFieldLabel = pm.getMetadataManager().getEntity(SystemEntities.User).getField("loginName").getLabel();
            throw new IllegalArgumentException("存在相同的" + lnFieldLabel);
        }


        //TODO: 需要用户密码进行MD5加密处理！！！
        FormQueryResult result = crudService.saveRecord(entityName, recordId, dataMap);
        // 更新用户缓存
        loadUserCache();
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

    private List<Map<String, Object>> buildDepartmentChildNodes(ID parentDptId) {
        List<Map<String, Object>> childrenList = new ArrayList<>();
        for (EntityRecord dptRecord : _departmentCache.values()) {
            ID curParentDptId = (ID) dptRecord.getFieldValue("parentDepartmentId");
            ID curDptId = (ID) dptRecord.getFieldValue("departmentId");
            if (parentDptId.equals(curParentDptId)) {
                Map<String, Object> nodeTree = new TreeMap<>();
                nodeTree.put("id", curDptId.toString());
                nodeTree.put("label", dptRecord.getFieldValue("departmentName"));
                nodeTree.put("children", buildDepartmentChildNodes(curDptId));

                childrenList.add(nodeTree);
            }
        }

        return childrenList;
    }

    public List<Map<String, Object>> buildDepartmentTree() {
        if (_departmentCache.size() <= 0) {
            loadDepartmentCache();
        }

        //Map转为json最多支持几层嵌套？？
        Map<String, Object> departmentMap = new TreeMap<>();
        String rootDptIdStr = SystemEntityCodes.ROOT_DEPARTMENT_ID;
        ID rootDptId = ID.valueOf(SystemEntityCodes.ROOT_DEPARTMENT_ID);
        EntityRecord rootDptRecord = _departmentCache.get(rootDptId);
        departmentMap.put("id", rootDptIdStr);
        departmentMap.put("label", rootDptRecord.getFieldValue("departmentName"));
        departmentMap.put("children", buildDepartmentChildNodes(rootDptId));
        List<Map<String, Object>> resultList = new ArrayList<>();
        resultList.add(departmentMap);

        return resultList;
    }

    public ID getParentDepartmentId(ID dptId) {
        if (dptId == null) {
            return null;
        }

        if (_departmentCache.size() <= 0) {
            loadDepartmentCache();
        }

        if (!_departmentCache.containsKey(dptId)) {
            return null;
        }

        return (ID) _departmentCache.get(dptId).getFieldValue("parentDepartmentId");
    }

    /* 获取所有父级节点Id，包含本身 */
    public Set<ID> getChildrenDepartmentIdList(ID dptId) {
        Set<ID> resultSet = new LinkedHashSet<>();
        if (dptId == null) {
            return resultSet;
        }

        resultSet.add(dptId);  //包含自己
        for (EntityRecord dptRecord : _departmentCache.values()) {
            ID parentId = (ID) dptRecord.getFieldValue("parentDepartmentId");
            ID curDptId = (ID) dptRecord.getFieldValue("departmentId");
            if (dptId.equals(parentId)) {
                resultSet.add(curDptId);
                resultSet.addAll( getChildrenDepartmentIdList(curDptId) );
            }
        }

        return resultSet;
    }

    /* 获取所有子级节点Id，包含本身 */
    public Set<ID> getParentDepartmentIdList(ID dptId) {
        Set<ID> resultSet = new LinkedHashSet<>();
        if (dptId == null) {
            return resultSet;
        }

        resultSet.add(dptId);  //包含自己
        for (EntityRecord dptRecord : _departmentCache.values()) {
            ID curDptId = (ID) dptRecord.getFieldValue("departmentId");
            ID parentId = (ID) dptRecord.getFieldValue("parentDepartmentId");
            if (curDptId.equals(dptId)) {
                if (parentId != null) {
                    resultSet.add(parentId);
                    resultSet.addAll( getParentDepartmentIdList(parentId) );
                }
            }
        }

        return resultSet;
    }

}
