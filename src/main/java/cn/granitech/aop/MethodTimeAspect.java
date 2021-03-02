package cn.granitech.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: VDP 思南
 * @Date: 2020/8/28 15:27
 * @Version: 1.0
 */

@Component
@Aspect
public class MethodTimeAspect {

    private String methodName;      // 方法名
    private long startTime;         // 开始时间

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 定义一个切入点.
     * 解释下：
     *
     * ~ 第一个 * 代表任意修饰符及任意返回值.
     * ~ 第二个 * 定义在web包或者子包
     * ~ 第三个 * 任意方法
     * ~ .. 匹配任意数量的参数.
     */
    @Pointcut("execution( public * cn.granitech.web.controller..*.*(..))")
    public void aopPointCut() {
    }

    @Before("aopPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        methodName = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        startTime = System.currentTimeMillis();
    }

    @After("aopPointCut()")
    public void doAfter() {
        long E_time = System.currentTimeMillis() - startTime;
        System.out.println("中文");
        System.out.println( System.getProperty("file.encoding") );
        log.info("执行 " + methodName + " 耗时为：" + E_time + "ms");  //计时效果跟doAround方法一样
    }

    @AfterReturning(returning = "object", pointcut = "aopPointCut()")
    public void doAfterReturning(Object object) {
        log.info("response={}", object.toString());
    }

    @Around("aopPointCut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("+++++around " + joinPoint + "\tUse time : " + (end - start) + " ms!");
            return result;

        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            log.error("+++++around " + joinPoint + "\tUse time : " + (end - start) + " ms with exception : " + e.getMessage());
            throw e;
        }
    }

}
