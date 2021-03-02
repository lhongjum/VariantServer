package cn.granitech.business.service;

import cn.granitech.variantorm.constant.CommonFields;
import cn.granitech.variantorm.constant.ReservedFields;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.dbmapping.DBMappingManager;
import cn.granitech.variantorm.exception.DuplicateEntityException;
import cn.granitech.variantorm.metadata.MetadataManager;
import cn.granitech.variantorm.metadata.fieldtype.FieldTypes;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.pojo.Entity;
import cn.granitech.variantorm.pojo.Field;
import cn.granitech.web.pojo.KeyValueEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/10 12:32
 * @Version: 1.0
 */

@Service
public class EntityManagerService {

    @Autowired
    DBMappingManager dbMappingManager;

    @Autowired
    PersistenceManager persistenceManager;

    @Autowired
    OptionManagerService optionManagerService;

    @Autowired
    TagManagerService tagManagerService;

    @Autowired
    FormLayoutManager formLayoutManager;
    @Autowired
    RoleService roleService;

    public MetadataManager getMetadataManager() {
        return persistenceManager.getMetadataManager();
    }

    @Transactional
    public void createEntity(Entity entity, String mainEntityName) {
        if (getMetadataManager().containsEntity(entity.getName())) {
            throw new DuplicateEntityException("实体已存在!");
        }

        if (entity.getEntityCode() != null) {
            if (getMetadataManager().containsEntity( entity.getEntityCode() ))
                throw new DuplicateEntityException("entityCode[" + entity.getEntityCode() +"]已存在!");
        }

        if (StringUtils.isBlank(entity.getPhysicalName())) {
            entity.setPhysicalName("t_" + entity.getName().toLowerCase());
        }
        if (entity.isDetailEntityFlag()) {
            Entity mainEntity = getMetadataManager().getEntity(mainEntityName);
            entity.setMainEntity(mainEntity);
        }
        Field idField = new Field();
        String idFieldName = entity.getName().substring(0, 1).toLowerCase() +
                entity.getName().substring(1) + "Id";
        idField.setName(idFieldName);
        idField.setLabel("id主键");
        idField.setPhysicalName(idField.getName());
        idField.setType(FieldTypes.PRIMARYKEY);
        idField.setIdFieldFlag(true);
        entity.setIdField(idField);
        dbMappingManager.createEntity(entity);

        //创建主从实体关联字段
        //TODO: 并且检查主从实体是否互相循环引用！！
        if (entity.isDetailEntityFlag()) {
            Field mainDetailField = new Field();
            mainDetailField.setName("md" + mainEntityName + "Id");
            mainDetailField.setLabel("主从关联Id");
            mainDetailField.setPhysicalName( mainDetailField.getName() );
            mainDetailField.setType(FieldTypes.REFERENCE);
            mainDetailField.setNullable(false);
            mainDetailField.setCreatable(true);
            mainDetailField.setUpdatable(false);
            mainDetailField.setIdFieldFlag(false);
            mainDetailField.setDefaultMemberOfListFlag(true);
            mainDetailField.setMainDetailFieldFlag(true);
            Entity mainEntity = getMetadataManager().getEntity(mainEntityName);
            Set<Entity> referTo = new HashSet<Entity>(){{
                add(mainEntity);
            }};
            mainDetailField.setReferTo(referTo);
            mainDetailField.setOwner(entity);
            dbMappingManager.createField(entity.getEntityCode(), mainDetailField);
        }
    }

    @Transactional
    public void updateEntity(Entity entity) {
        dbMappingManager.updateEntity(entity);
    }

    @Transactional
    public void updateEntityNameField(Entity entity, Field newNameField) {
        Field oldNameField = entity.getNameField();
        if (oldNameField != null) {
            oldNameField.setNameFieldFlag(false);
            dbMappingManager.updateField(entity.getEntityCode(), oldNameField);
        }
        entity.setNameField(newNameField);
        dbMappingManager.updateEntity(entity);
        dbMappingManager.updateField(entity.getEntityCode(), newNameField);
    }

