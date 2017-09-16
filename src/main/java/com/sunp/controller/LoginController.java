package com.sunp.controller;

import com.sunp.model.User;
import com.sunp.service.UserService;
import com.sunp.sso.model.UserInfo;
import com.sunp.sso.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by IntelliJ IDEA.
 * User: sunpeng
 * Date: 2017/9/3
 * Time: 17:21
 * Describe: 用户数据 dao 接口实现
 */

@Controller
@RequestMapping(value = "/user")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getUserList() {
        logger.info("user/getUserList init");
        Map<String, Object> map = new HashMap<>();
        map.put("users", userService.getUserList());
        return map;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public void login(String username, String password, String target,
                      HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (username == null) {
            username = request.getParameter("name");
        }
        if (password == null) {
            password = request.getParameter("pwd");
        }
        if (target == null) {
            target = request.getParameter("target");
        }


        User user = userService.login(username, password);
        if (user != null) {
            String appSessionId = target.split("&")[1];
            UserInfo info = new UserInfo(request.getSession().getId(), appSessionId, username, null);
            String token = TokenUtils.takeTokenWithUserInfo(info);
            response.sendRedirect(URLUtils.decodeUrl(target.split("&")[0]) + "?token=" + token);
            return;
        }
        response.sendRedirect("/login.jsp?target=" + target);
    }

    @RequestMapping(value = "/checkToken", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkToken(String token, String appUrl) {
        Map<String, Object> map = new HashMap();
        map.put("success", false);
        UserInfo user = TokenUtils.getUserInfo(token);
        if (user != null) {
            user.setAppUrl(appUrl);
            //存入用户和应用的关联
            UserAppManager.add(user.getUserName(), appUrl);
            //重新初始化 token，是刚才的 token 失效，目的是 token 验证只能用一次
            TokenUtils.updataTokenWithUserInfo(token, user);
            map.put("success", true);
            map.put("data", user);
        }
        return map;
    }

    @RequestMapping(value = "/checkLogin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> checkLogin(String appUrl, String username,String appSessionId, HttpServletRequest request, HttpServletResponse response) {
        username = Base64Utils.decodeBase64(URLUtils.decodeUrl(username));
        Map<String, Object> map = new HashMap();
        map.put("success", false);
        UserInfo user = TokenUtils.getUserInfoByUserName(username);
        //当前账号已登录
        if (user != null) {
            //当前应用没登录
            if (TokenUtils.getUserInfoByNameUrl(username,appUrl)==null){
                UserInfo info=new UserInfo(user.getGloalSessionId(),appSessionId,username,appUrl);
                //存入用户和应用的关联
                UserAppManager.add(username, appUrl);
                TokenUtils.takeTokenWithUserInfo(info);
                map.put("data", info);
                map.put("success", true);
                return map;
            }else {
                map.put("data", TokenUtils.getUserInfoByNameUrl(username,appUrl));
                map.put("success", true);
                return map;
            }
        }
        return map;
    }

    @RequestMapping(value = "/loginOut", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> loginOut(String username) {
        username = Base64Utils.decodeBase64(URLUtils.decodeUrl(username));
        Map<String, Object> map = new HashMap();
        map.put("success", false);
        try {
            Set<String> urls = UserAppManager.getByName(username);
            if (urls != null) {
                for (String url : urls) {
                    //远程删除 session
                    UserInfo info = TokenUtils.getUserInfoByNameUrl(username, url);
                    Map<String, Object> params = new HashMap();
                    params.put("sessionId", info.getLocalSessionId());
                    String targetUrl = "http://" + url + "/user/toLoginOut";
                    HttpUtils.httpPostRequest(targetUrl, params);
                }
            }
            TokenUtils.deleteByName(username);
            UserAppManager.deleteByName(username);
            map.put("success", true);
            return map;
        } catch (Exception e) {
            logger.error("loginOut error",e);
        }
        return map;

    }

}
