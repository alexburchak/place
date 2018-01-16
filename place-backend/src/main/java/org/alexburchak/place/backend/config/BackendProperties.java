package org.alexburchak.place.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * @author alexburchak
 */
@Component
@ConfigurationProperties("backend")
@Getter
@Setter
@Validated
public class BackendProperties {
    @Min(1)
    private int producerBufferSize;
    @Min(1)
    private int batchFlushPeriod;
}
