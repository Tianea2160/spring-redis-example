package org.tianea.redisdistributedlock.aop

import org.slf4j.LoggerFactory
import org.springframework.context.expression.MethodBasedEvaluationContext
import org.springframework.core.DefaultParameterNameDiscoverer
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.lang.reflect.Method

@Component
class SpelParser {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val expressionParser: ExpressionParser = SpelExpressionParser()
    private val parameterNameDiscoverer = DefaultParameterNameDiscoverer()

    /**
     * SpEL 표현식을 파싱하여 실제 값으로 변환
     * 
     * @param spel SpEL 표현식
     * @param method 실행될 메서드
     * @param args 메서드 인자
     * @param target 대상 객체
     * @param prefix 키 접두사
     * @return 파싱된 키 값
     */
    fun parseKey(
        spel: String,
        method: Method,
        args: Array<Any>,
        target: Any,
        prefix: String
    ): String {
        try {
            if (!StringUtils.hasText(spel)) {
                return prefix
            }

            val context = MethodBasedEvaluationContext(target, method, args, parameterNameDiscoverer)
            val value = expressionParser.parseExpression(spel).getValue(context, String::class.java) ?: ""
            return prefix + value
        } catch (e: Exception) {
            logger.error("Failed to parse SpEL expression: {}", spel, e)
            return prefix + spel
        }
    }
}
