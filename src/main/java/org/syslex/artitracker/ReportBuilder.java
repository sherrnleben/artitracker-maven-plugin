package org.syslex.artitracker;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.syslex.artitracker.report.*;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

public class ReportBuilder {

    private final static Logger logger = LoggerFactory.getLogger(ReportBuilder.class);

    private final Model model;
    Report report;

    public ReportBuilder(final Model model) {
        this.model = model;
    }

    /**
     * Map dependency model from POM to generic dependency model of report
     *
     * @param modelDependency Dependency model from POM
     * @return Generic dependency report mode
     */
    public static Dependency mapToDependency(final org.apache.maven.model.Dependency modelDependency) {
        final var dependency = new Dependency();
        dependency.group = modelDependency.getGroupId();
        dependency.name = modelDependency.getArtifactId();
        dependency.version = modelDependency.getVersion();
        dependency.inclusion = DependencyInclusion.DEPENDENCY;
        return dependency;
    }

    /**
     * Map plugin model from POM to generic dependency model of report
     *
     * @param plugin Plugin model from POM
     * @return Generic dependency report mode
     */
    public static Dependency mapToDependency(final org.apache.maven.model.Plugin plugin) {
        final var dependency = new Dependency();
        dependency.group = plugin.getGroupId();
        dependency.name = plugin.getArtifactId();
        dependency.version = plugin.getVersion();
        dependency.inclusion = DependencyInclusion.PLUGIN;
        return dependency;
    }

    /**
     * Map parent model from POM to generic dependency model of report
     *
     * @param parent Parent model from POM
     * @return Generic dependency report mode
     */
    public static Dependency mapToDependency(final org.apache.maven.model.Parent parent) {
        final var dependency = new Dependency();
        dependency.group = parent.getGroupId();
        dependency.name = parent.getArtifactId();
        dependency.version = parent.getVersion();
        dependency.inclusion = DependencyInclusion.PARENT;
        return dependency;
    }

    public Report buildReport() {
        report = new Report();
        collectGenerator();
        collectProgramming();
        collectArtifact();
        collectParent();
        collectDependencies();
        collectPlugins();
        return report;
    }

    /**
     * Collect information about report generation and add it to report
     */
    void collectGenerator() {
        // ensure that report contains generator object
        if (Objects.isNull(report.generator))
            report.generator = new Generator();

        final Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (final IOException e) {
            logger.warn("Error loading project properties file to extract generator name and version", e);
        }

        // set values for generator
        report.generator.name = properties.getProperty("artifactId");
        report.generator.version = properties.getProperty("version");
        report.generator.generatedAt = OffsetDateTime.now();
    }

    /**
     * Collect information about programming language
     */
    void collectProgramming() {
        // ensure that report contains programming object
        if (Objects.isNull(report.programming))
            report.programming = new Programming();

        // set values for programming
        report.programming.language = Language.Java;
        report.programming.version = Stream.of(
                        readJavaVersionFromReleaseProperties(model, "maven.compiler.release"),
                        readJavaVersionFromReleaseProperties(model, "maven.compiler.target"),
                        readJavaVersionFromReleaseProperties(model, "maven.compiler.source"),
                        readJavaVersionFromMavenCompilerConfig(model, "release"),
                        readJavaVersionFromMavenCompilerConfig(model, "target"),
                        readJavaVersionFromMavenCompilerConfig(model, "source")
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .orElse(null);
    }

    /**
     * Read Java version from POM properties (release)
     *
     * @param model       Maven POM model
     * @param propertyKey Key of property
     * @return Optional of Java version
     */
    Optional<String> readJavaVersionFromReleaseProperties(final Model model, final String propertyKey) {
        return Objects.nonNull(model.getProperties()) && model.getProperties().containsKey(propertyKey)
                ? Optional.of(model.getProperties().getProperty(propertyKey))
                : Optional.empty();
    }

    /**
     * Read Java version from Maven  properties (release)
     *
     * @param model     Maven POM model
     * @param configKey Key of configuration
     * @return Optional of Java version
     */
    Optional<String> readJavaVersionFromMavenCompilerConfig(final Model model, final String configKey) {
        if (Objects.isNull(model.getBuild()) || Objects.isNull(model.getBuild().getPlugins()))
            return Optional.empty();

        // find maven compiler plugin
        final var compilerPlugin = model.getBuild().getPlugins().stream()
                .filter(plugin -> Objects.nonNull(plugin.getArtifactId()))
                .filter(plugin -> plugin.getArtifactId().equals("maven-compiler-plugin"))
                .findFirst();
        if (compilerPlugin.isEmpty()
                || Objects.isNull(compilerPlugin.get().getConfiguration())
                || !(compilerPlugin.get().getConfiguration() instanceof Xpp3Dom config))
            return Optional.empty();

        final var param = config.getChild(configKey);
        if (Objects.isNull(param) || Objects.isNull(param.getValue()))
            return Optional.empty();

        return Optional.of(param.getValue());
    }

    /**
     * Collect information about artifact and add it to report
     */
    void collectArtifact() {
        // ensure that report contains artifact object
        if (Objects.isNull(report.artifact))
            report.artifact = new ArtifactIdentity();

        // set values for artifact
        report.artifact.group = model.getGroupId();
        report.artifact.name = model.getArtifactId();
        report.artifact.version = model.getVersion();
    }

    /**
     * Collect information about parent and add it to report
     */
    void collectParent() {
        // ensure that report contains a list for collected dependencies
        if (Objects.isNull(report.dependencies))
            report.dependencies = new ArrayList<>();

        // abort if no parent is found
        if (Objects.isNull(model.getParent()))
            return;

        // map parent to generic dependency and add it to report
        report.dependencies.add(ReportBuilder.mapToDependency(model.getParent()));
    }

    /**
     * Collect information about plugins and add it to report
     */
    void collectPlugins() {
        // ensure that report contains a list for collected dependencies
        if (Objects.isNull(report.dependencies))
            report.dependencies = new ArrayList<>();

        // abort if no plugins are found
        if (Objects.isNull(model.getBuild()) || Objects.isNull(model.getBuild().getPlugins()))
            return;

        // stream over all plugins, map them to generic dependency, and add them to report
        model.getBuild().getPlugins().stream()
                .map(ReportBuilder::mapToDependency)
                .forEach(report.dependencies::add);
    }

    /**
     * Collect information about dependencies and add it to report
     */
    void collectDependencies() {
        // ensure that report contains a list for collected dependencies
        if (Objects.isNull(report.dependencies))
            report.dependencies = new ArrayList<>();

        // abort if no dependencies are found
        if (Objects.isNull(model.getDependencies()))
            return;

        // stream over all dependencies, map them to generic dependency, and add them to report
        model.getDependencies().stream()
                .map(ReportBuilder::mapToDependency)
                .forEach(report.dependencies::add);
    }

}
