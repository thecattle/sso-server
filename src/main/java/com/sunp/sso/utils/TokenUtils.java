package com.sunp.sso.utils;

import com.sunp.sso.model.UserInfo;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: sunpeng
 * Date: 2017/9/14
 * Time: 22:54
 * Describe:
 */
public class TokenUtils {
    private static Map<String,UserInfo> TOKEN_USER_MANAGER=new ConcurrentHashMap<>();

    /**
     * 获取随机 token
     * @return
     */
    public static String getToken(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 插入用户并且返回 token
     * @param info
     * @return
     */
    public static String takeTokenWithUserInfo(UserInfo info){
        String token=getToken();
        TOKEN_USER_MANAGER.put(token, info);
        return token;
    }

    /**
     * 根据 token 删除用户
     * @param username
     * @return
     */
    public static void deleteByName(String username){
        for (Map.Entry<String,UserInfo> user:TOKEN_USER_MANAGER.entrySet()) {
            if (user.getValue().getUserName().equals(username)){
                TOKEN_USER_MANAGER.remove(user.getKey());
            }
        }
    }


    /**
     * 根据 token 获取用户
     * @param token
     * @return
     */
    public static UserInfo getUserInfo(String token){
        for (Map.Entry<String,UserInfo> user:TOKEN_USER_MANAGER.entrySet()) {
            if (user.getKey().equals(token)){
                return user.getValue();
            }
        }
        return null;
    }


    /**
     * 根据 用户名 获取 用户
     * @param name
     * @return
     */
    public static UserInfo getUserInfoByUserName(String name){
        for (Map.Entry<String,UserInfo> user:TOKEN_USER_MANAGER.entrySet()) {
            if (user.getValue().getUserName().equals(name)){
                return user.getValue();
            }
        }
        return null;
    }

    /**
     * 根据 用户名和路径 获取 用户
     * @param name
     * @return
     */
    public static UserInfo getUserInfoByNameUrl(String name,String url){
        for (Map.Entry<String,UserInfo> user:TOKEN_USER_MANAGER.entrySet()) {
            if (name.equals(user.getValue().getUserName())&&url.equals(user.getValue().getAppUrl())){
                return user.getValue();
            }
        }
        return null;
    }

    public static void updataTokenWithUserInfo(String token,UserInfo user) {
        TOKEN_USER_MANAGER.remove(token);
        TOKEN_USER_MANAGER.put(getToken(),user);
    }
}
