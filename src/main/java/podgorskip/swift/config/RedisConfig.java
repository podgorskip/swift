package podgorskip.swift.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import podgorskip.swift.model.entities.SwiftCode;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, SwiftCode> swiftCodeCache(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, SwiftCode> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(SwiftCode.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, String> countryISO2Cache(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        return template;
    }
}

