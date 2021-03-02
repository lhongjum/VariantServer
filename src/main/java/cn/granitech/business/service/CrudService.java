package cn.granitech.business.service;

import cn.granitech.util.CommonHelper;
import cn.granitech.util.JsonHelper;
import cn.granitech.variantorm.constant.CommonFields;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.FieldType;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.metadata.fieldtype.FieldTypes;
import cn.granitech.variantorm.persistence.DataQuery;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.persistence.RecordQuery;
import cn.granitech.variantorm.persistence.cache.QueryCache;
import cn.granitech.variantorm.pojo.*;
import cn.granitech.web.pojo.*;
import cn.granitech.web.pojo.layout.FieldProps;
import cn.granitech.web.pojo.layout.LayoutJsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/10 12:31
 * @Version: 1.0
 */

@Service
//@Transactional
public class CrudService extends BaseService {

    @Autowired
    PersistenceManager pm;

    @Autowired
    UserService userService;

    @Autowired
    OptionManagerService optionManagerService;

    @Autowired
    TagManagerService tagManagerService;

    private static final String FIELD_SP = ",";

    public EntityRecord newRecord(String entityName) {
        return pm.newRecord(entityName);
    }

    public <T extends BasePojo> T newPojo(String entityName, Class<T> pojoClass) throws IllegalAccessException,
            InstantiationException {
        T pojo = pojoClass.newInstance();
        EntityRecord entityRecord = newRecord(entityName);
        pojo.setEntityRecord(entityRecord);
        return pojo;
    }

    @Transactional
    public ID create(EntityRecord record) {
        Entity entity = record.getEntity();
        ID callerId = ID.valueOf(callerContext.getCallerId());
        if (entity.containsField(CommonFields.createdOn)) {
            record.setFieldValue(CommonFields.createdOn, new java.util.Date());
        }
        if (entity.containsField(CommonFields.createdBy)) {
            record.setFieldValue(CommonFields.createdBy, callerId);
        }
        if (entity.containsField(CommonFields.modifiedOn)) {
            record.setFieldValue(CommonFields.modifiedOn, null);
        }
        if (entity.containsField(CommonFields.modifiedBy)) {
            record.setFieldValue(CommonFields.modifiedBy, null);
        }
        if (entity.containsField(CommonFields.ownerUser)) {
            record.setFieldValue(CommonFields.ownerUser, callerId);
        }
        ID departmentId = userService.getDepartmentIdOfUser(callerId);
        if (entity.containsField(CommonFields.ownerDepartment)) {
            record.setFieldValue(CommonFields.ownerDepartment, departmentId);
        }

        pm.insert(record);
        return record.id();
    }

    @Transactional
    public <T extends BasePojo> ID create(T pojo) {
        return create(pojo.getEntityRecord());
    }

    @Transactional
    public EntityRecord update(EntityRecord record) {
        EntityRecord entityRecord = queryById( record.id() );  //先从数据库加载已有记录
        entityRecord.setNotModified();  //清除修改标志！！
        entityRecord.copyFrom(record);
        ID callerId = ID.valueOf(callerContext.getCallerId());
        Entity entity = record.getEntity();
        if (entity.containsField(CommonFields.modifiedOn)) {
            entityRecord.setFieldValue(CommonFields.modifiedOn, new Date());
        }
        if (entity.containsField(CommonFields.modifiedBy)) {
            entityRecord.setFieldValue(CommonFields.modifiedBy, callerId);
        }

        pm.update(entityRecord);
        return entityRecord;
    }

    @Transactional
    public <T extends BasePojo> T update(T pojo) {
        update(pojo.getEntityRecord());
        return pojo;
    }

    @Transactional
    public Boolean delete(ID recordId, String... entityName) {
        //TODO：是否要考虑级联删除？？（引用字段需要增加级联删除的设置！！）
        return pm.delete(recordId, entityName);
    }

