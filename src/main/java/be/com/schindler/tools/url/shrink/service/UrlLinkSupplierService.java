package be.com.schindler.tools.url.shrink.service;

import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.domain.UrlLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Service
public class UrlLinkSupplierService implements Function<Request, Flux<UrlLink>> {
  public Flux<UrlLink> get(Request request) {
    var hashValues =
        Stream.concat(Stream.of(request.getUrl()), Stream.generate(() -> UUID.randomUUID().toString()));
    return Flux.fromStream(hashValues)
        .map(String::hashCode)
        .map(String::valueOf)
        .map(hash -> hash.replaceFirst("-", "M"))
        .map(hash -> UrlLink.builder().hash(hash))
        .map(urlLinkBuilder -> urlLinkBuilder.expiration(OffsetDateTime.now().plusDays(1)))
        .map(urlLinkBuilder -> urlLinkBuilder.url(request.getUrl()))
        .map(UrlLink.UrlLinkBuilder::build);
  }

  @Override
  public Flux<UrlLink> apply(Request request) {
    return get(request);
  }
}
