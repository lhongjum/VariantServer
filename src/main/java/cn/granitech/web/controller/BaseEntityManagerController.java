package cn.granitech.web.controller;

import cn.granitech.variantorm.metadata.MetadataManager;
import cn.granitech.variantorm.metadata.fieldtype.FieldTypes;
import cn.granitech.variantorm.pojo.Entity;
import cn.granitech.variantorm.pojo.Field;

import java.util.HashSet;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/24 10:54
 * @Version: 1.0
 */

public abstract class BaseEntityManagerController extends BaseController {

    protected Entity newEntity(int entityCode, String entityName, String entityLabel) {
        Entity entity = new Entity();
        entity.setEntityCode(entityCode);
        entity.setName(entityName);
        entity.setLabel(entityLabel);
        entity.setPhysicalName("t_" + entityName.toLowerCase());

        return entity;
    }

    protected Field newIdField(String fieldName, String fieldLabel) {
        Field idField = new Field();
        idField.setName(fieldName);
        idField.setLabel(fieldLabel);
        idField.setPhysicalName("c_" + fieldName);
        idField.setIdFieldFlag(true);
        idField.setType(FieldTypes.PRIMARYKEY);

        return idField;
    }

    protected Field newIdField(String fieldName, String fieldLabel, String physicalName) {
        Field idField = new Field();
        idField.setName(fieldName);
        idField.setLabel(fieldLabel);
        idField.setPhysicalName(physicalName);
        idField.setIdFieldFlag(true);
        idField.setType(FieldTypes.PRIMARYKEY);

        return idField;
    }

    protected Field newReferenceField(MetadataManager mdm, String fieldName, String fieldLabel, String referToEntityName) {
        Field refField = new Field();
        refField.setName(fieldName);
        refField.setLabel(fieldLabel);
        refField.setPhysicalName("c_" + fieldName);
        refField.setType(FieldTypes.REFERENCE);
        Entity refToEntity = mdm.getEntity(referToEntityName);
        refField.setReferTo(new HashSet<Entity>(){{
            add(refToEntity);
        }});

        return refField;
    }

    protected Field newReferenceField(MetadataManager mdm, String fieldName, String fieldLabel, String physicalName,
                                      String referToEntityName) {
        Field refField = new Field();
        refField.setName(fieldName);
        refField.setLabel(fieldLabel);
        refField.setPhysicalName(physicalName);
        refField.setType(FieldTypes.REFERENCE);
        Entity refToEntity = mdm.getEntity(referToEntityName);
        refField.setReferTo(new HashSet<Entity>(){{
            add(refToEntity);
        }});

        return refField;
    }

    protected Field newTextField(String fieldName, String fieldLabel) {
        Field txtField = new Field();
        txtField.setName(fieldName);
        txtField.setLabel(fieldLabel);
        txtField.setPhysicalName("c_" + fieldName);
        txtField.setType(FieldTypes.TEXT);

        return txtField;
    }

    protected Field newTextField(String fieldName, String fieldLabel, String physicalName) {
        Field txtField = new Field();
        txtField.setName(fieldName);
        txtField.setLabel(fieldLabel);
        txtField.setPhysicalName(physicalName);
        txtField.setType(FieldTypes.TEXT);

        return txtField;
    }

    protected Field newPasswordField(String fieldName, String fieldLabel, String physicalName) {
        Field pwdField = new Field();
        pwdField.setName(fieldName);
        pwdField.setLabel(fieldLabel);
        pwdField.setPhysicalName(physicalName);
        pwdField.setType(FieldTypes.PASSWORD);

        return pwdField;
    }

    protected Field newIntegerField(String fieldName, String fieldLabel, String physicalName) {
        Field intField = new Field();
        intField.setName(fieldName);
        intField.setLabel(fieldLabel);
        intField.setPhysicalName(physicalName);
        intField.setType(FieldTypes.INTEGER);

        return intField;
    }

    protected Field newTextAreaField(String fieldName, String fieldLabel, String physicalName) {
        Field taField = new Field();
        taField.setName(fieldName);
        taField.setLabel(fieldLabel);
        taField.setPhysicalName(physicalName);
        taField.setType(FieldTypes.TEXTAREA);

        return taField;
    }

}
