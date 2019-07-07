package com.github.thierrysquirrel.core.utils;

import com.github.thierrysquirrel.annotation.RocketMessage;
import com.github.thierrysquirrel.autoconfigure.RocketProperties;
import com.github.thierrysquirrel.core.factory.execution.SendMessageFactoryExecution;
import com.github.thierrysquirrel.core.factory.execution.ThreadPoolExecutorExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ClassName: AspectUtils  
 * Description:  
 * date: 2019/4/29 22:03 
 *
 * @author ThierrySquirrel
 * @since JDK 1.8
 */
public class AspectUtils {
	public static <T extends Annotation> Object intercept(ProceedingJoinPoint proceedingJoinPoint, Map<String, Object> consumerContainer, RocketProperties rocketProperties, ThreadPoolExecutor threadPoolExecutor, Class<T> annotationClass) throws Throwable {
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();

		T annotation = method.getAnnotation(annotationClass);
		Object proceed = proceedingJoinPoint.proceed();

		byte[] body = ByteUtils.objectToByte(proceed);
//
		RocketMessage rocketMessage = method.getDeclaringClass().getAnnotation(RocketMessage.class);

		String shardingValue = "~";
		if (proceedingJoinPoint.getArgs().length > 0) {
			shardingValue = proceedingJoinPoint.getArgs()[0].toString();
		}
//
		ThreadPoolExecutorExecution.statsThread(threadPoolExecutor, new SendMessageFactoryExecution(consumerContainer,rocketMessage,annotation, rocketProperties, body, shardingValue));
		return proceed;
	}
}
