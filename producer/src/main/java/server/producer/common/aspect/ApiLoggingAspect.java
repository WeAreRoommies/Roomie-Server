package server.producer.common.aspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiLoggingAspect {

	private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

	// 컨트롤러 메서드 실행 전 로깅
	@Before("execution(* server.producer.domain.controller.*.*(..))")
	public void logBefore(JoinPoint joinPoint) {
		logger.info("Request to: {}", joinPoint.getSignature());
		Object[] args = joinPoint.getArgs();
		if (args.length > 0) {
			logger.info("Arguments: {}", args);
		}
	}

	// 컨트롤러 메서드 실행 후 로깅
	@AfterReturning(pointcut = "execution(* server.producer.domain.controller.*.*(..))", returning = "result")
	public void logAfterReturning(JoinPoint joinPoint, Object result) {
		logger.info("Response from: {}", joinPoint.getSignature());
		logger.info("Response: {}", result);
	}

	// 예외 발생 시 로깅
	@AfterThrowing(pointcut = "execution(* server.producer.domain.controller.*.*(..))", throwing = "exception")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
		logger.error("Exception in: {}", joinPoint.getSignature());
		logger.error("Exception: {}", exception.getMessage(), exception);
	}
}
