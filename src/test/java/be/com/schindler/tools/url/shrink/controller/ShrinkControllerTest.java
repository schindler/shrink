package be.com.schindler.tools.url.shrink.controller;

import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.domain.UrlLink;
import be.com.schindler.tools.url.shrink.service.ShrinkService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ShrinkController.class)
class ShrinkControllerTest {
  @Autowired private WebTestClient webClient;
  @MockBean ShrinkService service;

  @BeforeEach
  public void setup() {
    when(service.create(any()))
        .thenReturn(
            Mono.just(
                UrlLink.builder()
                    .expiration(OffsetDateTime.now().plusDays(1))
                    .url("test")
                    .hash("123")
                    .build()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"http://domain?param=1", "http://domain"})
  void createOkTest(String url) {
    var request = Request.builder().url(url).build();
    webClient
        .post()
        .uri("/")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus()
        .is2xxSuccessful()
        .expectBody()
        .jsonPath("$.url")
        .value(Matchers.is("test"))
        .jsonPath("$.link")
        .value(Matchers.is("/123"));
  }
}
