package cn.granitech.web.controller;

import cn.granitech.business.service.EntityManagerService;
import cn.granitech.business.service.OptionManagerService;
import cn.granitech.business.service.TagManagerService;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.fieldtype.FieldTypes;
import cn.granitech.variantorm.pojo.*;
import cn.granitech.variantorm.util.MDHelper;
import cn.granitech.web.pojo.KeyValueEntry;
import cn.granitech.web.pojo.OptionRequestBody;
import cn.granitech.web.pojo.ResponseBean;
import cn.granitech.web.pojo.TagRequestBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/9/3 15:44
 * @Version: 1.0
 */

@RestController
@RequestMapping("/systemManager")
public class SystemManagerController extends BaseController {

    @Autowired
    EntityManagerService entityManagerService;

    @Autowired
    OptionManagerService optionManagerService;

    @Autowired
    TagManagerService tagManagerService;

    //TODO: 本类所有方法需要添加权限检查！！

    @RequestMapping("/getEntitySet")
    public ResponseBean<List<Map<String, Object>>> getEntitySet() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        entityManagerService.getMetadataManager().getEntitySet().forEach(entity -> {
            if (SystemEntities.isInternalEntity(entity.getName()))
                return;  //跳出当前循环，进入下一次循环

            Map<String, Object> entityMap = new LinkedHashMap<>();
            entityMap.put("name", entity.getName());
            entityMap.put("label", entity.getLabel());
            entityMap.put("entityCode", entity.getEntityCode());
            entityMap.put("systemEntityFlag", SystemEntities.isSystemEntity(entity.getName()));
            entityMap.put("detailEntityFlag", entity.isDetailEntityFlag());
            entityMap.put("layoutable", entity.isLayoutable());
            entityMap.put("listable", entity.isListable());
            resultList.add(entityMap);
        });
        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/getFieldSet")
    public ResponseBean<List<Map<String, Object>>> getFieldSet(@RequestParam("entity") String entityName) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        List<Map<String, Object>> resultList = new ArrayList<>();
        entity.getFieldSet().forEach(field -> {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("name", field.getName());
            fieldMap.put("label", field.getLabel());
            fieldMap.put("type", field.getType().getName());
            fieldMap.put("reserved", MDHelper.isReservedField(field));

            resultList.add(fieldMap);
        });

        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/getFieldListOfEntity")
    public ResponseBean<List<Map<String, Object>>> getFieldListOfEntity(@RequestParam("entity") String entityName) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        List<Map<String, Object>> resultList = new ArrayList<>();
        entity.getFieldSet().forEach(field -> {
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("name", field.getName());
            fieldMap.put("label", field.getLabel());
            fieldMap.put("physicalName", field.getPhysicalName());
            fieldMap.put("type", field.getType().getName());
            fieldMap.put("idFieldFlag", field.isIdFieldFlag());
            fieldMap.put("nameFieldFlag", field.isNameFieldFlag());
            fieldMap.put("mainDetailFieldFlag", field.isMainDetailFieldFlag());
            if ((field.getReferTo() == null) || (field.getReferTo().size() <= 0)){
                fieldMap.put("referTo", null);
            } else {
                StringBuilder referTos = new StringBuilder();
                field.getReferTo().forEach( ref -> {
                    referTos.append(ref.getName()).append(",");
                });
                fieldMap.put("referTo", referTos.toString());
            }
            fieldMap.put("reserved", MDHelper.isReservedField(field));

            resultList.add(fieldMap);
        });

        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/getEntityProps")
    public ResponseBean<Map<String, Object>> getEntityProps(@RequestParam("entity") String entityName) {
        Map<String, Object> resultMap = new HashMap<>();
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        resultMap.put("name", entity.getName());
        resultMap.put("label", entity.getLabel());
        resultMap.put("entityCode", entity.getEntityCode());
        resultMap.put("physicalName", entity.getPhysicalName());
        if (entity.getNameField() == null) {
            resultMap.put("nameField", null);
        } else {
            resultMap.put("nameField", entity.getNameField().getLabel());
        }
        resultMap.put("detailEntityFlag", entity.isDetailEntityFlag());
        resultMap.put("mainEntity", entity.getMainEntity());
        resultMap.put("layoutable", entity.isLayoutable());
        resultMap.put("listable", entity.isListable());
        resultMap.put("authorizable", entity.isAuthorizable());
        resultMap.put("assignable", entity.isAssignable());
        resultMap.put("shareable", entity.isShareable());

        return ResponseHelper.ok(resultMap, "success");
    }

