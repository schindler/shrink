package be.com.schindler.tools.url.shrink.config;

import be.com.schindler.tools.url.shrink.domain.UrlLink;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisShrinkConfiguration {
  @Bean
  ReactiveRedisOperations<String, UrlLink> redisOperations(
      ObjectMapper mapper, ReactiveRedisConnectionFactory factory) {
    Jackson2JsonRedisSerializer<UrlLink> serializer =
        new Jackson2JsonRedisSerializer<>(UrlLink.class);
    serializer.setObjectMapper(mapper);
    RedisSerializationContext.RedisSerializationContextBuilder<String, UrlLink> builder =
        RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
    RedisSerializationContext<String, UrlLink> context = builder.value(serializer).build();
    return new ReactiveRedisTemplate<>(factory, context);
  }
}
