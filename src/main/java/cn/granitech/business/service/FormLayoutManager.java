package cn.granitech.business.service;

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.web.pojo.FormLayout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/30 17:50
 * @Version: 1.0
 */

@Service
public class FormLayoutManager {

    private static final String LayoutName = "layoutName";
    private static final String EntityCode = "entityCode";
    private static final String LayoutJson = "layoutJson";

    @Autowired
    EntityManagerService entityManagerService;

    @Autowired
    PersistenceManager persistenceManager;

    @Autowired
    CrudService crudService;

    //TODO: 实现表单缓存管理

    @Transactional
    public ID saveLayout(String entityName, String layoutJson) {
        EntityRecord record = crudService.newRecord(SystemEntities.FormLayout);
        int entityCode = entityManagerService.getMetadataManager().getEntity(entityName).getEntityCode();
        record.setFieldValue(LayoutName, "默认表单布局");
        record.setFieldValue(EntityCode, entityCode);
        record.setFieldValue(LayoutJson, layoutJson);
        return crudService.create(record);
    }

    @Transactional
    public ID updateLayout(String layoutId, String layoutJson) {
        EntityRecord record = crudService.queryById( ID.valueOf(layoutId) );
        record.setFieldValue(LayoutJson, layoutJson);
        crudService.update(record);
        return record.id();
    }

    @Transactional
    public FormLayout getLayout(String entityName) throws InstantiationException,
            IllegalAccessException, IOException {
        //TODO: 实现表单缓存管理
        return crudService.getFormLayout(entityName);
    }

    @Transactional
    public Boolean deleteEntityLayout(int entityCode) {
        String rawDeleteFilter = String.format("entityCode=%d", entityCode);
        persistenceManager.batchDelete(SystemEntities.FormLayout, rawDeleteFilter);

        return true;
    }

}
