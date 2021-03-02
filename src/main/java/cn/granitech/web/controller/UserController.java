package cn.granitech.web.controller;

import cn.granitech.business.service.CrudService;
import cn.granitech.business.service.UserService;
import cn.granitech.interceptor.CallerContext;
import cn.granitech.interceptor.SessionIPChecker;
import cn.granitech.util.ResponseHelper;
import cn.granitech.variantorm.constant.SystemEntities;
import cn.granitech.variantorm.metadata.ID;
import cn.granitech.variantorm.persistence.EntityRecord;
import cn.granitech.web.constant.SessionNames;
import cn.granitech.web.pojo.FormQueryResult;
import cn.granitech.web.pojo.LoginInfo;
import cn.granitech.web.pojo.ResponseBean;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/25 13:40
 * @Version: 1.0
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    CrudService crudService;

    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public ResponseBean login(HttpServletRequest request, @RequestBody LoginInfo loginInfo) {
        if ((loginInfo == null) || StringUtils.isBlank(loginInfo.getUser()) ||
                StringUtils.isBlank(loginInfo.getPassword())) {
            return new ResponseBean<String>(403, "error", "", "");
        }

        //
        String userId = getLoginUserId(loginInfo.getUser().trim(), loginInfo.getPassword().trim());
        if (StringUtils.isNotBlank(userId)) {
            if (request.getSession(false) != null) {
                request.changeSessionId();
                //System.out.println( request.getSession().getId() );
                HttpSession session = request.getSession();
                session.setAttribute(SessionNames.LoginUserId, userId);
                SessionIPChecker.addSessionIdIP(request, session.getId());
            } else {
                HttpSession session = request.getSession(true);
                session.setAttribute(SessionNames.LoginUserId, userId);
                SessionIPChecker.addSessionIdIP(request, session.getId());
            }

            return new ResponseBean<String>(200, null, "success", userId.toString());
        } else {
            return new ResponseBean<String>(403, "error", "", "");
        }
    }

    private String getLoginUserId(String loginName, String password) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("loginName", loginName);
        paramMap.put("password", password);
        List<EntityRecord> userList = crudService.queryListRecord(SystemEntities.User, " [loginName]=:loginName and [loginPwd]=:password ",
                paramMap, null, null,"userId");
        if (ObjectUtils.isNotEmpty(userList)) {
            ID userID = (ID) userList.get(0).getFieldValue("userId");
            return userID.toString();
        }

        return null;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseBean<Boolean> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

    @RequestMapping("/saveUser")
    public ResponseBean<FormQueryResult> saveUser(@RequestParam("entity") String entityName,
                                                    @RequestParam("id") String recordId,
                                                    @RequestBody Map<String, Object> dataMap)
            throws IllegalAccessException, IOException, InstantiationException {
        FormQueryResult formQueryResult = userService.saveUser(entityName, recordId, dataMap);
        return ResponseHelper.ok(formQueryResult, "success");
    }

    @RequestMapping(value = "/deleteUser", method = RequestMethod.POST)
    public ResponseBean<Boolean> deleteUser(@RequestParam String userId) {
        userService.deleteUser( ID.valueOf(userId) );
        return ResponseHelper.ok(Boolean.TRUE, "success");
    }

}
