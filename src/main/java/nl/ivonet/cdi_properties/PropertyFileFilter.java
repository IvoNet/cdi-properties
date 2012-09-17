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

        if (pathname == null) {
            //probably never happens but for robust programming purposes added...
            return false;
        }

        // We're not investigating sub-directories
        final boolean isDirectory = pathname.isDirectory();

        /*
         * FIXME: Change to something more appropriate - user can have a dir named "classes" on the regular
         * path - should it also be scanned for 'properties'?
         */
        //        final boolean isWebInfResource = pathname.getAbsolutePath().contains("classes/");

        if (isDirectory) { // || !isWebInfResource
            return false;
        }

        final String extension = getExtension(pathname.getName());

        return extension.equals("properties");
    }

    /**
     * <p>
     * Returns filename extension. Returns empty String if no extension is defined. E.g.:
     * <ul>
     * <li><code>myFile.dat</code>, returns <code>dat</code></li>
     * <li><code>myFile.with.dots.properties</code>, returns <code>properties</code></li>
     * </ul>
     * </p>
     * <p/>
     * <p>
     * This method never returns null and is null-argument safe.
     * </p>
     *
     * @param filename the file from which we extract the extension
     * @return extension of the <code>filename</code> without the trailing dot.
     */
    protected String getExtension(final String filename) {
        if (filename == null) {
            return "";
        }

        final int lastDotIdx = filename.lastIndexOf(".");

        if (lastDotIdx == -1) {
            return "";
        }

        return filename.substring(lastDotIdx + 1);
    }
}
