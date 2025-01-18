package org.syslex.artitracker.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Generator {

    @JsonProperty
    public String name;

    @JsonProperty
    public String version;

    @JsonProperty
    public OffsetDateTime generatedAt;


}
