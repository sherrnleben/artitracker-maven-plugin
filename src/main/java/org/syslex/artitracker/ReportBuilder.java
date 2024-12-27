package org.syslex.artitracker;

import org.syslex.artitracker.reporting.ArtifactIdentity;
import org.syslex.artitracker.reporting.Dependency;
import org.syslex.artitracker.reporting.DependencyInclusion;
import org.syslex.artitracker.reporting.Report;
import org.apache.maven.model.Model;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReportBuilder {

    private ReportBuilder() {
    }

    public static Report buildReport(Model model) {
        final var report = new Report();
        report.generatedAt = OffsetDateTime.now();
        report.artifact = buildArtifact(model);
        report.dependencies = Stream.of(
                        getParent(model),
                        getDependencyList(model),
                        getPluginsList(model)
                ).flatMap(Collection::stream)
                .toList();
        return report;
    }

    public static ArtifactIdentity buildArtifact(Model model) {
        final var artifact = new ArtifactIdentity();
        artifact.group = model.getGroupId();
        artifact.name = model.getArtifactId();
        artifact.version = model.getVersion();
        return artifact;
    }


    public static Optional<String> getVersion(final Model model) {
        return Optional.ofNullable(model.getVersion());
    }

    public static Optional<String> getArtifactName(final Model model) {
        final var artifactName = Stream.of(model.getGroupId(), model.getArtifactId())
                .filter(Objects::nonNull)
                .collect(Collectors.joining("."));
        return artifactName.isBlank() ? Optional.empty() : Optional.of(artifactName);
    }

    public static List<Dependency> getDependencyList(final Model model) {
        if (Objects.isNull(model.getDependencies()))
            return List.of();
        return model.getDependencies().stream()
                .map(ReportBuilder::mapToDependency)
                .toList();
    }

    public static List<Dependency> getPluginsList(final Model model) {
        if (Objects.isNull(model.getBuild()) || Objects.isNull(model.getBuild().getPlugins()))
            return List.of();
        return model.getBuild().getPlugins().stream()
                .map(ReportBuilder::mapToDependency)
                .toList();
    }

    public static List<Dependency> getParent(final Model model) {
        if (Objects.isNull(model.getParent()))
            return List.of();
        return Stream.of(model.getParent())
                .map(ReportBuilder::mapToDependency)
                .toList();
    }

    public static Dependency mapToDependency(final org.apache.maven.model.Dependency modelDependency) {
        final var dependency = new Dependency();
        dependency.group = modelDependency.getGroupId();
        dependency.name = modelDependency.getArtifactId();
        dependency.version = modelDependency.getVersion();
        dependency.inclusion = DependencyInclusion.DEPENDENCY;
        return dependency;
    }

    public static Dependency mapToDependency(final org.apache.maven.model.Plugin plugin) {
        final var dependency = new Dependency();
        dependency.group = plugin.getGroupId();
        dependency.name = plugin.getArtifactId();
        dependency.version = plugin.getVersion();
        dependency.inclusion = DependencyInclusion.PLUGIN;
        return dependency;
    }

    public static Dependency mapToDependency(final org.apache.maven.model.Parent parent) {
        final var dependency = new Dependency();
        dependency.group = parent.getGroupId();
        dependency.name = parent.getArtifactId();
        dependency.version = parent.getVersion();
        dependency.inclusion = DependencyInclusion.PARENT;
        return dependency;
    }

}
