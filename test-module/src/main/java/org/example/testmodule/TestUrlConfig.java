package org.example.testmodule;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "app")
public class TestUrlConfig {
    private String mvcUrl;
    private String webfluxUrl;
}
