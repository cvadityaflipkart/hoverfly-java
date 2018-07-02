package io.specto.hoverfly.junit.core;

import io.specto.hoverfly.junit.core.model.*;
import io.specto.hoverfly.junit.dsl.HoverflyDsl;
import io.specto.hoverfly.junit.dsl.StubServiceBuilder;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.specto.hoverfly.junit.core.HoverflyUtils.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


/**
 * Interface for reading simulation source as a String
 */
@FunctionalInterface
public interface SimulationSource {

    /**
     * Creates a simulation from a URL
     *
     * @param url the url of the simulation
     * @return the resource
     */
    static SimulationSource url(final URL url) {
        return () -> {
            try (InputStream is = url.openStream()) {
                return convertStreamToString(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot read simulation", e);
            }
        };
    }

    /**
     * Creates a simulation from a URL string
     *
     * @param url the url of the simulation
     * @return the resource
     */
    static SimulationSource url(final String url) {
        return () -> {
            try (InputStream is = new URL(url).openStream()) {
                return convertStreamToString(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot read simulation", e);
            }
        };
    }

    /**
     * Creates a simulation from the classpath
     *
     * @param classpath the classpath of the simulation
     * @return the resource
     */
    static SimulationSource classpath(final String classpath) {
        return () -> {
            try (InputStream is = getClasspathResourceAsStream(classpath)) {
                return convertStreamToString(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot load classpath resource: '" + classpath + "'", e);
            }
        };
    }

    /**
     * Creates a simulation from the file located in default hoverfly resource path which is relative to src/test/resources/hoverfly
     * @param pathString path string relative to the default hoverfly resource path
     * @return the resource
     */
    static SimulationSource defaultPath(String pathString) {
        return () -> {
            final String fullClasspath = HoverflyConstants.DEFAULT_HOVERFLY_RESOURCE_DIR + "/" + pathString;
            try (InputStream is = getClasspathResourceAsStream(fullClasspath)) {
                return convertStreamToString(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot load default path resource: '" + pathString + "'", e);
            }
        };
    }

    /**
     * Creates a simulation from a file
     *
     * @param path the file path of the simulation
     * @return the resource
     */
    static SimulationSource file(final Path path) {
        return () -> {
            try(InputStream is = Files.newInputStream(path)) {
                return convertStreamToString(is);
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot load file resource: '" + path.toString() + "'", e);
            }
        };
    }

    /**
     * Creates a simulation from the dsl
     * You can pass in multiple {@link StubServiceBuilder} to simulate services with different base urls
     *
     * @param stubServiceBuilder the fluent builder for {@link RequestResponsePair}
     * @return the resource
     * @see HoverflyDsl
     */
    static SimulationSource dsl(final StubServiceBuilder... stubServiceBuilder) {
        return () -> {
            final Set<RequestResponsePair> pairs = Arrays.stream(stubServiceBuilder)
                    .map(StubServiceBuilder::getRequestResponsePairs)
                    .flatMap(Set::stream)
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            final List<DelaySettings> delaySettings = Arrays.stream(stubServiceBuilder)
                    .map(StubServiceBuilder::getDelaySettings)
                    .flatMap(List::stream)
                    .collect(toList());

            return writeSimulationAsString(new Simulation(new HoverflyData(pairs, new GlobalActions(delaySettings)), new HoverflyMetaData()));
        };
    }

    /**
     * Creates a simulation from a {@link Simulation} object
     *
     * @param simulation the simulation
     * @return the simulation
     */
    static SimulationSource simulation(final Simulation simulation) {
        return () -> writeSimulationAsString(simulation);
    }

    /**
     * Creates no simulation
     *
     * @return an empty simulation
     */
    static SimulationSource empty() {
        return () -> writeSimulationAsString(Simulation.newEmptyInstance());
    }

    String getSimulation();

}
