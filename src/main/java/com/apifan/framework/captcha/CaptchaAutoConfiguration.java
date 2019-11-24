package com.apifan.framework.captcha;

import com.apifan.framework.captcha.component.CaptchaHelper;
import com.apifan.framework.captcha.config.CaptchaProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 *
 * @author yin
 */
@Configuration
@ConditionalOnProperty(prefix = "captcha", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(CaptchaProperties.class)
@ComponentScan(basePackages = "com.apifan.framework.captcha")
public class CaptchaAutoConfiguration {

    private final CaptchaProperties captchaProperties;

    public CaptchaAutoConfiguration(final CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    /**
     * 图形验证码辅助工具
     *
     * @return
     */
    @Bean
    public CaptchaHelper captchaHelper() {
        final CaptchaHelper helper = new CaptchaHelper(captchaProperties);
        helper.init();
        return helper;
    }
}
