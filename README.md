# 图形验证码辅助工具包
## 概述
简单易用的图形验证码辅助工具包。可直接快速集成到您的 SpringBoot 项目中。支持前后端分离以及前后端不分离的web项目。

目前支持以下4种形式的图形验证码：
- 随机的英文字母或数字
- 随机的汉字（约2000个常用汉字）
- 随机的汉语成语（约30000个常用成语）
- 随机的整数数学算式（0~100之内的加法、减法、乘法）

## 优点
- 容易集成，无需过多第三方依赖
- 支持使用自定义的TTF字体进行渲染
- 支持随机角度旋转和噪点，提升防OCR破解能力

## 效果
![效果](https://i.loli.net/2019/11/24/aOK1xPkc9VhL4lX.png)

## 如何使用
### 配置仓库
暂时尚未进入maven中央仓库，因此请在 pom.xml 中配置一个仓库地址：
```xml
<repositories>
    <repository>
        <id>apifan-repo</id>
        <name>apifan-repo</name>
        <url>http://118.31.70.236:8004/nexus/content/repositories/biz-repo/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```
### 引入依赖
```xml
<dependency>
    <groupId>com.apifan.framework</groupId>
    <artifactId>easycaptcha-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### 配置
请在您的应用程序对应的配置文件（application.yml或application.properties）中配置参数。
#### 参数说明
| 参数名 | 参数用途 | 默认值 |
| ------ | --------- | -------- |
| captcha.enabled | 总开关 | true |
| captcha.random-bg-color | 是否使用随机的背景色 | true |
| captcha.draw-noise | 是否绘制随机的噪点 | true |
| captcha.font-name | 字体名称或路径 | Serif |
| captcha.font-size | 字体大小 | 30 |

#### 参数范例
```yaml
captcha:  
  enabled: true
  random-bg-color: true
  draw-noise: true
  font-name: /home/me/SourceHanSerifCN-Regular.ttf
  font-size: 30
```
#### 汉字显示注意事项
- 需要输出汉字验证码时，请使用包含汉字的TTF字体文件以避免不同服务器环境下显示异常（在 font-name 参数中指定该字体文件的完整路径）
- 为避免出现侵权纠纷，在未经授权的情况下，不要使用商业字体（如操作系统自带的苹方、微软雅黑等），推荐使用开源的[思源字体](https://github.com/adobe-fonts/source-han-serif)

#### 代码范例
```java
@RequestMapping("/demo")
@Controller
public class DemoController {
    @Autowired
    private CaptchaHelper captchaHelper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 获取验证码
     *
     * @return base64编码后的验证码图片
    */
    @RequestMapping(value = "/image")
    @ResponseBody
    public String image(){
        //TODO 根据业务情况，获取cacheKey
        String cacheKey = "123456789";

        //采用随机字符（英文字母或数字）,长度4
        CaptchaInfoVO vo = captchaHelper.drawRandomAlphanumeric(false, 4);

        //采用随机数学算式
        CaptchaInfoVO vo = captchaHelper.drawRandomMathEquation();

        //采用随机汉字,长度4
        CaptchaInfoVO vo = captchaHelper.drawRandomChinese(4);

        //采用随机汉语成语
        CaptchaInfoVO vo = captchaHelper.drawRandomChineseIdiom();
        if(vo != null){
            //放入缓存
            redisTemplate.opsForValue().set(cacheKey, vo.getText(), 180L, TimeUnit.SECONDS);
            
            //返回
            return vo.getImageBase64();
        }
        return null;    
    }

    /**
     * 校验用户输入的验证码
     *
     * @param code 用户输入的验证码
     * @return 是否正确
     */
    @RequestMapping(value = "/check")
    @ResponseBody
    public boolean check(@RequestParam("code") String code){
        if(StringUtils.isEmpty(code)){
            return false;
        }
        //TODO 根据业务情况，获取cacheKey
        String cacheKey = "123456789";
        String cachedCode = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isEmpty(cachedCode)){
            return false;
        }
        if(code.equalsIgnoreCase(cachedCode)){
            redisTemplate.delete(cacheKey);
            return true;
        }
        return false;
    }
}
```
### 更多信息
- 可参阅 [easycaptcha-demo](https://github.com/yindz/easycaptcha-example) 工程