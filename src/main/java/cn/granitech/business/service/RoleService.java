package cn.granitech.business.service;

import cn.granitech.util.JsonHelper;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.persistence.RecordQuery;
import cn.granitech.variantorm.pojo.Entity;
import cn.granitech.web.pojo.RoleDTO;
import cn.granitech.web.pojo.layout.LayoutJsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2021/2/18 17:16
 * @Version: 1.0
 */

@Service
public class RoleService extends BaseService {

    //TODO 此类应该包括权限缓存管理

    @Autowired
    EntityManagerService entityManagerService;

    @Autowired
    PersistenceManager persistenceManager;

    @Autowired
    CrudService crudService;

    @Autowired
    UserService userService;

    private final Map<ID, Map<String, Object>> _roleCache = new LinkedHashMap<>();

    private void loadRoleCache() throws JsonProcessingException {
        RecordQuery recordQuery = persistenceManager.createRecordQuery();
        List<EntityRecord> roleList = recordQuery.query(SystemEntities.Role, null, null, null, null);
        _roleCache.clear();
        for (EntityRecord role : roleList) {
            Map<String, Object> rightValueMap = new HashMap<>();
            String rightJson = (String) role.getFieldValue("rightJson");
            if (StringUtils.isNotBlank(rightJson)) {
                rightValueMap = JsonHelper.readJsonValue(rightJson, new TypeReference<Map<String, Object>>(){});
            }
            _roleCache.put((ID) role.getFieldValue("roleId"), rightValueMap);
        }
    }

    /**
     * 合并两个权限角色，取两者中最高权限
     * @param map1
     * @param map2
     * @return
     */
    private Map<String, Object> mergeRightMap(Map<String, Object> map1, Map<String, Object> map2) {
        for (String mapKey : map2.keySet()) {
            if (!map1.containsKey(mapKey)) {
                map1.put(mapKey, map2.get(mapKey));
            } else {
                if (map2.get(mapKey) instanceof Integer) {
                    Integer map1Integer = (Integer) map1.get(mapKey);
                    Integer map2Integer = (Integer) map2.get(mapKey);
                    if (map2Integer > map1Integer) {
                        map1.put(mapKey, map2Integer);
                    }
                }

                if (map2.get(mapKey) instanceof Boolean) {
                    boolean map2Boolean = (Boolean) map2.get(mapKey);
                    if (map2Boolean) {
                        map1.put(mapKey, true);
                    }
                }
            }
        }

        return map1;
    }

    /**
     * 获取用户权限map
     * @param userId
     * @return
     */
    public Map<String, Object> getRightMapOfUser(ID userId) throws JsonProcessingException {
        List<ID> roleList = userService.getRoleIDListOfUser(userId);
        if (roleList.size() <= 0) {
            return new HashMap<>();
        }

        if (_roleCache.size() <= 0) {
            loadRoleCache();
        }

        Map<String, Object> resultMap = _roleCache.get(roleList.get(0));
        for (int i = 1; i < roleList.size(); i++) {
            Map<String, Object> newMap = _roleCache.get(roleList.get(i));
            mergeRightMap(resultMap, newMap);
        }

        return resultMap;
    }

    private Map<String, Object> buildRightValueMap() {
        Map<String, Object> rightMap = new LinkedHashMap<>();
        Collection<Entity> entitySet = entityManagerService.getMetadataManager().getEntitySet();
        entitySet.forEach(entity -> {
            if ( !SystemEntities.isInternalEntity(entity.getName()) ) {
                rightMap.put("r" + entity.getEntityCode() + "-1", 0);  //查看权限
                rightMap.put("r" + entity.getEntityCode() + "-2", 0);  //新建权限
                rightMap.put("r" + entity.getEntityCode() + "-3", 0);  //修改权限
                rightMap.put("r" + entity.getEntityCode() + "-4", 0);  //删除权限
            }
        });

        SystemRights.getRightList().forEach(rightNum -> {
            rightMap.put("r" + rightNum, false);
        });

        return rightMap;
    }

    private List<Map<String, Object>> buildRightEntityList() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Collection<Entity> entitySet = entityManagerService.getMetadataManager().getEntitySet();
        entitySet.forEach(entity -> {
            if ( !SystemEntities.isInternalEntity(entity.getName()) ) {
                Map<String, Object> entityMap = new HashMap<>();
                entityMap.put("name", entity.getName());
                entityMap.put("label", entity.getLabel());
                entityMap.put("entityCode", entity.getEntityCode());
                entityMap.put("authorizable", entity.isAuthorizable());
                resultList.add(entityMap);
            }
        });

