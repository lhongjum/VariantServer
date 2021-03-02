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
import cn.granitech.variantorm.pojo.TagModel;
import cn.granitech.web.pojo.KeyValueEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 管理多选字段的增删改查操作及缓存同步
 */

@Service
public class TagManagerService {

    private final static String entityNameFld = "entityName";
    private final static String fieldNameFld = "fieldName";
    private final static String valueFld = "value";
    private final static String displayOrderFld = "displayOrder";

    @Autowired
    PersistenceManager persistenceManager;

    public MetadataManager getMetadataManager() {
        return persistenceManager.getMetadataManager();
    }

    @Transactional
    public boolean saveTagList(String entityName, String fieldName, List<String> tagList) {
        // 删除旧的选项值
        String deleteFilter = String.format(" (entityName = '%s') AND (fieldName = '%s') ", entityName, fieldName);
        persistenceManager.batchDelete(SystemEntities.TagItem, deleteFilter);

        int displayOrder = 1;
        for (String tagText : tagList) {
            EntityRecord tagRecord = persistenceManager.newRecord(SystemEntities.TagItem);
            tagRecord.setFieldValue(entityNameFld, entityName);
            tagRecord.setFieldValue(fieldNameFld, fieldName);
            tagRecord.setFieldValue(valueFld, tagText);
            tagRecord.setFieldValue(displayOrderFld, displayOrder);

            persistenceManager.insert(tagRecord);
            displayOrder += 1;
        }
        persistenceManager.getTagCacheManager().reloadTagsByField(entityName, fieldName);

        return true;
    }

    public List<TagModel> getTagList(String entityName, String fieldName) {
        return persistenceManager.getTagCacheManager().getTags(entityName, fieldName);
    }

}
