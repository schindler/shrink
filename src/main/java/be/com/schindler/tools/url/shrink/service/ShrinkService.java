package be.com.schindler.tools.url.shrink.service;

import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.domain.UrlLink;
import reactor.core.publisher.Mono;

public interface ShrinkService {
    Mono<UrlLink> create(Request request);
    Mono<UrlLink> find(String id);
}
