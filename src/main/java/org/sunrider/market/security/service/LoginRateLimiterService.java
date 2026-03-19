package org.sunrider.market.security.service;

import java.time.Instant;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import org.sunrider.market.exception.TooManyRequestsException;

@Service
public class LoginRateLimiterService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, AttemptInfo> attempts = new ConcurrentHashMap<>();

    /**
     * Проверяет, не заблокирован ли IP. Вызывать ДО попытки входа.
     */
    public void checkLimit(String ip) {
        AttemptInfo info = attempts.get(ip);
        if (info == null) {
            return;
        }
        if (info.blockedUntil != null && Instant.now().isBefore(info.blockedUntil)) {
            long secondsLeft = Duration.between(Instant.now(), info.blockedUntil).getSeconds();
            throw new TooManyRequestsException(
                "Слишком много попыток входа. Повторите через " + secondsLeft + " секунд."
            );
        }
    }

    /**
     * Фиксирует неудачную попытку входа. Вызывать ПОСЛЕ получения ошибки аутентификации.
     */
    public void recordFailure(String ip) {
        AttemptInfo info = attempts.compute(ip, (key, existing) -> {
            if (existing == null || (existing.blockedUntil != null && Instant.now().isAfter(existing.blockedUntil))) {
                return new AttemptInfo(new AtomicInteger(1), null);
            }
            existing.count.incrementAndGet();
            return existing;
        });

        if (info.count.get() >= MAX_ATTEMPTS) {
            info.blockedUntil = Instant.now().plus(BLOCK_DURATION);
        }
    }

    /**
     * Сбрасывает счётчик для IP после успешного входа.
     */
    public void recordSuccess(String ip) {
        attempts.remove(ip);
    }

    private static class AttemptInfo {
        final AtomicInteger count;
        volatile Instant blockedUntil;

        AttemptInfo(AtomicInteger count, Instant blockedUntil) {
            this.count = count;
            this.blockedUntil = blockedUntil;
        }
    }
}
