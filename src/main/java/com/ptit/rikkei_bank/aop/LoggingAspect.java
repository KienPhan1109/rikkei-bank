package com.ptit.rikkei_bank.aop;


import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import com.ptit.rikkei_bank.dto.request.TransferRequest;
import com.ptit.rikkei_bank.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;

    public LoggingAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Pointcut cho Controllers (Web Endpoints)
     * Level INFO: Chỉ log ngắn gọn (Method, URI, Status, Time).
     * Level DEBUG: Log chi tiết tham số, kết quả JSON và PII Masking.
     */
    @Around("within(com.ptit.rikkei_bank.controller..*)")
    public Object logControllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortenedClassName = shortenClassName(className);

        HttpServletRequest request = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }
        } catch (Exception e) {
            // Ignore if outside web context
        }

        // DEBUG LEVEL: In chi tiết Arguments (đã mask) khi Controller bắt đầu
        if (log.isDebugEnabled()) {
            String maskedArgs = maskPII(serializeArgs(joinPoint.getArgs()));
            log.debug("ENTER - Web [{}] {}() - Args: {}", shortenedClassName, methodName, maskedArgs);
        }

        Object result;
        try {
            result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            // INFO LEVEL: Log tóm tắt rất ngắn gọn cho Môi trường Production
            if (request != null) {
                String method = request.getMethod();
                String uri = request.getRequestURI();
                int status = 200;
                if (result instanceof ResponseEntity<?> responseEntity) {
                    status = responseEntity.getStatusCode().value();
                }
                log.info("{} {} - Status: {} - Time: {}ms", method, uri, status, elapsedTime);
            }

            // DEBUG LEVEL: In chi tiết Result (đã mask) khi Controller kết thúc
            if (log.isDebugEnabled()) {
                String maskedResult = maskPII(serializeResult(result));
                log.debug("EXIT - Web [{}] {}() - Result: {} - Time: {}ms", shortenedClassName, methodName, maskedResult, elapsedTime);
            }
            
            return result;

        } catch (BusinessException e) {
            log.warn("BUSINESS WARNING - Web [{}] {}() - Message: {}", shortenedClassName, methodName, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("ILLEGAL ARG - Web [{}] {}() - Args: {}", shortenedClassName, methodName, maskPII(serializeArgs(joinPoint.getArgs())));
            throw e;
        } catch (Exception e) {
            log.error("EXCEPTION - Web [{}] {}() - Cause: {}", shortenedClassName, methodName, e.getCause() != null ? e.getCause() : e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Pointcut cho Service và Repository
     * Chỉ log khi bật level DEBUG. (Sạch sẽ hoàn toàn ở Production).
     */
    @Around("within(com.ptit.rikkei_bank.service.impl..*) || within(com.ptit.rikkei_bank.repository..*)")
    public Object logServiceAndRepoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!log.isDebugEnabled()) {
            // Nếu không ở chế độ DEBUG, bỏ qua log để tối ưu hiệu năng
            return joinPoint.proceed();
        }

        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortenedClassName = shortenClassName(className);

        String maskedArgs = maskPII(serializeArgs(joinPoint.getArgs()));
        log.debug("ENTER - Svc/Repo [{}] {}() - Args: {}", shortenedClassName, methodName, maskedArgs);

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            String maskedResult = maskPII(serializeResult(result));
            log.debug("EXIT - Svc/Repo [{}] {}() - Result: {} - Time: {}ms", shortenedClassName, methodName, maskedResult, elapsedTime);
            return result;
        } catch (Exception e) {
            log.debug("ERROR - Svc/Repo [{}] {}() - Exception: {}", shortenedClassName, methodName, e.getMessage());
            throw e;
        }
    }

    private String shortenClassName(String className) {
        if (className == null) return "";
        String[] parts = className.split("\\.");
        if (parts.length <= 1) return className;
        return parts[parts.length - 1]; // Lấy Class name ngắn nhất (Ví dụ: UserService)
    }

    private String serializeArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        List<String> argStrings = Arrays.stream(args).map(arg -> {
            if (arg == null) return "null";
            // Bỏ qua việc serialize một số object phức tạp của Spring
            if (arg instanceof org.springframework.validation.BindingResult) return "BindingResult";
            if (arg instanceof HttpServletRequest) return "HttpServletRequest";
            if (arg instanceof org.springframework.security.core.Authentication) return "Authentication";
            try {
                return objectMapper.writeValueAsString(arg);
            } catch (Exception e) {
                return arg.toString(); // Fallback nếu không parse được JSON
            }
        }).collect(Collectors.toList());
        return argStrings.toString();
    }

    private String serializeResult(Object result) {
        if (result == null) return "null";
        try {
            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                return body != null ? objectMapper.writeValueAsString(body) : "null";
            }
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return result.toString();
        }
    }

    /**
     * Dùng Regex quét chuỗi JSON để che giấu PII (Data Masking)
     */
    private String maskPII(String input) {
        if (input == null) return null;
        
        // 1. Mask Email (Ví dụ: "email":"test@gmail.com" -> "email":"***@gmail.com")
        input = input.replaceAll("(?i)(\"[^\"]*email[^\"]*\"\\s*:\\s*\")[^\"@]+@([^\"]+)(\")", "$1***@$2$3");
        
        // 2. Mask Số điện thoại (Ví dụ: "phoneNumber":"0987654321" -> "phoneNumber":"098****321")
        input = input.replaceAll("(?i)(\"[^\"]*phone[^\"]*\"\\s*:\\s*\")(\\d{3})\\d{4}(\\d{3})(\")", "$1$2****$3$4");
        
        // 3. Mask Mật khẩu, Mã PIN (Ví dụ: "password":"abc" -> "password":"***")
        input = input.replaceAll("(?i)(\"[^\"]*(?:password|pin)[^\"]*\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");

        // 4. Mask Token (Ví dụ: "accessToken":"eyJ..." -> "accessToken":"***")
        input = input.replaceAll("(?i)(\"[^\"]*(?:token)[^\"]*\"\\s*:\\s*\")([^\"]+)(\")", "$1***$3");

        return input;
    }

    @AfterReturning(
        pointcut = "execution(* com.ptit.rikkei_bank.service.TransactionService.transfer(..)) && args(userId, request)",
        returning = "result"
    )
    public void logAuditTransferSuccess(Object result, Long userId, TransferRequest request) {
        log.info("[AUDIT] User {} transferred {} to Account {}", 
                 userId, request.getAmount(), request.getToAccountNumber());
    }

    @AfterThrowing(
        pointcut = "execution(* com.ptit.rikkei_bank.service.TransactionService.transfer(..)) && args(userId, request)",
        throwing = "ex"
    )
    public void logAuditTransferFailure(Exception ex, Long userId, TransferRequest request) {
        log.warn("[AUDIT] Transfer failed from User {} to Account {}. Amount: {}. Reason: {}", 
                 userId, request.getToAccountNumber(), request.getAmount(), ex.getMessage());
    }
}
