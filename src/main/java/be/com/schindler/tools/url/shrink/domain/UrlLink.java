package be.com.schindler.tools.url.shrink.domain;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UrlLink {
  private String url;
  @Setter private String hash;
  private OffsetDateTime expiration;
}
