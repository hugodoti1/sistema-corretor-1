package br.com.corretor.util;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Supplier;

public class LogUtil {

    public static void logOperacao(Logger logger, String operacao, String recurso, String id, Runnable acao) {
        logOperacaoComRetorno(logger, operacao, recurso, id, () -> {
            acao.run();
            return null;
        });
    }

    public static <T> T logOperacaoComRetorno(Logger logger, String operacao, String recurso, String id, Supplier<T> acao) {
        long inicio = System.currentTimeMillis();
        String userId = getCurrentUserId();
        
        try (MDC.MDCCloseable ignored1 = MDC.putCloseable("operacao", operacao);
             MDC.MDCCloseable ignored2 = MDC.putCloseable("recurso", recurso);
             MDC.MDCCloseable ignored3 = MDC.putCloseable("id", id);
             MDC.MDCCloseable ignored4 = MDC.putCloseable("userId", userId)) {
            
            // Adiciona informações do request se disponível
            Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest)
                .ifPresent(request -> {
                    MDC.put("ipAddress", getClientIp(request));
                    MDC.put("userAgent", request.getHeader("User-Agent"));
                    MDC.put("requestUri", request.getRequestURI());
                    MDC.put("requestMethod", request.getMethod());
                });

            logger.info("Iniciando operação: {} em {}/{}", operacao, recurso, id);
            
            try {
                T resultado = acao.get();
                long duracao = System.currentTimeMillis() - inicio;
                
                MDC.put("duracao", String.valueOf(duracao));
                MDC.put("status", "SUCESSO");
                logger.info("Operação concluída com sucesso: {} em {}/{} ({}ms)", 
                    operacao, recurso, id, duracao);
                
                return resultado;
                
            } catch (Exception e) {
                long duracao = System.currentTimeMillis() - inicio;
                
                MDC.put("duracao", String.valueOf(duracao));
                MDC.put("status", "ERRO");
                MDC.put("erro", e.getMessage());
                MDC.put("stackTrace", getStackTraceAsString(e));
                
                logger.error("Erro na operação: {} em {}/{} ({}ms): {}", 
                    operacao, recurso, id, duracao, e.getMessage(), e);
                
                throw e;
            }
        }
    }

    private static String getCurrentUserId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .map(Authentication::getName)
            .orElse("SYSTEM");
    }

    private static String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
