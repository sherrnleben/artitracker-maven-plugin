package org.syslex.artitracker.reporting;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class ReportTest {

    @Test
    @DisplayName("JSON serialization: mandatory")
    public void jsonSerialization_mandatory() throws JsonProcessingException {
        final var report = new Report();

        report.artifact = new ArtifactIdentity();
        report.artifact.group = "com.example";
        report.artifact.name = "test-artifact";
        report.artifact.version = "1.0.0";

        report.generatedAt = OffsetDateTime.of(2024,12,22,22,37,14,0, ZoneOffset.UTC);

        final var result = report.toJsonString();
        Assertions.assertEquals(
                "{\"artifact\":{\"group\":\"com.example\",\"name\":\"test-artifact\",\"version\":\"1.0.0\"},\"generatedAt\":\"2024-12-22T22:37:14Z\"}",
                result
        );
    }

    @Test
    @DisplayName("JSON serialization: dependency")
    public void jsonSerialization_dependency() throws JsonProcessingException {
        final var report = new Report();

        final var dependency = new Dependency();
        dependency.group = "com.example";
        dependency.name = "test-plugin";
        dependency.version = "2024.5-SNAPSHOT";
        dependency.inclusion = DependencyInclusion.PLUGIN;

        report.dependencies = List.of(dependency);

        final var result = report.toJsonString();
        Assertions.assertEquals(
                "{\"dependencies\":[{\"group\":\"com.example\",\"name\":\"test-plugin\",\"version\":\"2024.5-SNAPSHOT\",\"inclusion\":\"PLUGIN\"}]}",
                result
        );
    }

}