    public Boolean entityCanBeDeleted(String entityName) {
        //TODO 添加更多删除判断逻辑
        return !SystemEntities.isInternalEntity(entityName) && !SystemEntities.isSystemEntity(entityName);
    }

    @Transactional
    public void deleteEntity(String entityName) throws JsonProcessingException {
        Entity entity = getMetadataManager().getEntity(entityName);
        dbMappingManager.deleteEntity(entityName);

        //为降低模块代码耦合，应使用事件通知方式！！
        formLayoutManager.deleteEntityLayout(entity.getEntityCode());
        roleService.deleteEntityRight(entity.getEntityCode());
        //TODO 删除实体单选项字段和多选项数据字典！！
        //TODO 删除实体列表设置数据和其他更多实体关联数据！！
    }

    @Transactional
    public void createPlainField(int entityCode, Field field) {
        dbMappingManager.createField(entityCode, field);
    }

    @Transactional
    public void createOptionField(int entityCode, Field field, List<KeyValueEntry<String, Integer>> optionList) {
        dbMappingManager.createField(entityCode, field);
        optionManagerService.saveOptionList(field.getOwner().getName(), field.getName(), optionList);
    }

    @Transactional
    public void updateOptionField(int entityCode, Field field, List<KeyValueEntry<String, Integer>> optionList) {
        updatePlainField(entityCode, field);
        optionManagerService.saveOptionList(field.getOwner().getName(), field.getName(), optionList);
    }

    @Transactional
    public void createTagField(int entityCode, Field field, List<String> tagList) {
        dbMappingManager.createField(entityCode, field);
        tagManagerService.saveTagList(field.getOwner().getName(), field.getName(), tagList);
    }

    @Transactional
    public void updateTagField(int entityCode, Field field, List<String> tagList) {
        updatePlainField(entityCode, field);
        tagManagerService.saveTagList(field.getOwner().getName(), field.getName(), tagList);
    }

    @Transactional
    public void updatePlainField(int entityCode, Field field) {
        Entity entity = getMetadataManager().getEntity(entityCode);
        if (!entity.containsField(field.getName())) {
            throw new IllegalArgumentException("invalid field name: [" + field.getName() + "]");
        } else {
            field.setOwner(entity);
            field.setEntityCode(entityCode);

            Field oldField = entity.getField(field.getName());
            /* 前端没有传入的属性值，需要设置原始值，以防保存时被清空，begin */
            field.setName( oldField.getName() );
            field.setPhysicalName( oldField.getPhysicalName() );
            field.setType( oldField.getType() );
            field.setDisplayOrder( oldField.getDisplayOrder() );
            field.setDescription( oldField.getDescription() );
            field.setIdFieldFlag( oldField.isIdFieldFlag() );
            field.setNameFieldFlag( oldField.isNameFieldFlag() );
            field.setMainDetailFieldFlag( oldField.isMainDetailFieldFlag() );
            /* end */
        }

        dbMappingManager.updateField(entityCode, field);
    }

    //@Transactional
    public Boolean fieldCanBeDeleted(String entityName, String fieldName) {
        return !ReservedFields.isReservedField(entityName, fieldName);
    }

    public Boolean fieldCanBeEdited(String entityName, String fieldName) {
        if (SystemEntities.User.equals(entityName) && "roles".equals(fieldName)) {
            return false;
        }

        return !CommonFields.containField(fieldName);
    }


    @Transactional
    public void deleteField(String entityName, String fieldName) throws IllegalAccessException {
        if (ReservedFields.isReservedField(entityName, fieldName)) {
            throw new IllegalAccessException("System field or reserved field can not be deleted!");
        }

        dbMappingManager.deleteField(entityName, fieldName);
    }

}
