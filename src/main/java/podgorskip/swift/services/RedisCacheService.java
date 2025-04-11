package podgorskip.swift.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import podgorskip.swift.model.entities.SwiftCode;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, SwiftCode> swiftCodeCache;
    private final RedisTemplate<String, String> countryISO2Cache;

    public void save(String key, SwiftCode value) {
        swiftCodeCache.opsForValue().set(key, value);
    }

    public void saveWithTTL(String key, SwiftCode value, long ttlInSeconds) {
        swiftCodeCache.opsForValue().set(key, value, ttlInSeconds, TimeUnit.SECONDS);
    }

    public Optional<SwiftCode> get(String key) {
        SwiftCode value = swiftCodeCache.opsForValue().get(key);
        return Optional.ofNullable(value);
    }

    public boolean exists(String key) {
        return swiftCodeCache.hasKey(key);
    }

    public void delete(String key) {
        swiftCodeCache.delete(key);
    }

    public Set<String> getSet(String key) {
        return countryISO2Cache.opsForSet().members(key);
    }

    public void addToSet(String key, String value) {
        countryISO2Cache.opsForSet().add(key, value);
    }
}
