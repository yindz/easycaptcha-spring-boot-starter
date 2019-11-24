package com.apifan.framework.captcha.component;

import com.apifan.framework.captcha.config.CaptchaProperties;
import com.apifan.framework.captcha.util.MathEquationUtils;
import com.apifan.framework.captcha.vo.CaptchaInfoVO;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

/**
 * 图形验证码辅助工具
 *
 * @author yin
 */
public class CaptchaHelper {
    private static final Logger logger = LoggerFactory.getLogger(CaptchaHelper.class);

    private final CaptchaProperties captchaProperties;

    private Font font;

    private boolean initSuccess;

    private String chineseCommon;

    private List<String> chineseIdioms = new ArrayList<>();

    public CaptchaHelper(CaptchaProperties captchaProperties) {
        this.captchaProperties = captchaProperties;
    }

    /**
     * 初始化
     */
    public void init() {
        if (initSuccess) {
            return;
        }
        Preconditions.checkArgument(StringUtils.isNotEmpty(captchaProperties.getFontName()), "字体名不能为空");
        Preconditions.checkArgument(captchaProperties.getFontSize() > 10, "字体大小不正确");
        if ("Serif".equalsIgnoreCase(captchaProperties.getFontName())) {
            this.font = new Font(captchaProperties.getFontName(), Font.PLAIN, captchaProperties.getFontSize());
        } else {
            File fontFile = new File(captchaProperties.getFontName());
            if (!fontFile.exists()) {
                throw new RuntimeException("字体文件 " + captchaProperties.getFontName() + " 不存在");
            }
            try {
                this.font = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(Float.parseFloat(String.valueOf(captchaProperties.getFontSize())));
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(this.font);
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
        }
        ClassPathResource cpr;
        try {
            cpr = new ClassPathResource("chinese-common.txt");
            this.chineseCommon = new String(FileCopyUtils.copyToByteArray(cpr.getInputStream()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.warn("无法正常初始化常用汉字表");
        }
        try {
            cpr = new ClassPathResource("chinese-idioms.txt");
            String allIdioms = new String(FileCopyUtils.copyToByteArray(cpr.getInputStream()), StandardCharsets.UTF_8);
            String[] tmp = allIdioms.split("\\r\\n");
            this.chineseIdioms = Arrays.asList(tmp);
        } catch (IOException e) {
            logger.warn("无法正常初始化常用成语表");
        }
        logger.info("图形验证码辅助工具初始化成功");
        initSuccess = true;
    }

    /**
     * 绘制随机字符串
     *
     * @param isDigitsOnly 是否仅含数字
     * @param length       字符个数
     * @return 结果
     */
    public CaptchaInfoVO drawRandomAlphanumeric(boolean isDigitsOnly, int length) {
        String text = getRandomString(length, isDigitsOnly).toUpperCase();
        CaptchaInfoVO result = new CaptchaInfoVO();
        result.setText(text);
        result.setImageBase64(generateBase64Image(text, true, 0));
        return result;
    }

    /**
     * 绘制随机汉字
     *
     * @param length 字符个数
     * @return 结果
     */
    public CaptchaInfoVO drawRandomChinese(int length) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char ch = this.chineseCommon.charAt(RandomUtils.nextInt(0, this.chineseCommon.length()));
            text.append(ch);
        }
        CaptchaInfoVO result = new CaptchaInfoVO();
        result.setText(text.toString());
        result.setImageBase64(generateBase64Image(text.toString(), true, 6));
        return result;
    }

    /**
     * 绘制随机成语
     *
     * @return 结果
     */
    public CaptchaInfoVO drawRandomChineseIdiom() {
        //获取随机成语
        StringBuilder src = new StringBuilder(this.chineseIdioms.get(RandomUtils.nextInt(0, this.chineseIdioms.size())));
        int index = RandomUtils.nextInt(0, src.length());
        char missing = src.charAt(index);
        CaptchaInfoVO result = new CaptchaInfoVO();
        result.setText(new String(new char[]{missing}));
        src.setCharAt(index, '?');
        result.setImageBase64(generateBase64Image(src.toString(), false, 6));
        return result;
    }

    /**
     * 绘制随机数学算式
     *
     * @return 结果
     */
    public CaptchaInfoVO drawRandomMathEquation() {
        String exp = MathEquationUtils.generateRandomExpression();
        String value = MathEquationUtils.calculateResult(exp);
        CaptchaInfoVO result = new CaptchaInfoVO();
        result.setText(value);
        result.setImageBase64(generateBase64Image(exp.replaceAll("\\*", "×").replaceAll("-", "−"), false, 0));
        return result;
    }

    /**
     * 生成base64图像
     *
     * @param text           图像中包含的文本
     * @param useRandomAngle 是否使用随机角度
     * @param extraCharSpace 额外字间距
     * @return
     */
    private String generateBase64Image(String text, boolean useRandomAngle, int extraCharSpace) {
        String base64Str;
        int charCount = text.length();

        //验证码高度/宽度
        int captchaWidth = 24 * charCount + charCount * extraCharSpace + 16;
        int captchaHeight = 34;

        BufferedImage bufferedImage = new BufferedImage(captchaWidth, captchaHeight, BufferedImage.OPAQUE);
        Graphics2D g = bufferedImage.createGraphics();

        //生成绘制文本所用随机深色
        Color[] charColors = new Color[charCount];
        for (int i = 0; i < charCount; i++) {
            charColors[i] = getRandomColor(0, 200);
        }
        if (captchaProperties.isRandomBgColor()) {
            //分成几段，取随机浅色画背景
            int bgHeight = captchaHeight / charCount;
            for (int i = 0; i < charCount; i++) {
                g.setColor(getRandomColor(200, 255));
                g.fillRect(0, i * bgHeight, captchaWidth, captchaHeight);
            }
        } else {
            //画单色背景
            g.setColor(getRandomColor(200, 255));
            g.fillRect(0, 0, captchaWidth, captchaHeight);
        }
        g.setFont(font);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, (RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB));
        for (int i = 0; i < charCount; i++) {
            //逐个绘制字符
            char c = text.charAt(i);
            g.setColor(charColors[i]);

            //x坐标
            int x = 10 + 26 * i;
            if(extraCharSpace > 0){
                x += 5;
            }

            //y坐标
            int y = 28;

            //获取随机旋转角度
            double theta = useRandomAngle ? Math.toRadians(RandomUtils.nextInt(0, 45) - 30) : 0;

            //绘制字符前进行旋转
            g.rotate(theta, x, y);
            g.drawString(String.valueOf(c), x, y);
            //绘制字符后恢复角度
            g.rotate(-theta, x, y);
        }

        if (captchaProperties.isDrawNoise()) {
            int arcLinesCount = charCount * 80;
            for (int j = 0; j < arcLinesCount; j++) {
                int x1 = RandomUtils.nextInt(0, captchaWidth);
                int y1 = RandomUtils.nextInt(0, captchaHeight);
                g.setColor(charColors[RandomUtils.nextInt(0, charCount)]);
                int x2 = x1 + RandomUtils.nextInt(0, 2);
                int y2 = y1 + RandomUtils.nextInt(0, 2);
                g.drawLine(x1, y1, Math.max(x2, 1), Math.max(y2, 1));
            }
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", bos);
            base64Str = Base64.getEncoder().encodeToString(bos.toByteArray());
            return base64Str;
        } catch (IOException e) {
            logger.error("绘制图像出现异常", e);
        } finally {
            g.dispose();
            try {
                bos.close();
            } catch (IOException e) {
                logger.error("关闭输出流出现异常", e);
            }
        }
        return null;
    }

    /**
     * 获取随机颜色
     *
     * @param begin RGB范围起点
     * @param end   RGB范围终点
     * @return 随机颜色
     */
    private Color getRandomColor(int begin, int end) {
        return new Color(RandomUtils.nextInt(begin, end), RandomUtils.nextInt(begin, end), RandomUtils.nextInt(begin, end));
    }

    /**
     * 生成随机字符串
     *
     * @param length     长度
     * @param digitsOnly 是否仅含数字
     * @return 随机字符串
     */
    private String getRandomString(int length, boolean digitsOnly) {
        RandomStringGenerator.Builder builder = new RandomStringGenerator.Builder().withinRange('0', 'z');
        if (digitsOnly) {
            return builder.filteredBy(DIGITS).build().generate(length);
        }
        return builder.filteredBy(LETTERS, DIGITS).build().generate(length);
    }
}
