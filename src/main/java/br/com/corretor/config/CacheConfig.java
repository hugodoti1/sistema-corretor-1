package br.com.corretor.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String TRANSACOES_CACHE = "transacoes";
    public static final String SALDOS_CACHE = "saldos";
    public static final String CONCILIACOES_CACHE = "conciliacoes";

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Configuração padrão com TTL de 1 hora
        RedisCacheConfiguration defaultConfig = createDefaultConfig()
            .entryTtl(Duration.ofHours(1));

        // Configurações específicas por cache
        Map<String, RedisCacheConfiguration> configs = new HashMap<>();
        
        // Cache de transações: 30 minutos
        configs.put(TRANSACOES_CACHE, createDefaultConfig()
            .entryTtl(Duration.ofMinutes(30)));
        
        // Cache de saldos: 5 minutos
        configs.put(SALDOS_CACHE, createDefaultConfig()
            .entryTtl(Duration.ofMinutes(5)));
        
        // Cache de conciliações: 1 hora
        configs.put(CONCILIACOES_CACHE, createDefaultConfig()
            .entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(defaultConfig)
            .withInitialCacheConfigurations(configs)
            .build();
    }

    private RedisCacheConfiguration createDefaultConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()
                )
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                )
            );
    }
}
