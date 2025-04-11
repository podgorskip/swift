package podgorskip.swift.logging;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricsAspect {
    private final MeterRegistry meterRegistry;

    @Around("execution(* podgorskip.swift..*Service.*(..))")
    public Object timeMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            return joinPoint.proceed();
        } finally {
            sample.stop(meterRegistry.timer("method.execution", "method", methodName));
        }
    }

    @Around("execution(* podgorskip.swift..*Controller.*(..))")
    public Object logHttpRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (org.aspectj.lang.reflect.MethodSignature) joinPoint.getSignature();

        String method = signature.getMethod().getName();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("[HTTP IN] {}.{} called with args: {}", className, method, args);

        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            Object result = joinPoint.proceed();
            log.info("[HTTP OUT] {}.{} completed successfully.", className, method);
            return result;
        } catch (Throwable ex) {
            log.error("[HTTP ERROR] {}.{} threw: ", className, ex.getMessage());
            throw ex;
        } finally {
            sample.stop(meterRegistry.timer("http.request.execution", "method", className + "." + method));
        }
    }
}

