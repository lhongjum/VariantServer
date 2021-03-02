package cn.granitech.business.service;

import cn.granitech.interceptor.CallerContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/28 16:43
 * @Version: 1.0
 */

public abstract class BaseService {

    @Autowired
    protected CallerContext callerContext;

}
