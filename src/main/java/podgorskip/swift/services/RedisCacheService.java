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

    public Optional<SwiftCode> get(String key) {
        SwiftCode value = swiftCodeCache.opsForValue().get(key);
        return Optional.ofNullable(value);
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

    public void deleteFromSet(String key, String value) {
        countryISO2Cache.opsForSet().remove(key, value);
    }

    public void clearCaches() {
        swiftCodeCache.getConnectionFactory().getConnection().serverCommands().flushAll();
        countryISO2Cache.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
}
