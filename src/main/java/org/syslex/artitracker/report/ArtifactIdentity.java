package org.syslex.artitracker.report;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtifactIdentity {

    @JsonProperty
    public String group;

    @JsonProperty
    public String name;

    @JsonProperty
    public String version;

}
