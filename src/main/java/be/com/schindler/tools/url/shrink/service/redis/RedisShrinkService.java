package be.com.schindler.tools.url.shrink.service.redis;

import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.domain.UrlLink;
import be.com.schindler.tools.url.shrink.service.ShrinkService;
import be.com.schindler.tools.url.shrink.service.UrlLinkSupplierService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisShrinkService implements ShrinkService {
  @NonNull private final ReactiveRedisOperations<String, UrlLink> service;
  @NonNull private final UrlLinkSupplierService supplier;

  @Override
  public Mono<UrlLink> create(Request request) {
    return supplier
        .get(request)
        .limitRequest(10)
        .concatMap(this::addNew)
        .next()
        .flatMap(this::setTTL);
  }

  @Override
  public Mono<UrlLink> find(String hash) {
    log.info("find {}", hash);
    return service.opsForValue().get(hash);
  }

  Mono<UrlLink> addNew(UrlLink urlLink) {
    log.info("addNew {}", urlLink);
    return service
        .opsForValue()
        .setIfAbsent(urlLink.getHash(), urlLink)
        .filter(Boolean::booleanValue)
        .map(aBoolean -> urlLink);
  }

  Mono<UrlLink> setTTL(UrlLink urlLink) {
    log.info("setTTL {}", urlLink);
    return service
        .expireAt(urlLink.getHash(), urlLink.getExpiration().toInstant())
        .filter(Boolean::booleanValue)
        .thenReturn(urlLink);
  }
}
