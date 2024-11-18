package br.com.corretor.aspect;

import br.com.corretor.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final AuditoriaService auditoriaService;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void endpointMethods() {}

    @AfterReturning(pointcut = "endpointMethods()", returning = "result")
    public void logApiChamada(JoinPoint joinPoint, Object result) {
        String metodo = joinPoint.getSignature().getName();
        String classe = joinPoint.getTarget().getClass().getSimpleName();
        String args = Arrays.toString(joinPoint.getArgs());
        
        String detalhes = String.format("Método: %s, Argumentos: %s, Retorno: %s",
                metodo, args, result != null ? result.toString() : "void");

        auditoriaService.registrarAcao(classe, "API_CHAMADA", detalhes);
    }

    @AfterThrowing(pointcut = "execution(* br.com.corretor..*.*(..))", throwing = "ex")
    public void logErro(JoinPoint joinPoint, Throwable ex) {
        String metodo = joinPoint.getSignature().getName();
        String classe = joinPoint.getTarget().getClass().getSimpleName();
        
        auditoriaService.registrarErro(
            classe + "." + metodo,
            ex.getMessage(),
            Arrays.toString(ex.getStackTrace())
        );
    }

    private Optional<String> getClientIp() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .map(this::extractIp);
    }

    private String extractIp(HttpServletRequest request) {
        String headerIp = request.getHeader("X-Forwarded-For");
        return headerIp != null ? headerIp : request.getRemoteAddr();
    }
}
