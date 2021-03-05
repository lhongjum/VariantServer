package cn.granitech.business.service;

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.constant.SystemEntityCodes;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.web.pojo.FormQueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    UserService userService;

    @Autowired
    PersistenceManager pm;

    @Transactional
    public List<Map<String, Object>> getDepartmentTree() throws InstantiationException, IllegalAccessException {
        return userService.buildDepartmentTree();
    }

    @Transactional
    public FormQueryResult saveDepartment(String entityName, String recordId, Map<String, Object> dataMap)
            throws IllegalAccessException, IOException, InstantiationException {
        if (SystemEntityCodes.ROOT_DEPARTMENT_ID.equals(recordId)) {
            throw new IllegalArgumentException("根部门节点禁止编辑！");
        }

        ID newDptId = StringUtils.isBlank(recordId) ? null : ID.valueOf(recordId);
        String newDptName = (String) dataMap.get("departmentName");
        if (userService.checkSameDepartmentName(newDptName, newDptId)) {  // 部门名称禁止重复！！
            String dptFieldLabel = pm.getMetadataManager().getEntity(SystemEntities.Department).getField("departmentName").getLabel();
            throw new IllegalArgumentException("存在相同的" + dptFieldLabel);
        }

        ID oldParentId = userService.getParentDepartmentId(newDptId);
        ID newParentId = ID.valueOf((String) dataMap.get("parentDepartmentId"));
        FormQueryResult result = crudService.saveRecord(entityName, recordId, dataMap);

        // 更新部门节点表（必须先更新部门缓存）
        updateDepartmentNodes(newDptId, oldParentId, newParentId, result.getFormData());

        return result;
    }

    private void updateDepartmentNodes(ID newDptId, ID oldParentId, ID newParentId, EntityRecord savedDpt) {
        // 重新加载部门缓存
        userService.loadDepartmentCache();

        ID savedDptId = (ID) savedDpt.getFieldValue("departmentId");
        if (newDptId == null) {
            Set<ID> parentIdSet = new LinkedHashSet<>();
            parentIdSet.add(savedDptId);
            parentIdSet.addAll( userService.getParentDepartmentIdList(savedDptId) );
            parentIdSet.forEach( parentId -> {
                createDepartmentNode(parentId, savedDptId);
            });
        } else {
            if (!oldParentId.equals(newParentId)) {
                Set<ID> childrenIdSet = new LinkedHashSet<>();
                childrenIdSet.addAll( userService.getChildrenDepartmentIdList(savedDptId) );
                childrenIdSet.forEach(this::batchDeleteDepartmentNode);

                Set<String> checkSet = new HashSet<>();
                Set<ID> parentIdSet = new LinkedHashSet<>();
                parentIdSet.addAll( userService.getParentDepartmentIdList(savedDptId) );
                for (ID pId : parentIdSet) {
                    for(ID cId : childrenIdSet) {
                        createDepartmentNode(pId, cId);
                        checkSet.add(pId.toString() + cId.toString());

                        //if (!cId.equals(pId) && !parentIdSet.contains(cId)) {
                        if (!checkSet.contains(cId.toString() + cId.toString())) {  //创建自身节点关联记录（跳过重复插入，避免索引冲突！！）
                            createDepartmentNode(cId, cId);
                            checkSet.add(cId.toString() + cId.toString());
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public Boolean deleteDepartment(ID departmentId) {
        // 检查如果有子部门禁止删除
        Set<ID> childrenIdSet = userService.getChildrenDepartmentIdList(departmentId);
        if (childrenIdSet.size() > 1) {
            throw new IllegalStateException("当前部门存在子部门，不可删除！");
        }

        //TODO: 如果部门存在用户禁止删除

        if (SystemEntityCodes.ROOT_DEPARTMENT_ID.equals(departmentId.toString())) {
            throw new IllegalArgumentException("根部门节点禁止删除！");
        }

        Boolean result = crudService.delete(departmentId, SystemEntities.Department);
        if (result) {
            // 重新加载部门缓存
            userService.loadDepartmentCache();

            // 更新部门节点表
            batchDeleteDepartmentNode(departmentId);
        }

        return result;
    }

    private void createDepartmentNode(ID parentId, ID childId) {
        System.out.println("pId: " + parentId.toString() + ", " + "cId: " + childId.toString());

        EntityRecord dptNode = pm.newRecord(SystemEntities.DepartmentNode);
        dptNode.setFieldValue("parentDepartmentId", parentId);
        dptNode.setFieldValue("childDepartmentId", childId);
        pm.insert(dptNode);
    }

    private void deleteDepartmentNode(ID parentId, ID childId) {
        String deleteFilter = String.format(" (parentDepartmentId = '%s') and (childDepartmentId = '%s') ",
                parentId.toString(), childId.toString());
        pm.batchDelete(SystemEntities.DepartmentNode, deleteFilter);
    }

    private void batchDeleteDepartmentNode(ID departmentId) {
        String deleteFilter = String.format(" (parentDepartmentId = '%s') ", departmentId.toString());
        pm.batchDelete(SystemEntities.DepartmentNode, deleteFilter);

        deleteFilter = String.format(" (childDepartmentId = '%s') ", departmentId.toString());
        pm.batchDelete(SystemEntities.DepartmentNode, deleteFilter);
    }

}