        return  resultList;
    }

    public RoleDTO getBlankRoleData() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setRoleName("");
        roleDTO.setDisabled(false);
        roleDTO.setDescription("");
        roleDTO.setRightValueMap( buildRightValueMap() );
        roleDTO.setRightEntityList( buildRightEntityList() );

        return roleDTO;
    }

    @Transactional
    public RoleDTO getRoleData(ID roleId) throws JsonProcessingException {
        RoleDTO roleDTO = new RoleDTO();
        EntityRecord roleRecord = crudService.queryById(roleId);
        roleDTO.setRoleId( roleRecord.getFieldValue("roleId").toString() );
        roleDTO.setRoleName( (String) roleRecord.getFieldValue("roleName") );
        roleDTO.setDisabled( (Boolean) roleRecord.getFieldValue("disabled") );
        roleDTO.setDescription( (String) roleRecord.getFieldValue("description") );
        roleDTO.setRightEntityList( buildRightEntityList() );

        String rightJson = (String) roleRecord.getFieldValue("rightJson");
        Map<String, Object> rightValueMap = new LinkedHashMap<>();
        if (StringUtils.isNotBlank(rightJson)) {
            rightValueMap = JsonHelper.readJsonValue(rightJson, new TypeReference<Map<String, Object>>(){});
        }
        roleDTO.setRightValueMap(rightValueMap);

        return roleDTO;
    }

    @Transactional
    public RoleDTO saveRole(RoleDTO roleDTO) throws JsonProcessingException {
        //检查是否重名
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("roleName", roleDTO.getRoleName());
        }};
        List<EntityRecord> oldRoleList = crudService.queryListRecord(SystemEntities.Role, "[roleName]=:roleName",
                paramMap, null, null);
        boolean foundFlag = false;
        for (EntityRecord oneRole : oldRoleList) {
            if (oneRole.getFieldValue("roleName").equals(roleDTO.getRoleName()) &&
                    !oneRole.getFieldValue("roleId").equals(roleDTO.getRoleId())) {
                foundFlag = true;
                break;
            }
        }
        if (foundFlag) {
            throw new IllegalArgumentException("角色名称重复！");
        }

        String roleIdStr = roleDTO.getRoleId();
        if (StringUtils.isBlank(roleIdStr)) {
            EntityRecord roleRecord = crudService.newRecord(SystemEntities.Role);
            roleRecord.setFieldValue("roleName", roleDTO.getRoleName());
            roleRecord.setFieldValue("disabled", roleDTO.getDisabled());
            roleRecord.setFieldValue("description", roleDTO.getDescription());
            if (roleDTO.getRightValueMap() != null) {
                removeBlankRightFromMap(roleDTO.getRightValueMap());
                roleRecord.setFieldValue("rightJson", JsonHelper.writeObjectAsString(roleDTO.getRightValueMap()));
            }
            ID newRoleId = crudService.create(roleRecord);
            roleDTO.setRoleId(newRoleId.toString());
        } else {
            //禁止修改管理员角色
            if (SystemEntityCodes.ADMIN_ROLE_ID.equals(roleDTO.getRoleId())) {
                throw new IllegalArgumentException("管理员角色禁止修改");
            }

            EntityRecord oldRoleRecord = crudService.queryById( ID.valueOf(roleDTO.getRoleId()) );
            oldRoleRecord.setFieldValue("roleName", roleDTO.getRoleName());
            oldRoleRecord.setFieldValue("disabled", roleDTO.getDisabled());
            oldRoleRecord.setFieldValue("description", roleDTO.getDescription());
            if (roleDTO.getRightValueMap() != null) {
                removeBlankRightFromMap(roleDTO.getRightValueMap());
                oldRoleRecord.setFieldValue("rightJson", JsonHelper.writeObjectAsString(roleDTO.getRightValueMap()));
            }
            crudService.update(oldRoleRecord);
        }

        //新权限缓存
        loadRoleCache();

        return roleDTO;
    }

    private void removeBlankRightFromMap(Map<String, Object> rightValueMap) {
//        for (Iterator<Map.Entry<String, Object>> it = rightValueMap.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry<String, Object> item = it.next();
//            if (((Integer ) item.getValue() == 0) || "0".equals(item.getValue())) {
//                it.remove();
//            }
//        }
        rightValueMap.entrySet().removeIf(item -> ((item.getValue() instanceof Integer) && (Integer) item.getValue() == 0));
    }

    @Transactional
    public Boolean deleteRole(ID roleId) throws JsonProcessingException {
        //禁止删除管理员角色
        if (SystemEntityCodes.ADMIN_ROLE_ID.equals(roleId.toString())) {
            throw new IllegalArgumentException("管理员角色禁止删除");
        }

        //更新权限缓存
        loadRoleCache();

        return crudService.delete(roleId, SystemEntities.Role);
    }

    //TODO 删除实体权限！！
    //TODO 删除实体表单布局！！
    //TODO 删除实体列表设置！！
    public Boolean deleteEntityRight(int entityCode) throws JsonProcessingException {
        List<EntityRecord> roleList = crudService.queryListRecord(SystemEntities.Role, null, null,
                null, null);
        for (EntityRecord roleRecord : roleList) {
            String rightJson = (String) roleRecord.getFieldValue("rightJson");
            if (StringUtils.isNotBlank(rightJson)) {
                Map<String, Object> rightValueMap = new LinkedHashMap<>();
                rightValueMap = JsonHelper.readJsonValue(rightJson, new TypeReference<Map<String, Object>>(){});
                boolean foundFlag = deleteEntityRightFromMap(rightValueMap, entityCode);
                if (foundFlag) {
                    roleRecord.setFieldValue("rightJson", JsonHelper.writeObjectAsString(rightValueMap));
                    persistenceManager.update(roleRecord);
                }
            }
        }

        return true;
    }

    private boolean deleteEntityRightFromMap(Map<String, Object> rightValueMap, int entityCode) {
        boolean result = false;
        String mapPrefix = "r" + entityCode + "-";
        for (int i = 1; i < 5; i++) {
            String mapKey = mapPrefix + i;
            if (rightValueMap.containsKey(mapKey)) {
                result = true;
                rightValueMap.remove(mapKey);
            }
        }

        return result;
    }

    @Transactional
    public List<EntityRecord> listRole() {
        List<EntityRecord> roleList = crudService.queryListRecord(SystemEntities.Role, "[disabled]=0", null,
                null, null, "roleId", "roleName");
        return roleList;
    }

}
