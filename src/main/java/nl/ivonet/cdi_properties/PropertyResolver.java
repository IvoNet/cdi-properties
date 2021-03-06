package nl.ivonet.cdi_properties;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Reads all valid property files within the classpath and prepare them to be fetched.
 *
 * This class can be accessed concurrently by multiple clients. The inner representation of properties should not be
 * leaked out; if this is absolutely required, use unmodifiable collection.
 *
 * This resolver doesn't pay attention to multiple properties defined with the same name in different files. It's
 * impossible to determine which one will take precedence, so the responsibility for name-clash is a deployer concern.
 *
 * @author Ivo Woltring
 */
@Singleton
public class PropertyResolver {

    Map<String, Object> properties = new HashMap<>();

    /**
     * Initializes the properties by reading and uniforming them. This method is called by the container only. It's not
     * supposed to be invoked by the client directly.
     */
    @SuppressWarnings({"rawtypes", "unchecked", "unused"})
    @PostConstruct
    private void init() {
        final ClassLoader classLoader = Thread.currentThread()
                                              .getContextClassLoader();

        final List<File> propertyFiles = getPropertyFiles(classLoader);

        for (final File file : propertyFiles) {
            final Properties p = new Properties();
            try {
                p.load(new FileInputStream(file));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            this.properties.putAll(new HashMap<>((Map) p));
        }
    }

    /**
     * Gets flat-file properties files accessible from the root of the given classloader.
     *
     * @param classLoader classpath to be used when scanning for files.
     * @return found property files.
     */
    List<File> getPropertyFiles(final ClassLoader classLoader) {
        final List<File> result = new ArrayList<>();

        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources("");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        while (resources.hasMoreElements()) {
            final File resource = getFileFromURL(resources.nextElement());

            final File[] files = resource.listFiles(new PropertyFileFilter());

            result.addAll(Arrays.asList(files));
        }

        return result;
    }

    /**
     * Converts URL resource to a File. Makes sure that invalid URL characters (e.g. whitespaces) won't prevent us from
     * accessing the valid file location.
     *
     * @param url URL to be transformed
     * @return File pointing to the given <code>url</code>.
     */
    File getFileFromURL(final URL url) {
        File result;

        try {
            result = new File(url.toURI());
        } catch (final URISyntaxException e) {
            result = new File(url.getPath());
        }

        return result;
    }

    /**
     * Returns property held under specified <code>key</code>. If the value is supposed to be of any other type than
     * {@link String}, it's up to the client to do appropriate casting.
     *
     * @param key the key to find
     * @return value for specified <code>key</code> or null if not defined.
     */
    public String getValue(final String key) {
        final Object value = this.properties.get(key);

        return (value != null) ? String.valueOf(value) : null;
    }
}
