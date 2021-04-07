package be.com.schindler.tools.url.shrink.service;

import be.com.schindler.tools.url.shrink.config.TestRedisConfiguration;
import be.com.schindler.tools.url.shrink.domain.Request;
import be.com.schindler.tools.url.shrink.service.redis.RedisShrinkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
@Import(TestRedisConfiguration.class)
public class ShrinkServiceTest {
  @Autowired RedisShrinkService service;

  @Test
  void test() {
    var url1 = "AaAaAaAa";
    var url2 = "AaAaBBBB";
    var request = Request.builder().url(url1).build();
    var add = StepVerifier.create(service.create(request).log());
    add.expectNextMatches(urlLink -> urlLink.getUrl().equals(url1)).verifyComplete();
    var find = StepVerifier.create(service.find("M540425984").log());
    find.expectNextMatches(u -> u.getUrl().equals(url1)).verifyComplete();
    request = Request.builder().url(url2).build();
    add = StepVerifier.create(service.create(request).log());
    add.expectNextMatches(urlLink -> !urlLink.getHash().equals("M540425984")).verifyComplete();
  }
}
