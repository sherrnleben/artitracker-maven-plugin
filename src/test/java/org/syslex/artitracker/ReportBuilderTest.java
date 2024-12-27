package org.syslex.artitracker;

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReportBuilderTest {

    @Test
    @DisplayName("get version: success")
    public void getVersion_success() {
        final var model = new Model();
        model.setVersion("1.0.0");
        final var result = ReportBuilder.getVersion(model);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("1.0.0", result.get());
    }

    @Test
    @DisplayName("get version: none found")
    public void getVersion_none() {
        final var model = new Model();
        final var result = ReportBuilder.getVersion(model);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("get artifact name: group ID + artifact ID")
    public void getArtifactName_groupAndArtifactID() {
        final var model = new Model();
        model.setGroupId("com.example");
        model.setArtifactId("artifact");
        final var result = ReportBuilder.getArtifactName(model);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.example.artifact", result.get());
    }

    @Test
    @DisplayName("get artifact name: group ID")
    public void getArtifactName_groupID() {
        final var model = new Model();
        model.setGroupId("com.example");
        final var result = ReportBuilder.getArtifactName(model);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("com.example", result.get());
    }

    @Test
    @DisplayName("get artifact name: artifact ID")
    public void getArtifactName_artifactID() {
        final var model = new Model();
        model.setArtifactId("artifact");
        final var result = ReportBuilder.getArtifactName(model);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("artifact", result.get());
    }

    @Test
    @DisplayName("get artifact name: none")
    public void getArtifactName_none() {
        final var model = new Model();
        final var result = ReportBuilder.getArtifactName(model);
        Assertions.assertFalse(result.isPresent());
    }

}