package com.xxl.sso.sample.config;

import com.xxl.sso.core.conf.Conf;
import com.xxl.sso.core.filter.XxlSsoTokenFilter;
//import com.xxl.sso.core.util.JedisUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xuxueli 2018-11-15
 */
@Configuration
public class XxlSsoConfig implements DisposableBean {

    /**
     * 单点中心服务地址
     */
    @Value("${xxl.sso.server}")
    private String xxlSsoServer;

    /**
     * 单点中心退出API路径
     */
    @Value("${xxl.sso.logout.path}")
    private String xxlSsoLogoutPath;

    /**
     * 单点中心忽略拦截请求路径
     */
    @Value("${xxl-sso.excluded.paths}")
    private String xxlSsoExcludedPaths;

    /**
     * 单点中心会话高速缓存地址（redis地址）
     */
    //@Value("${xxl.sso.redis.address}")
    //private String xxlSsoRedisAddress;


    @Bean
    public FilterRegistrationBean xxlSsoFilterRegistration() {

        // xxl-sso, redis init
        //JedisUtil.init(xxlSsoRedisAddress);

        // xxl-sso, filter init
        FilterRegistrationBean registration = new FilterRegistrationBean();

        registration.setName("XxlSsoWebFilter");
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        registration.setFilter(new XxlSsoTokenFilter());
        registration.addInitParameter(Conf.SSO_SERVER, xxlSsoServer);
        registration.addInitParameter(Conf.SSO_LOGOUT_PATH, xxlSsoLogoutPath);
        registration.addInitParameter(Conf.SSO_EXCLUDED_PATHS, xxlSsoExcludedPaths);

        return registration;
    }

    @Override
    public void destroy() throws Exception {

        // xxl-sso, redis close
        //JedisUtil.close();
    }

}
