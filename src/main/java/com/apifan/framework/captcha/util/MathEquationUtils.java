package com.apifan.framework.captcha.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;
import java.util.Objects;

/**
 * 数学算式工具类
 *
 * @author yin
 */
public class MathEquationUtils {
    private static final Logger logger = LoggerFactory.getLogger(MathEquationUtils.class);

    private static final List<String> operators = Lists.newArrayList("+", "-", "*");
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");

    /**
     * 生成随机算术表达式
     *
     * @return 随机算术表达式
     */
    public static String generateRandomExpression() {
        int a = RandomUtils.nextInt(0, 101);
        int b = RandomUtils.nextInt(0, 101);
        return a + operators.get(RandomUtils.nextInt(0, 3)) + b;
    }

    /**
     * 计算结果
     *
     * @param exp 算术表达式
     * @return 结果
     */
    public static String calculateResult(String exp) {
        try {
            Object val = engine.eval(prepareExpression(exp));
            return Objects.toString(val);
        } catch (ScriptException e) {
            logger.error("解析表达式 {} 出错", exp, e);
        }
        return null;
    }

    /**
     * 处理算术表达式
     *
     * @param src
     * @return
     */
    private static String prepareExpression(String src) {
        return src.replace("=", "");
    }
}
