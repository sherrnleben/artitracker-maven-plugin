package org.syslex.artitracker;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.syslex.artitracker.report.Report;

import java.io.FileReader;
import java.io.IOException;

@Mojo(name = "sayhi")
public class ArtiTrackerReporter extends AbstractMojo {

    @Parameter(property = "artitracker.url")
    private String url;

    @Parameter(property = "artitracker.api-key")
    private String apiKey;

    public void execute() {
        getLog().info("Start collecting artifact information for ArtiTracker.");
        try {
            final var report = buildReport();
            getLog().info("Finished collecting artifact information for ArtiTracker.");
        } catch (Exception e) {
            getLog().error(e);
            e.printStackTrace();
        }
    }

    static Report buildReport() throws IOException, XmlPullParserException {
        // Initialize the Maven reader
        MavenXpp3Reader reader = new MavenXpp3Reader();

        // Read the pom.xml file
        Model model = reader.read(new FileReader("pom.xml"));

        // build report
        return new ReportBuilder(model).buildReport();
    }

}