    @Transactional
    public FormQueryResult saveRecord(String entityName, String recordId,  Map<String, Object> dataMap) throws IllegalAccessException,
            IOException, InstantiationException {
        EntityRecord entityRecord = newRecord(entityName);
        Entity entity = pm.getMetadataManager().getEntity(entityName);
        for (String dataKey : dataMap.keySet()) {
            if (!entity.containsField(dataKey)) {
                continue;
            }

            Field field = entity.getField(dataKey);
            entityRecord.setFieldValue(field.getName(), field.getType().fromJson( dataMap.get(dataKey) ));
        }

        if (StringUtils.isNotBlank(recordId)) {
            entityRecord.setFieldValue(entity.getIdField().getName(), ID.valueOf(recordId));
            update(entityRecord);
        } else {
            recordId = create(entityRecord).toString();
        }

        FormLayout formLayout = getFormLayout(entityName);
        if (formLayout == null) {
            throw new IllegalArgumentException("No formlayout found!");
        }
        String layoutJson = formLayout.getLayoutJson();
        LayoutJsonObject layoutJsonObject = JsonHelper.readJsonValue(layoutJson, new TypeReference<LayoutJsonObject>(){});
        List<String> layoutFields = layoutJsonObject.getFieldsOfLayout();
        List<String> queryFields = new ArrayList<String>() {{
            add(entity.getIdField().getName());
        }};
        for (String fldName : layoutFields) {
            if (!entity.containsField(fldName)) { /* 跳过布局中不存在的字段（可能已被删除） */
                continue;
            }
            queryFields.add(fldName);
        }
        EntityRecord savedRecord = queryById( ID.valueOf(recordId), queryFields.toArray(new String[]{}) );
        Map<String, String> labelsMap = savedRecord.getLabelsMap();

        List<String> deletedFields = new ArrayList<>();
        layoutFields.forEach(lf -> {
            if (!entity.containsField(lf)) {
                deletedFields.add(lf);
            }
        });

        //return new FormQueryResult(layoutJson, null, savedRecord, labelsMap);
        return new FormQueryResult(null, null, savedRecord, labelsMap, deletedFields);
    }

