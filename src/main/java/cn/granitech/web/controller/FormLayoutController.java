package cn.granitech.web.controller;

import cn.granitech.business.service.CrudService;
import cn.granitech.business.service.EntityManagerService;
import cn.granitech.business.service.FormLayoutManager;
import cn.granitech.util.CommonHelper;
import cn.granitech.util.JsonHelper;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.variantorm.pojo.Entity;
import cn.granitech.variantorm.pojo.Field;
import cn.granitech.web.pojo.FormLayout;
import cn.granitech.web.pojo.FormQueryResult;
import cn.granitech.web.pojo.ResponseBean;
import cn.granitech.web.pojo.layout.FieldProps;
import cn.granitech.web.pojo.layout.LayoutJsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/11 17:57
 * @Version: 1.0
 */

@RestController
@RequestMapping("/formLayout")
public class FormLayoutController extends BaseController {

    @Autowired
    EntityManagerService entityManagerService;

//    @Autowired
//    CrudService crudService;

    @Autowired
    FormLayoutManager formLayoutManager;

    @RequestMapping("/save")
    public ResponseBean<ID> saveLayout(@RequestParam("entity") String entityName, @RequestBody String layoutJson) {
        ID layoutId = formLayoutManager.saveLayout(entityName, layoutJson);
        return new ResponseBean<>(200, null, "success", layoutId);
    }

    @RequestMapping("/update")
    public ResponseBean<ID> updateLayout(@RequestParam("layoutId") String layoutId, @RequestBody String layoutJson) {
        ID savedId = formLayoutManager.updateLayout(layoutId, layoutJson);
        return new ResponseBean<>(200, null, "success", savedId);
    }

    @RequestMapping("/get")
    public ResponseBean<FormLayout> getLayout(@RequestParam("entity") String entityName) throws InstantiationException,
            IllegalAccessException, IOException {
        FormLayout formLayout = formLayoutManager.getLayout(entityName);
        if (formLayout == null) {
            return new ResponseBean<>(200, null, "No formlayout found!", null);
        } else {
            return new ResponseBean<>(200, null, "success", formLayout);
        }
    }

    @RequestMapping("/previewLayout")
    public ResponseBean<FormQueryResult> previewLayout(@RequestParam("entity") String entityName) {
        Map<String, FieldProps> fieldPropsMap = new HashMap<>();
        Entity entity = entityManagerService.getMetadataManager().getEntity(entityName);
        entity.getFieldSet().forEach(field -> {
            FieldProps fieldProps = CommonHelper.copyFieldProps(field);
            fieldPropsMap.put(field.getName(), fieldProps);
        });

        FormQueryResult formQueryResult = new FormQueryResult(null, fieldPropsMap, null, null, null);
        return ResponseHelper.ok(formQueryResult, "success");
    }

}
