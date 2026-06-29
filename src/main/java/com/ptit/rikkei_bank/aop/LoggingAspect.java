package com.ptit.rikkei_bank.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.ptit.rikkei_bank.dto.request.TransferRequest;
import com.ptit.rikkei_bank.exception.BusinessException;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Pointcut that matches all repositories, services and Web REST endpoints.
     */
    @Around("within(com.ptit.rikkei_bank.controller..*) || " +
            "within(com.ptit.rikkei_bank.service.impl..*) || " +
            "within(com.ptit.rikkei_bank.repository..*)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        String shortenedClassName = shortenClassName(className);
        
        log.info("Enter - Class: {} - Method: {}() - Arguments: {}", shortenedClassName, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;
            
            String formattedResult = formatResult(result);
            log.info("Exit - Class: {} - Method: {}() - {} - Execution time: {} ms", 
                     shortenedClassName, methodName, formattedResult, elapsedTime);
            return result;
        } catch (BusinessException e) {
            log.warn("Business warning - Class: {} - Method: {}() - Message: {}", 
                     shortenedClassName, methodName, e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Illegal argument - Class: {} - Method: {}() - Arguments: {}", 
                      shortenedClassName, methodName, Arrays.toString(joinPoint.getArgs()));
            throw e;
        } catch (Exception e) {
            log.error("Exception - Class: {} - Method: {}() - Cause: {}", 
                      shortenedClassName, methodName, e.getCause() != null ? e.getCause() : e.getMessage());
            throw e;
        }
    }

    private String shortenClassName(String className) {
        if (className == null) return "";
        String[] parts = className.split("\\.");
        if (parts.length <= 1) return className;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(parts[i].charAt(0)).append(".");
            }
        }
        sb.append(parts[parts.length - 1]);
        return sb.toString();
    }

    private String formatResult(Object result) {
        if (result == null) {
            return "Result: null";
        }
        if (result instanceof ResponseEntity<?> responseEntity) {
            Object body = responseEntity.getBody();
            String bodyStr = body != null ? body.toString() : "null";
            
            String statusStr = "";
            try {
                int statusCode = responseEntity.getStatusCode().value();
                statusStr = String.valueOf(statusCode);
                if (responseEntity.getStatusCode() instanceof HttpStatus httpStatus) {
                    statusStr += " " + httpStatus.getReasonPhrase();
                } else {
                    statusStr += " " + responseEntity.getStatusCode().toString();
                }
            } catch (Exception e) {
                statusStr = responseEntity.getStatusCode().toString();
            }
            
            String headersStr = responseEntity.getHeaders().toString();
            return String.format("Result Body: %s - Status: %s - Headers: %s", bodyStr, statusStr, headersStr);
        }
        return "Result: " + result.toString();
    }

    @AfterReturning(
        pointcut = "execution(* com.ptit.rikkei_bank.service.TransactionService.transfer(..)) && args(userId, request)",
        returning = "result"
    )
    public void logAuditTransferSuccess(Object result, Long userId, TransferRequest request) {
        log.info("[AUDIT] Account {} transferred {} to Account {}", 
                 request.getFromAccountNumber(), request.getAmount(), request.getToAccountNumber());
    }

    @AfterThrowing(
        pointcut = "execution(* com.ptit.rikkei_bank.service.TransactionService.transfer(..)) && args(userId, request)",
        throwing = "ex"
    )
    public void logAuditTransferFailure(Exception ex, Long userId, TransferRequest request) {
        log.warn("[AUDIT] Transfer failed from Account {} to Account {}. Amount: {}. Reason: {}", 
                 request.getFromAccountNumber(), request.getToAccountNumber(), request.getAmount(), ex.getMessage());
    }
}
