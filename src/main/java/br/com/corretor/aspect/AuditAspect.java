package br.com.corretor.aspect;

import br.com.corretor.annotation.Audited;
import br.com.corretor.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(br.com.corretor.annotation.Audited)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String userId = getCurrentUserId();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Audited auditedAnnotation = method.getAnnotation(Audited.class);
        
        String action = auditedAnnotation.action();
        String resourceType = auditedAnnotation.resourceType();
        
        // Extrai o resourceId dos argumentos do método se especificado
        String resourceId = extractResourceId(joinPoint, auditedAnnotation.resourceIdParam());
        
        try {
            // Executa o método
            Object result = joinPoint.proceed();
            
            // Calcula a duração
            long duration = System.currentTimeMillis() - startTime;
            
            // Registra o evento de sucesso
            auditService.logEventWithDuration(
                userId,
                action,
                resourceType,
                resourceId,
                "SUCCESS",
                formatMethodCall(joinPoint),
                duration
            );
            
            return result;
            
        } catch (Exception e) {
            // Registra o evento de erro
            auditService.logEvent(
                userId,
                action,
                resourceType,
                resourceId,
                "ERROR",
                formatErrorDetails(joinPoint, e)
            );
            
            throw e;
        }
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SYSTEM";
    }

    private String extractResourceId(ProceedingJoinPoint joinPoint, String paramName) {
        if (paramName.isEmpty()) {
            return "N/A";
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(paramName)) {
                Object arg = joinPoint.getArgs()[i];
                return arg != null ? arg.toString() : "null";
            }
        }
        
        return "N/A";
    }

    private String formatMethodCall(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        StringBuilder details = new StringBuilder();
        details.append("Method: ").append(signature.getName()).append(", ");
        details.append("Parameters: {");
        
        for (int i = 0; i < paramNames.length; i++) {
            if (i > 0) details.append(", ");
            details.append(paramNames[i]).append(": ");
            
            // Trata valores sensíveis
            if (paramNames[i].toLowerCase().contains("password") || 
                paramNames[i].toLowerCase().contains("secret")) {
                details.append("*****");
            } else {
                details.append(args[i]);
            }
        }
        
        details.append("}");
        return details.toString();
    }

    private String formatErrorDetails(ProceedingJoinPoint joinPoint, Exception e) {
        return String.format(
            "Error in %s.%s: %s - %s",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            e.getClass().getSimpleName(),
            e.getMessage()
        );
    }
}
