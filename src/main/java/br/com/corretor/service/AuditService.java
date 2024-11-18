package br.com.corretor.service;

import br.com.corretor.model.AuditEvent;
import br.com.corretor.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Async
    public void logEvent(String userId, String action, String resourceType, String resourceId, 
                        String status, String details) {
        logEventWithDuration(userId, action, resourceType, resourceId, status, details, null);
    }

    @Async
    public void logEventWithDuration(String userId, String action, String resourceType, String resourceId, 
                                   String status, String details, Long durationMs) {
        try {
            AuditEvent event = new AuditEvent();
            event.setUserId(userId);
            event.setAction(action);
            event.setResourceType(resourceType);
            event.setResourceId(resourceId);
            event.setStatus(status);
            event.setDetails(details);
            event.setTimestamp(LocalDateTime.now());
            event.setDurationMs(durationMs);

            // Adiciona informações do request se disponível
            Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    event.setIpAddress(getClientIp(request));
                    event.setUserAgent(request.getHeader("User-Agent"));
                    event.setRequestUri(request.getRequestURI());
                    event.setRequestMethod(request.getMethod());
                });

            auditEventRepository.save(event);
            
            // Log estruturado para fácil integração com ferramentas de análise
            log.info("Audit event: action={}, resourceType={}, resourceId={}, status={}, userId={}, duration={}ms", 
                    action, resourceType, resourceId, status, userId, durationMs);
                    
        } catch (Exception e) {
            log.error("Error logging audit event: {}", e.getMessage(), e);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
