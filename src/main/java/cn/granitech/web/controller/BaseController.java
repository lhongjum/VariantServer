package cn.granitech.web.controller;

import cn.granitech.business.service.CrudService;
import cn.granitech.interceptor.CallerContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/28 16:44
 * @Version: 1.0
 */

public abstract class BaseController {

    @Autowired
    protected CallerContext callerContext;

}
