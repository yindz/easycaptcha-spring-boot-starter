package com.apifan.framework.captcha.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 验证码配置
 *
 * @author yin
 */
@Component
@ConfigurationProperties(prefix = "captcha")
public class CaptchaProperties {

    /**
     * 总开关
     */
    private boolean enabled = true;

    /**
     * 随机背景色
     */
    private boolean randomBgColor = true;

    /**
     * 是否画噪点
     */
    private boolean drawNoise = true;

    /**
     * 字体名
     */
    private String fontName = "Serif";

    /**
     * 字体大小
     */
    private int fontSize = 30;

    /**
     * 获取 随机背景色
     *
     * @return randomBgColor 随机背景色
     */
    public boolean isRandomBgColor() {
        return this.randomBgColor;
    }

    /**
     * 设置 随机背景色
     *
     * @param randomBgColor 随机背景色
     */
    public void setRandomBgColor(boolean randomBgColor) {
        this.randomBgColor = randomBgColor;
    }

    /**
     * 获取 字体名
     *
     * @return fontName 字体名
     */
    public String getFontName() {
        return this.fontName;
    }

    /**
     * 设置 字体名
     *
     * @param fontName 字体名
     */
    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    /**
     * 获取 字体大小
     *
     * @return fontSize 字体大小
     */
    public int getFontSize() {
        return this.fontSize;
    }

    /**
     * 设置 字体大小
     *
     * @param fontSize 字体大小
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * 获取 总开关
     *
     * @return enabled 总开关
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * 设置 总开关
     *
     * @param enabled 总开关
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取 是否画噪点
     *
     * @return drawNoise 是否画噪点
     */
    public boolean isDrawNoise() {
        return this.drawNoise;
    }

    /**
     * 设置 是否画噪点
     *
     * @param drawNoise 是否画噪点
     */
    public void setDrawNoise(boolean drawNoise) {
        this.drawNoise = drawNoise;
    }
}
