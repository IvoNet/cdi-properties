package nl.ivonet.cdi_properties;

import java.io.File;
import java.io.FileFilter;

/**
 * Finds files with file extension "properties" on the classpath.
 *
 * @author Ivo Woltring
 */
public class PropertyFileFilter implements FileFilter {

    @Override
    public boolean accept(final File pathname) {
        return (pathname != null) && !pathname.isDirectory() && pathname.getName()
                                                                        .endsWith(".properties");

    }

}
