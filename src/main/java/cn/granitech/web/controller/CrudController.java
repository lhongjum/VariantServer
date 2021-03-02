package cn.granitech.web.controller;

import cn.granitech.business.service.CrudService;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.pojo.Pagination;
import cn.granitech.variantorm.pojo.QuerySchema;
import cn.granitech.web.pojo.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/10 15:27
 * @Version: 1.0
 */

@RestController
@RequestMapping("/crud")
public class CrudController extends BaseController {

    @Autowired
    CrudService crudService;

    @RequestMapping("/listQuery")
    public ResponseBean<ListQueryResult> queryListMap(@RequestBody ListQueryRequestBody requestBody) {
        QuerySchema querySchema = new QuerySchema();
        querySchema.setMainEntity(requestBody.getMainEntity());
        querySchema.setSelectFields(requestBody.getFieldsList());
        querySchema.setFilter(requestBody.getFilter());
        Pagination pagination = new Pagination(requestBody.getPageNo(), requestBody.getPageSize());
        ListQueryResult queryResult = new ListQueryResult();
        List<Map<String, Object>> resultList = crudService.queryListMap(querySchema, pagination, queryResult);
        queryResult.setDataList(resultList);
        queryResult.setPagination(pagination);

        return new ResponseBean<>(200, null, "success", queryResult);
    }

    @RequestMapping("/initDataList")
    public ResponseBean<ListQueryResult> initDataListView(@RequestParam("entity") String entityName)
            throws IllegalAccessException, InstantiationException, JsonProcessingException {
        ListQueryResult queryResult = crudService.getDataLiveViewAndData(entityName);

        return new ResponseBean<>(200, null, "success", queryResult);
    }

    @RequestMapping("/refFieldQuery")
    public ResponseBean<ReferenceQueryResult> queryReferenceFieldRecord(@RequestParam("entity") String entityName,
                                                                        @RequestParam("refField") String refFieldName,
                                                                        @RequestParam("pageNo") Integer pageNo,
                                                                        @RequestParam("pageSize") Integer pageSize) {
        ReferenceQueryResult queryResult = crudService.queryReferenceFieldRecord(entityName, refFieldName, pageNo, pageSize);
        return ResponseHelper.ok(queryResult, "success");
    }

    @RequestMapping("/formCreateQuery")
    public ResponseBean<FormQueryResult> queryFormDataForCreate(@RequestParam("entity") String entityName) throws
            IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = crudService.queryFormDataForCreate(entityName);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping("/formUpdateQuery")
    public ResponseBean<FormQueryResult> queryFormDataForUpdate(@RequestParam("entity") String entityName,
                                                                @RequestParam("id") String recordId)
            throws IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = crudService.queryFormDataForUpdate(entityName, recordId);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping("/formViewQuery")
    public ResponseBean<FormQueryResult> queryFormDataForView(@RequestParam("entity") String entityName,
                                                              @RequestParam("id") String recordId)
            throws IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = crudService.queryFormDataForUpdate(entityName, recordId);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping("/saveRecord")
    public ResponseBean<FormQueryResult> saveRecord(@RequestParam("entity") String entityName,
                                                 @RequestParam("id") String recordId,
                                                 @RequestBody Map<String, Object> dataMap)
            throws IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = crudService.saveRecord(entityName, recordId, dataMap);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping("/deleteRecord")
    public ResponseBean<Boolean> deleteRecord(@RequestParam("id") String recordId) {
        Boolean result = crudService.delete(ID.valueOf(recordId) );
        return ResponseHelper.ok(result, "success");
    }

}
