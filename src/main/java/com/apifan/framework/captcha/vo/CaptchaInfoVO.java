package com.apifan.framework.captcha.vo;


import java.io.Serializable;

/**
 * 验证码信息
 *
 * @author yin
 */
public class CaptchaInfoVO implements Serializable {
    private static final long serialVersionUID = 2281703558629627606L;

    /**
     * 文本
     */
    private String text;

    /**
     * base64编码后的图像
     */
    private String imageBase64;

    /**
     * 获取 文本
     *
     * @return text 文本
     */
    public String getText() {
        return this.text;
    }

    /**
     * 设置 文本
     *
     * @param text 文本
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 获取 base64编码后的图像
     *
     * @return imageBase64 base64编码后的图像
     */
    public String getImageBase64() {
        return this.imageBase64;
    }

    /**
     * 设置 base64编码后的图像
     *
     * @param imageBase64 base64编码后的图像
     */
    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}