    @RequestMapping("/createEntity")
    public ResponseBean<Entity> createEntity(@RequestBody Entity entity,
                                             @RequestParam("mainEntity") String mainEntityName) {
        if (StringUtils.isBlank(entity.getName()) || StringUtils.isBlank(entity.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }

        entityManagerService.createEntity(entity, mainEntityName);
        return ResponseHelper.ok(entity, "success");
    }

    @RequestMapping("/updateEntityLabel")
    public ResponseBean<Boolean> updateEntityLabel(@RequestParam("entity") String entityName,
                                                   @RequestParam("entityLabel") String entityLabel) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        if (!entity.getLabel().equals(entityLabel.trim())) {
            entity.setLabel( entityLabel.trim() );
            entityManagerService.updateEntity(entity);
        }

        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

    @RequestMapping("/entityCanBeDeleted")
    public ResponseBean<Boolean> entityCanBeDeleted(@RequestParam("entity") String entityName) {
        Boolean checkResult = entityManagerService.entityCanBeDeleted(entityName);
        return ResponseHelper.ok(checkResult, "success");
    }

    @RequestMapping("/deleteEntity")
    public ResponseBean<Boolean> deleteEntity(@RequestParam("entity") String entityName) throws JsonProcessingException {
        boolean found = entityManagerService.getMetadataManager().containsEntity(entityName);
        if (!found) {
            throw new IllegalArgumentException("实体" + entityName + "不存在!");
        }

        entityManagerService.deleteEntity(entityName);
        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

    @RequestMapping("/getTextFieldListOfEntity")
    public ResponseBean<List<Map<String, Object>>> getEntityTextFieldList(@RequestParam("entity") String entityName) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        List<Map<String, Object>> resultList = new ArrayList<>();
        entity.getFieldSet().forEach( field -> {
            if (field.getType() == FieldTypes.TEXT) {
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("name", field.getName());
                fieldMap.put("label", field.getLabel());
                fieldMap.put("nameFieldFlag", field.isNameFieldFlag());
                resultList.add(fieldMap);
            }
        });

        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/updateEntityNameField")
    public ResponseBean<Boolean> updateEntityNameField(@RequestParam("entity") String entityName,
                                                       @RequestParam("nameField") String nameFieldName) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        Field newNameField = entity.getField(nameFieldName);
        entityManagerService.updateEntityNameField(entity, newNameField);

        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

    @RequestMapping("/addField")
    public ResponseBean<Field> addPlainField(@RequestBody Field field, @RequestParam("entity") String entityName) {
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }

        field.setPhysicalName("c_" + field.getName());
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        entityManagerService.createPlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/updateField")
    public ResponseBean<Field> updatePlainField(@RequestBody Field field, @RequestParam("entity") String entityName) {
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }

        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        entityManagerService.updatePlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/addOptionField")
    public ResponseBean<Field> addOptionField(@RequestBody OptionRequestBody requestBody, @RequestParam("entity") String entityName) {
        Field field = requestBody.getField();
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }
        field.setPhysicalName("c_" + field.getName());
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        entityManagerService.createOptionField(entity.getEntityCode(), field, requestBody.getOptionList());

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/updateOptionField")
    public ResponseBean<Field> updateOptionField(@RequestBody OptionRequestBody requestBody, @RequestParam("entity") String entityName) {
        Field field = requestBody.getField();
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        entityManagerService.updateOptionField(entity.getEntityCode(), field, requestBody.getOptionList());

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/addTagField")
    public ResponseBean<Field> addTagField(@RequestBody TagRequestBody requestBody, @RequestParam("entity") String entityName) {
        Field field = requestBody.getField();
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }
        field.setPhysicalName("c_" + field.getName());
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        entityManagerService.createTagField(entity.getEntityCode(), field, requestBody.getTagList());

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/updateTagField")
    public ResponseBean<Field> updateTagField(@RequestBody TagRequestBody requestBody, @RequestParam("entity") String entityName) {
        Field field = requestBody.getField();
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        entityManagerService.updateTagField(entity.getEntityCode(), field, requestBody.getTagList());

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/addRefField")
    public ResponseBean<Field> addReferenceField(@RequestBody Field field, @RequestParam("entity") String entityName,
                                                 @RequestParam("refEntity") String refEntityName) {
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }

        field.setPhysicalName("c_" + field.getName());
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        Entity refEntity = entityManagerService.getMetadataManager().getEntity(refEntityName);
        Set<Entity> referTo = new LinkedHashSet<Entity>(){{
            add(refEntity);
        }};
        field.setReferTo(referTo);
        field.setOwner(entity);
        entityManagerService.createPlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/updateRefField")
    public ResponseBean<Field> updateReferenceField(@RequestBody Field field, @RequestParam("entity") String entityName,
                                                    @RequestParam("refEntity") String refEntityName) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        entityManagerService.updatePlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/addAnyRefField")
    public ResponseBean<Field> addAnyReferenceField(@RequestBody Field field, @RequestParam("entity") String entityName,
                                                 @RequestParam("referTo") String referToEntities) {
        if (StringUtils.isBlank(field.getName()) || StringUtils.isBlank(field.getLabel())) {
            throw new IllegalArgumentException("name or label must be not null");
        }

        field.setPhysicalName("c_" + field.getName());
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        field.setOwner(entity);
        String[] referToArray = referToEntities.split(",");
        Set<Entity> referTo = new LinkedHashSet<>();
        for(String refEntityName: referToArray) {
            if (StringUtils.isNotBlank(refEntityName)) {
                Entity refEntity = entityManagerService.getMetadataManager().getEntity(refEntityName);
                referTo.add(refEntity);
            }
        }
        field.setReferTo(referTo);
        entityManagerService.createPlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/updateAnyRefField")
    public ResponseBean<Field> updateAnyReferenceField(@RequestBody Field field, @RequestParam("entity") String entityName,
                                                    @RequestParam("referTo") String referToEntities) {
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        String[] referToArray = referToEntities.split(",");
        Set<Entity> referTo = new LinkedHashSet<>();
        for(String refEntityName: referToArray) {
            if (StringUtils.isNotBlank(refEntityName)) {
                Entity refEntity = entityManagerService.getMetadataManager().getEntity(refEntityName);
                referTo.add(refEntity);
            }
        }
        field.setReferTo(referTo);
        entityManagerService.updatePlainField(entity.getEntityCode(), field);

        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/fieldCanBeDeleted")
    public ResponseBean<Boolean>  fieldCanBeDeleted(@RequestParam("entity") String entityName,
                                                    @RequestParam("field") String fieldName) {
        Boolean checkResult = entityManagerService.fieldCanBeDeleted(entityName, fieldName);
        return ResponseHelper.ok(checkResult, "success");
    }

    @RequestMapping("/fieldCanBeEdited")
    public ResponseBean<Boolean>  fieldCanBeEdited(@RequestParam("entity") String entityName,
                                                    @RequestParam("field") String fieldName) {
        Boolean checkResult = entityManagerService.fieldCanBeEdited(entityName, fieldName);
        return ResponseHelper.ok(checkResult, "success");
    }

    @RequestMapping("/deleteField")
    public ResponseBean<Boolean> deleteField(@RequestParam("entity") String entityName,
                                             @RequestParam("field") String fieldName) throws IllegalAccessException {
        entityManagerService.deleteField(entityName, fieldName);
        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

    /* 获取一对一引用字段附加属性 */
    @RequestMapping("/getRefFieldExtras")
    public ResponseBean<Map<String, Object>> getReferenceFieldExtras(@RequestParam("entity") String entityName,
                                                                     @RequestParam("field") String fieldName) {
        Map<String, Object> resultMap = new HashMap<>();
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        Field field = entity.getField(fieldName);
        if (field.getType() != FieldTypes.REFERENCE) {
            return ResponseHelper.fail(resultMap, "field type mismatch");
        }

        Entity refEntity = field.getReferTo().iterator().next();
        resultMap.put("refEntityName", refEntity.getName());
        resultMap.put("refEntityLabel", refEntity.getLabel());
        resultMap.put("refEntityFullName", refEntity.getLabel() + "(" + refEntity.getName() + ")");
        resultMap.put("currentRefEntity", refEntity.getName());
        ReferenceModel referenceModel = field.getReferenceSetting().get(0);
        StringBuilder refEntityAndFieldsSb = new StringBuilder().append( refEntity.getLabel() ).append("[");
        List<Map<String, String>> selectedFieldItems = new ArrayList<>();
        for (String fldName : referenceModel.getFieldList()) {
            Map<String, String> selectedFieldMap = new HashMap<>();
            String selectedFieldLabel = refEntity.getField(fldName).getLabel();
            refEntityAndFieldsSb.append(selectedFieldLabel).append(",");
            selectedFieldMap.put("name", fldName);
            selectedFieldMap.put("label", selectedFieldLabel);
            selectedFieldItems.add(selectedFieldMap);
        }
        refEntityAndFieldsSb.append("]");
        resultMap.put("refEntityAndFields", refEntityAndFieldsSb.toString());
        resultMap.put("selectedFieldItems", selectedFieldItems);

        List<Map<String, Object>> fieldMapList = new ArrayList<>();
        refEntity.getFieldSet().forEach(fieldItem -> {
            if (fieldItem.getType() != FieldTypes.PRIMARYKEY) {
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("name", fieldItem.getName());
                fieldMap.put("label", fieldItem.getLabel());
                fieldMap.put("type", fieldItem.getType().getName());
                fieldMap.put("reserved", MDHelper.isReservedField(field));
                fieldMapList.add(fieldMap);
            }
        });
        resultMap.put("fieldItems", fieldMapList);

        return ResponseHelper.ok(resultMap, "success");
    }

    @RequestMapping("/getField")
    public ResponseBean<Field> getFieldObject(@RequestParam("entity") String entityName, @RequestParam("field") String fieldName) {
        Field field = entityManagerService.getMetadataManager().getEntity(entityName).getField(fieldName);
        return ResponseHelper.ok(field, "success");
    }

    @RequestMapping("/getOptionFields")
    public ResponseBean<List<Map<String, Object>>> getOptionFields() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Collection<Entity> entityCollection = entityManagerService.getMetadataManager().getEntitySet();
        boolean foundFlag = false;
        for (Entity entity : entityCollection) {
            Collection<Field> fieldCollection = entity.getFieldSet();
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("entityName", entity.getName());
            entityMap.put("entityLabel", entity.getLabel());
            List<Map<String, String>> fieldMapList = new ArrayList<>();
            foundFlag = false;
            for (Field field : fieldCollection) {
                if (field.getType() == FieldTypes.OPTION) {
                    foundFlag = true;
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("fieldName", field.getName());
                    fieldMap.put("fieldLabel", field.getLabel());
                    fieldMapList.add(fieldMap);
                }
            }

            if (foundFlag) {
                entityMap.put("fieldList", fieldMapList);
                resultList.add(entityMap);
            }
        }

        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/getOptionItems")
    public ResponseBean<List<Map<String, Object>>> getOptionItems(@RequestParam("entity") String entityName,
                                                                 @RequestParam("field")String fieldName) {
        List<OptionModel> optionModelList = optionManagerService.getOptionList(entityName, fieldName);
        List<Map<String, Object>> resultList= new ArrayList<>();
        optionModelList.forEach(om -> {
            Map<String, Object> optionMap = new HashMap<>();
            optionMap.put("label", om.getLabel());
            optionMap.put("value", om.getValue());
            optionMap.put("saved", Boolean.TRUE);
            resultList.add(optionMap);
        });
        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/saveOptionItems")
    public ResponseBean<Boolean> saveOptionItems(@RequestParam("entity") String entityName,
                                                 @RequestParam("field")String fieldName,
                                                 @RequestBody List<Map<String, Object>> optionItems) {
        List<KeyValueEntry<String, Integer>> optionList = new ArrayList<>();
        for (Map<String, Object> oi : optionItems) {
            KeyValueEntry<String, Integer> kv = new KeyValueEntry<String, Integer>((String) oi.get("label"),
                    (Integer) oi.get("value"));
            optionList.add(kv);
        }
        boolean result = optionManagerService.saveOptionList(entityName, fieldName, optionList);
        if (result) {
            return ResponseHelper.ok(Boolean.TRUE, "success");
        } else {
            return ResponseHelper.fail(Boolean.FALSE, "request failed");
        }
    }

    @RequestMapping("/getTagFields")
    public ResponseBean<List<Map<String, Object>>> getTagFields() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        Collection<Entity> entityCollection = entityManagerService.getMetadataManager().getEntitySet();
        boolean foundFlag = false;
        for (Entity entity : entityCollection) {
            Collection<Field> fieldCollection = entity.getFieldSet();
            Map<String, Object> entityMap = new HashMap<>();
            entityMap.put("entityName", entity.getName());
            entityMap.put("entityLabel", entity.getLabel());
            List<Map<String, String>> fieldMapList = new ArrayList<>();
            foundFlag = false;
            for (Field field : fieldCollection) {
                if (field.getType() == FieldTypes.TAG) {
                    foundFlag = true;
                    Map<String, String> fieldMap = new HashMap<>();
                    fieldMap.put("fieldName", field.getName());
                    fieldMap.put("fieldLabel", field.getLabel());
                    fieldMapList.add(fieldMap);
                }
            }

            if (foundFlag) {
                entityMap.put("fieldList", fieldMapList);
                resultList.add(entityMap);
            }
        }

        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/getTagItems")
    public ResponseBean<List<Map<String, Object>>> getTagItems(@RequestParam("entity") String entityName,
                                                                  @RequestParam("field")String fieldName) {
        List<TagModel> tagModelList = tagManagerService.getTagList(entityName, fieldName);
        List<Map<String, Object>> resultList= new ArrayList<>();
        tagModelList.forEach(tm -> {
            Map<String, Object> tagMap = new HashMap<>();
            tagMap.put("label", tm.getValue());
            tagMap.put("value", tm.getValue());
            tagMap.put("saved", Boolean.TRUE);
            resultList.add(tagMap);
        });
        return ResponseHelper.ok(resultList, "success");
    }

    @RequestMapping("/saveTagItems")
    public ResponseBean<Boolean> saveTagItems(@RequestParam("entity") String entityName,
                                                 @RequestParam("field")String fieldName,
                                                 @RequestBody List<Map<String, Object>> tagItems) {
        List<String> tagModelList = new ArrayList<>();
        tagItems.forEach(ti -> {
            tagModelList.add((String) ti.get("value"));
        });
        boolean result = tagManagerService.saveTagList(entityName, fieldName, tagModelList);
        if (result) {
            return ResponseHelper.ok(Boolean.TRUE, "success");
        } else {
            return ResponseHelper.fail(Boolean.FALSE, "request failed");
        }
    }

    /* Pojo类代码生成 */
    @RequestMapping("/genePojo")
    public ResponseBean<String> generatePojo(@RequestParam("entity") String entityName) {


        return null;
    }


}
