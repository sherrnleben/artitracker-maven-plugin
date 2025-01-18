package org.syslex.artitracker.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {

    @JsonProperty
    public ArtifactIdentity artifact;

    @JsonProperty
    public Programming programming;

    @JsonProperty
    public List<Dependency> dependencies;

    @JsonProperty
    public Generator generator;

    @JsonIgnore
    public String toJsonString() throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(this);
    }

}
