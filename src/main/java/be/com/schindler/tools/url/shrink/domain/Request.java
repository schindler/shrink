package be.com.schindler.tools.url.shrink.domain;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {
  @NonNull private String url;
}
