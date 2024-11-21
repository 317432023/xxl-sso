package com.xxl.sso.core.store;

import com.xxl.sso.core.user.XxlSsoUser;

import java.util.Map;

/**
 * make client sessionId
 *
 *      client: cookie = [userid#version]
 *      server: redis
 *                  key = [userid]
 *                  value = user (user.version, valid this)
 *
 * //   group         The same group shares the login status, Different groups will not interact
 *
 * @author xuxueli 2018-11-15 15:45:08
 * @since 2024-09-12 modified
 * <p>
 *     多终端同时登录支持
 */

public class SsoSessionIdHelper {


    /**
     * make client sessionId
     *
     * @param xxlSsoUser
     * @return 返回格式：userid@terminal_version
     */
    public static String makeSessionId(XxlSsoUser xxlSsoUser){
        StringBuilder sessBuf = new StringBuilder();
        sessBuf.append(xxlSsoUser.getUserid());

        String terminal;
        Map<String, String> plugininfo = xxlSsoUser.getPlugininfo();
        if (plugininfo != null && plugininfo.containsKey("terminal") && "1".equals(plugininfo.get("terminal"))) {
            terminal = "1";
        } else {
            terminal = "0";
        }

        sessBuf.append(":").append(terminal);

        sessBuf.append("_");
        sessBuf.append(xxlSsoUser.getVersion());

        return sessBuf.toString();
    }

    /**
     * parse storeKey from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseStoreKey(String sessionId) {
        if (sessionId!=null && sessionId.contains("_")) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length==2
                    && sessionIdArr[0]!=null
                    && !sessionIdArr[0].trim().isEmpty()) {
                return sessionIdArr[0].trim();
            }
        }
        return null;
    }

    /**
     * parse version from sessionId
     *
     * @param sessionId
     * @return
     */
    public static String parseVersion(String sessionId) {
        if (sessionId!=null && sessionId.contains("_")) {
            String[] sessionIdArr = sessionId.split("_");
            if (sessionIdArr.length==2
                    && sessionIdArr[1]!=null
                    && !sessionIdArr[1].trim().isEmpty()) {
                return sessionIdArr[1].trim();
            }
        }
        return null;
    }

}
