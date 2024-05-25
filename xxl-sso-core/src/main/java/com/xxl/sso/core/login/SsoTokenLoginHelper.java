package com.xxl.sso.core.login;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.store.SsoLoginStore;
import com.xxl.sso.core.store.SsoSessionIdHelper;
import com.xxl.sso.core.user.XxlSsoUser;
import com.xxl.sso.core.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xuxueli 2018-11-15 15:54:40
 */
public class SsoTokenLoginHelper {
    private static final Logger logger = LoggerFactory.getLogger(SsoTokenLoginHelper.class);

    /**
     * client login
     *
     * @param sessionId
     * @param xxlUser
     */
    public static void login(String sessionId, XxlSsoUser xxlUser) {

        String storeKey = SsoSessionIdHelper.parseStoreKey(sessionId);
        if (storeKey == null) {
            throw new RuntimeException("parseStoreKey Fail, sessionId:" + sessionId);
        }

        SsoLoginStore.put(storeKey, xxlUser);
    }

    /**
     * client logout
     *
     * @param sessionId
     */
    public static void logout(String sessionId) {

        String storeKey = SsoSessionIdHelper.parseStoreKey(sessionId);
        if (storeKey == null) {
            return;
        }

        SsoLoginStore.remove(storeKey);
    }

    /**
     * client logout
     *
     * @param request
     */
    public static void logout(HttpServletRequest request) {
        String headerSessionId = request.getHeader(Conf.SSO_SESSIONID);
        logout(headerSessionId);
    }


    /**
     * login check
     *
     * @param sessionId
     * @return
     */
    public static XxlSsoUser loginCheck(String sessionId) {

        String storeKey = SsoSessionIdHelper.parseStoreKey(sessionId);
        if (storeKey == null) {
            return null;
        }

        XxlSsoUser xxlUser = SsoLoginStore.get(storeKey);
        if (xxlUser != null) {
            String version = SsoSessionIdHelper.parseVersion(sessionId);
            if (xxlUser.getVersion().equals(version)) {

                // After the expiration time has passed half, Auto refresh
                if ((System.currentTimeMillis() - xxlUser.getExpireFreshTime()) > xxlUser.getExpireMinute() / 2) {
                    xxlUser.setExpireFreshTime(System.currentTimeMillis());
                    SsoLoginStore.put(storeKey, xxlUser);
                }

                return xxlUser;
            }
        }
        return null;
    }


    /**
     * login check
     *
     * @param request
     * @param ssoServer
     * @return
     */
    public static XxlSsoUser loginCheck(HttpServletRequest request, String ssoServer) {
        String headerSessionId = request.getHeader(Conf.SSO_SESSIONID);
        return loginCheck(headerSessionId, ssoServer);
    }


    /**
     * login check
     *
     * @param sessionId
     * @param ssoServer
     * @return
     */
    public static XxlSsoUser loginCheck(String sessionId, String ssoServer) {
        // return loginCheck(headerSessionId);
        // 20240511 修改为 远程调用 ssoserver 的 logincheck 接口
        // logincheck url
        String logincheckUrl = ssoServer + "/app/logincheck";

        // logincheck param
        Map<String, String> logincheckParam = new HashMap<>();
        logincheckParam.put("sessionId", sessionId);

        String logincheckResultJson = HttpClientUtil.post(logincheckUrl, logincheckParam, null);
        JSONObject logincheckResult = JSONUtil.parseObj(logincheckResultJson);

        int code = (int) logincheckResult.get("code");
        if (code == 200) {

            XxlSsoUser xxlSsoUser = logincheckResult.get("data", XxlSsoUser.class);

            logger.info("当前为登录状态，登陆用户 = " + xxlSsoUser.getUsername());

            return xxlSsoUser;
        } else {

            logger.info("当前为注销状态");
            return null;
        }

    }
}
