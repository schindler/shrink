package be.com.schindler.tools.url.shrink.controller;

import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.domain.UrlLink;
import be.com.schindler.tools.url.shrink.service.ShrinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ShrinkController {
  private final ShrinkService service;

  @GetMapping("/go/{id}")
  public Mono<Void> go(ServerHttpResponse response, @PathVariable("id") String id) {
    response.setStatusCode(HttpStatus.NOT_FOUND);
    return service
        .find(id)
        .flatMap(
            urlLink -> {
              response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
              response.getHeaders().setLocation(URI.create(urlLink.getUrl()));
              return response.setComplete();
            })
        .switchIfEmpty(response.setComplete());
  }

  @PostMapping(
      path = "/url",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<UrlLink> create(ServerHttpRequest client, @RequestBody Request request) {
    return service
        .create(request)
        .map(
            urlLink -> {
              urlLink.setHash(
                  UriComponentsBuilder.newInstance()
                      .port(client.getURI().getPort())
                      .scheme(client.getURI().getScheme())
                      .host(client.getURI().getHost())
                      .path("/go")
                      .pathSegment(urlLink.getHash())
                      .build()
                      .toUriString());
              return urlLink;
            });
  }
}
