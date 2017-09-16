package com.sunp.sso.utils;

import com.sunp.sso.model.UserInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: sunpeng
 * Date: 2017/9/16
 * Time: 18:59
 * Describe:
 */
public class UserAppManager {
    private static Map<String,Set<String>> USER_APP_MANAGER=new ConcurrentHashMap<>();

   public static void add(String username,String appUrl){
       Set<String> urls = USER_APP_MANAGER.get(username);
       if (urls==null){
           urls=new HashSet<>();
       }
       urls.add(appUrl);
       USER_APP_MANAGER.put(username,urls);
   }

   public static Set<String> getByName(String username){
       return USER_APP_MANAGER.get(username);
   }
   public static void deleteByName(String username){
       USER_APP_MANAGER.remove(username);
   }


}
