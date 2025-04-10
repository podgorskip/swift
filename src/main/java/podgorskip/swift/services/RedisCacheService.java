package podgorskip.swift.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import podgorskip.swift.model.entities.SwiftCode;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, SwiftCode> redisTemplate;

    public void save(String key, SwiftCode value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void saveWithTTL(String key, SwiftCode value, long ttlInSeconds) {
        redisTemplate.opsForValue().set(key, value, ttlInSeconds, TimeUnit.SECONDS);
    }

    public Optional<SwiftCode> get(String key) {
        SwiftCode value = redisTemplate.opsForValue().get(key);
        return Objects.isNull(value) ? Optional.empty() : Optional.of(value);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
