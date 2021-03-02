package cn.granitech.business.service;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/10/14 12:31
 * @Version: 1.0
 */

import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.MetadataManager;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.pojo.OptionModel;
import cn.granitech.web.pojo.KeyValueEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理单选字段的增删改查操作及缓存同步
 */

@Service
public class OptionManagerService {

    private final static String entityNameFld = "entityName";
    private final static String fieldNameFld = "fieldName";
    private final static String labelFld = "label";
    private final static String valueFld = "value";
    private final static String displayOrderFld = "displayOrder";

    @Autowired
    PersistenceManager persistenceManager;

    public MetadataManager getMetadataManager() {
        return persistenceManager.getMetadataManager();
    }

    @Transactional
    public boolean saveOptionList(String entityName, String fieldName, List<KeyValueEntry<String, Integer>> optionList) {
        // 删除旧的选项值
        String deleteFilter = String.format(" (entityName = '%s') AND (fieldName = '%s') ", entityName, fieldName);
        persistenceManager.batchDelete(SystemEntities.OptionItem, deleteFilter);

        int displayOrder = 1;
        for (KeyValueEntry<String, Integer> optionKv : optionList) {
            EntityRecord optionRecord = persistenceManager.newRecord(SystemEntities.OptionItem);
            optionRecord.setFieldValue(entityNameFld, entityName);
            optionRecord.setFieldValue(fieldNameFld, fieldName);
            optionRecord.setFieldValue(labelFld, optionKv.getKey());
            optionRecord.setFieldValue(valueFld, optionKv.getValue());
            optionRecord.setFieldValue(displayOrderFld, displayOrder);

            persistenceManager.insert(optionRecord);
            displayOrder += 1;
        }
        persistenceManager.getOptionCacheManager().reloadOptionsByField(entityName, fieldName);

        return true;
    }

    @Transactional
    public List<OptionModel> getOptionList(String entityName, String fieldName) {
        return persistenceManager.getOptionCacheManager().getOptions(entityName, fieldName);
    }

}
