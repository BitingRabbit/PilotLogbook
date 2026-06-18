package de.dhbwravensburg.webeng.pilotlogbook.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configures caches for upstream API responses.
 *
 * Two separate caches with different lifecycles:
 * <ul>
 *   <li>{@link #LIVE_METAR}: 10min TTL, weather observations change frequently</li>
 *   <li>{@link #AIRPORTS}: 24h TTL, airport data is basically static</li>
 * </ul>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String LIVE_METAR = "liveMetar";
    public static final String AIRPORTS   = "airports";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(LIVE_METAR, AIRPORTS);
        manager.registerCustomCache(LIVE_METAR, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(500)
                .build());
        manager.registerCustomCache(AIRPORTS, Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofHours(24))
                .maximumSize(2_000)
                .build()
        );
        return manager;
    }
}