    @Transactional
    public EntityRecord queryById(ID entityId, String... fieldNames) {
        Entity entity = pm.getMetadataManager().getEntity( entityId.getEntityCode() );
        RecordQuery recordQuery = pm.createRecordQuery();
        String filter = String.format("[%s] = :entityId", entity.getIdField().getName());
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("entityId", entityId);
            //put("entityId", entityId.toString());  //传入ID对象会将参数转为二进制值格式，导致查询不出结果！！？
        }};
        List<EntityRecord> resultList = recordQuery.query(entity.getName(), filter, paramMap, null, null,
                fieldNames);
        return resultList.size() == 0 ? null : resultList.get(0);
    }

    @Transactional
    public List<EntityRecord> queryListRecord(String entityName, String filter, Map<String, Object> paramMap, String sort,
                                              Pagination pagination, String... fieldNames) {
        RecordQuery recordQuery = pm.createRecordQuery();
        return recordQuery.query(entityName, filter, paramMap, sort, pagination, fieldNames);
    }

    @Transactional
    public <T extends BasePojo> List<T> queryListPojo(Class<T> pojoClass, String entityName, String filter,
                                                      Map<String, Object> paramMap, String sort, Pagination pagination,
                                                      String... fieldNames) throws IllegalAccessException, InstantiationException {
        RecordQuery recordQuery = pm.createRecordQuery();
        List<EntityRecord> recordList = recordQuery.query(entityName, filter, paramMap, sort, pagination, fieldNames);
        List<T> pojoList = new ArrayList<>();
        for (EntityRecord record : recordList) {
            T pojo = pojoClass.newInstance();
            pojo.setEntityRecord(record);
            pojoList.add(pojo);
        }
        return pojoList;
    }

    private Boolean containsIdField(QuerySchema querySchema, Field idField) {
        String[] fieldArray = querySchema.getSelectFields().replaceAll(" ", "").split(FIELD_SP);
        for (String fldExp : fieldArray) {
            if (fldExp.equals(idField.getName())) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public List<Map<String, Object>> queryListMap(QuerySchema querySchema, Pagination pagination,
                                                  ListQueryResult listQueryResult) {
        Field idField = pm.getMetadataManager().getEntity(querySchema.getMainEntity()).getIdField();
        if (!containsIdField(querySchema, idField)) { //在查询字段中补充id字段！！
            querySchema.setSelectFields(idField.getName() + ", " + querySchema.getSelectFields());
        }
        DataQuery dataQuery = pm.createDataQuery();
        return dataQuery.query(querySchema, pagination);
    }

    @Transactional
    public ListQueryResult getDataLiveViewAndData(String entityName) throws InstantiationException,
            IllegalAccessException, JsonProcessingException {
        Entity dataEntity = pm.getMetadataManager().getEntity(entityName);
        int entityCode = dataEntity.getEntityCode();
        String filter = String.format("([entityCode] = %d) and ([viewName] = '默认视图')", entityCode);
        List<DataListView> pojoList = queryListPojo(DataListView.class, SystemEntities.DataListView, filter, null, null, null);

        List<TableHeaderColumn> columnList = new ArrayList<>();
        Pagination pagination = null;
        StringBuffer selectFieldsSB = new StringBuffer();
        boolean hasSelectFieldsFlag = false;
        //boolean hasPaginationFlag = false;
        if (pojoList.size() > 0) {
            String headerJson = pojoList.get(0).getHeaderJson();
            if (StringUtils.isNotBlank(headerJson)) {
                columnList = JsonHelper.readJsonValue(headerJson, new TypeReference<List<TableHeaderColumn>>(){});
                if ((columnList != null) && (columnList.size() > 0)) {
                    hasSelectFieldsFlag = true;
                }
            }

            String paginationJson = pojoList.get(0).getPaginationJson();
            if (StringUtils.isNotBlank(paginationJson)) {
                pagination = JsonHelper.readJsonValue(paginationJson, new TypeReference<Pagination>(){});
            }
        }
        if (pagination == null) {
            pagination = new Pagination(1, 20);
        }

        boolean firstSelectFieldFlag = true;
        if (!hasSelectFieldsFlag) {
            Collection<Field> fieldCollection = dataEntity.getFieldSet();
            columnList = new ArrayList<>();
            for (Field field : fieldCollection) {
                if (field.isDefaultMemberOfListFlag()) {
                    if (firstSelectFieldFlag) {
                        selectFieldsSB.append( field.getName() );
                        firstSelectFieldFlag = false;
                    } else {
                        selectFieldsSB.append(", ").append( field.getName() );
                    }

                    TableHeaderColumn thColumn = new TableHeaderColumn();
                    thColumn.setProp(field.getName());
                    thColumn.setLabel(field.getLabel());
                    thColumn.setType(field.getType().getName());
                    TableHeaderMapping.setHeaderColumnDefaultProps(field.getType().getName(), thColumn);
                    columnList.add(thColumn);
                }
            }
        } else {
            for (TableHeaderColumn thc : columnList) {
                if (firstSelectFieldFlag) {
                    selectFieldsSB.append( thc.getProp() );
                    firstSelectFieldFlag = false;
                } else {
                    selectFieldsSB.append(", ").append( thc.getProp() );
                }
            }
        }

        QuerySchema querySchema = new QuerySchema();
        querySchema.setMainEntity(entityName);
        querySchema.setSelectFields(selectFieldsSB.toString());
        querySchema.setFilter(null);

        Field idField = pm.getMetadataManager().getEntity(querySchema.getMainEntity()).getIdField();
        if (!containsIdField(querySchema, idField)) { //在查询字段中补充id字段！！
            querySchema.setSelectFields(idField.getName() + ", " + querySchema.getSelectFields());
        }

        String nameFieldName = dataEntity.getNameField() == null ? null : dataEntity.getNameField().getName();
        EntityBasicInfo entityBasicInfo = new EntityBasicInfo(dataEntity.getName(), dataEntity.getLabel(),
                dataEntity.getIdField().getName(), nameFieldName);
        List<Map<String, Object>> dataList = pm.createDataQuery().query(querySchema, pagination);
        return new ListQueryResult(dataList, pagination, columnList, entityBasicInfo);
    }

    @Transactional
    public ReferenceQueryResult queryReferenceFieldRecord(@RequestParam("entity") String entityName,
                                                          @RequestParam("refField") String refFieldName,
                                                          @RequestParam("pageNo") Integer pageNo,
                                                          @RequestParam("pageSize") Integer pageSize) {
        Entity entity = pm.getMetadataManager().getEntity(entityName);
        Field refField = entity.getField(refFieldName);
        ReferenceModel referenceModel = refField.getReferenceSetting().get(0);
        String refEntityName = referenceModel.getEntityName();
        Entity refEntity = pm.getMetadataManager().getEntity(refEntityName);
        List<Map<String, String>> columnList = new ArrayList<>();
        for (String fldName : referenceModel.getFieldList()) {
            Field columnFld = refEntity.getField(fldName);
            FieldType fldType = columnFld.getType();
            Map<String, String> columnMap = new HashMap<>();
            columnMap.put("prop", columnFld.getName());
            columnMap.put("label", columnFld.getLabel());
            columnMap.put("width", "160");
            if ((fldType == FieldTypes.INTEGER) || (fldType == FieldTypes.DECIMAL) || (fldType == FieldTypes.MONEY) ||
                    (fldType == FieldTypes.PERCENT) || (fldType == FieldTypes.DATE) || (fldType == FieldTypes.DATETIME)) {
                columnMap.put("align", "right");
            } else {
                columnMap.put("align", "center");
            }
            columnMap.put("type", fldType.getName());
            columnList.add(columnMap);
        }

        QuerySchema querySchema = new QuerySchema();
        querySchema.setMainEntity(refEntityName);
        StringBuilder fieldsSb = new StringBuilder();
        fieldsSb.append( refEntity.getIdField().getName() ).append(",");
        referenceModel.getFieldList().forEach( fName -> {
            fieldsSb.append(fName).append(",");
        });

        String idFieldName = refEntity.getIdField().getName();
        String nameFieldName = null;
        if (refEntity.getNameField() != null) {
            nameFieldName = refEntity.getNameField().getName();
            if (!referenceModel.getFieldList().contains( nameFieldName )) {
                fieldsSb.append( nameFieldName ).append(",");
            }
        } else {
            nameFieldName = idFieldName;
        }

        querySchema.setSelectFields(fieldsSb.toString());
        querySchema.setFilter("(1=1)");
        Pagination pagination = new Pagination(pageNo, pageSize);
        List<Map<String, Object>> dataList = queryListMap(querySchema, pagination, null);

        return new ReferenceQueryResult(columnList, dataList, pagination, idFieldName, nameFieldName);
    }

    @Transactional
    public FormLayout getFormLayout(String entityName) throws InstantiationException, IllegalAccessException, IOException {
        Entity flEntity = pm.getMetadataManager().getEntity(entityName);

        //PojoGenerator.generatePojoClass(flEntity, "cde.abc321.ok");

        int entityCode = flEntity.getEntityCode();
        String filter = String.format("([entityCode] = %d) and ([layoutName] = '默认表单布局')", entityCode);
        List<FormLayout> pojoList = queryListPojo(FormLayout.class, SystemEntities.FormLayout, filter, null, null, null);
        if (pojoList.size() > 0) {
            return pojoList.get(0);
        } else {
            return null;
        }
    }

    @Transactional
    public FormQueryResult queryFormDataForCreate(String entityName) throws IllegalAccessException,
            InstantiationException, IOException {
        FormLayout formLayout = getFormLayout(entityName);
        if (formLayout == null) {
            throw new IllegalArgumentException("No formlayout found!");
        }

        String layoutJson = formLayout.getLayoutJson();
        LayoutJsonObject layoutJsonObject = JsonHelper.readJsonValue(layoutJson, new TypeReference<LayoutJsonObject>(){});
        List<String> layoutFields = layoutJsonObject.getFieldsOfLayout();
        EntityRecord entityRecord = pm.newRecord(entityName);
        Map<String, FieldProps> fieldPropsMap = new HashMap<>();
        Entity entity = pm.getMetadataManager().getEntity(entityName);
        for (String fldName : layoutFields) {
            if (!entity.containsField(fldName)) { /* 跳过布局中不存在的字段（可能已被删除） */
                continue;
            }

            Field field = entity.getField(fldName);
            FieldProps fieldProps = CommonHelper.copyFieldProps(field);
            fieldPropsMap.put(fldName, fieldProps);
            entityRecord.setFieldValue(fldName, null);
            if ((field.getType() == FieldTypes.REFERENCE) || (field.getType() == FieldTypes.ANYREFERENCE)) {
                entityRecord.setFieldLabel(fldName, "");
            } else if (field.getType() == FieldTypes.REFERENCELIST) {
                entityRecord.setFieldLabel(fldName, "");
            } else if (field.getType() == FieldTypes.OPTION) {
                List<OptionModel> optionModelList = optionManagerService.getOptionList(entityName, fldName);
                fieldProps.setOptionList(optionModelList);
            } else if (field.getType() == FieldTypes.TAG) {
                List<TagModel> tagModelList = tagManagerService.getTagList(entityName, fldName);
                fieldProps.setTagList(tagModelList);
            }
        }
        setCreatorInfo(entity, entityRecord);
        Map<String, String> labelsMap = entityRecord.getLabelsMap();

        List<String> deletedFields = new ArrayList<>();
        layoutFields.forEach(lf -> {
            if (!entity.containsField(lf)) {
                deletedFields.add(lf);
            }
        });

        return new FormQueryResult(layoutJson, fieldPropsMap, entityRecord, labelsMap, deletedFields);
    }

    private void setCreatorInfo(Entity entity, EntityRecord record) {
        ID callerId = ID.valueOf(callerContext.getCallerId());
        QueryCache queryCache = pm.getQueryCache();
        if (entity.containsField(CommonFields.createdBy)) {
            record.setFieldValue(CommonFields.createdBy, callerId);
            record.setFieldLabel(CommonFields.createdBy, userService.getUserName(callerId));
        }
        if (entity.containsField(CommonFields.ownerUser)) {
            record.setFieldValue(CommonFields.ownerUser, callerId);
            record.setFieldLabel(CommonFields.ownerUser, userService.getUserName(callerId));
        }
        ID departmentId = userService.getDepartmentIdOfUser(callerId);
        if (entity.containsField(CommonFields.ownerDepartment)) {
            record.setFieldValue(CommonFields.ownerDepartment, departmentId);
            record.setFieldLabel(CommonFields.ownerDepartment, userService.getDepartmentNameOfUser(callerId));
        }
    }

    @Transactional
    public FormQueryResult queryFormDataForUpdate(String entityName, String recordId) throws IllegalAccessException,
            IOException, InstantiationException {
        FormLayout formLayout = getFormLayout(entityName);
        if (formLayout == null) {
            throw new IllegalArgumentException("No formlayout found!");
        }

        String layoutJson = formLayout.getLayoutJson();
        LayoutJsonObject layoutJsonObject = JsonHelper.readJsonValue(layoutJson, new TypeReference<LayoutJsonObject>(){});
        List<String> layoutFields = layoutJsonObject.getFieldsOfLayout();
        Map<String, FieldProps> fieldPropsMap = new HashMap<>();
        Entity entity = pm.getMetadataManager().getEntity(entityName);
        List<String> queryFields = new ArrayList<String>() {{
            add(entity.getIdField().getName());
        }};
        for (String fldName : layoutFields) {
            if (!entity.containsField(fldName)) { /* 跳过布局中不存在的字段（可能已被删除） */
                continue;
            }

            Field field = entity.getField(fldName);
            FieldProps fieldProps = CommonHelper.copyFieldProps(field);
            if (field.getType() == FieldTypes.OPTION) {
                List<OptionModel> optionModelList = optionManagerService.getOptionList(entityName, fldName);
                fieldProps.setOptionList(optionModelList);
            } else if (field.getType() == FieldTypes.TAG) {
                List<TagModel> tagModelList = tagManagerService.getTagList(entityName, fldName);
                fieldProps.setTagList(tagModelList);
            }
            fieldPropsMap.put(fldName, fieldProps);
            queryFields.add(fldName);
        }

        EntityRecord entityRecord = queryById( ID.valueOf(recordId), queryFields.toArray(new String[]{}) );
        Map<String, String> labelsMap = entityRecord.getLabelsMap();

        List<String> deletedFields = new ArrayList<>();
        layoutFields.forEach(lf -> {
            if (!entity.containsField(lf)) {
                deletedFields.add(lf);
            }
        });

        return new FormQueryResult(layoutJson, fieldPropsMap, entityRecord, labelsMap, deletedFields);
    }

}
