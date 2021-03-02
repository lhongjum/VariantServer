package cn.granitech.business.service;

import cn.granitech.variantorm.persistence.PersistenceManager;
import cn.granitech.variantorm.pojo.ReferenceCacheEO;
import cn.granitech.variantorm.pojo.ReferenceListCacheEO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/11/27 14:04
 * @Version: 1.0
 */

//@Service
@Component
public class QueryCacheManager {


    @Autowired
    PersistenceManager pm;

    @EventListener
    //@Async  /* 是否需要异步执行？ */
    public void updateIDName(ReferenceCacheEO refCEO) {
        pm.getQueryCache().updateIDName(refCEO.getRefId(), refCEO.getIdName());
    }

    @EventListener
    //@Async  /* 是否需要异步执行？ */
    public void updateIDNameList(ReferenceListCacheEO refListCEO) {
        System.out.println("update roles cache...");
        pm.getQueryCache().updateIDNameList(refListCEO.getEntityName(), refListCEO.getRefFieldName(),
                refListCEO.getRecordId(), refListCEO.getIdNameList());
    }

}
