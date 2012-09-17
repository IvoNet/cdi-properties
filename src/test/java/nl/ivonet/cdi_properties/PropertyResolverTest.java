package nl.ivonet.cdi_properties;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PropertyResolverTest extends Arquillian {

    @Inject PropertyResolver cut;

    @Deployment
    public static Archive<?> createDeployment() throws IOException {

        return ShrinkWrap.create(JavaArchive.class).addClass(PropertyResolver.class);
    }

    @Test(dataProvider = "getFileFromURLProvider")
    public void getFileFromURL(final URL url, final File expected) {
        final File actual = cut.getFileFromURL(url);

        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider(name = "getFileFromURLProvider")
    Object[][] getFileFromURLProvider() throws MalformedURLException {
        final List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[]{new URL("file:///testMyFile"), new File("/testMyFile")});
        data.add(new Object[]{new URL("file:///myDir/file with whitespaces"),
                              new File("/myDir/file with whitespaces")});
        data.add(new Object[]{new URL("file:///file%20with%20whitespaces"),
                              new File("/file with whitespaces")});

        return data.toArray(new Object[0][0]);
    }

    @Test(dataProvider = "keyProvider")
    public void getValue(final String key, final String expected) {
        final String actual = cut.getValue(key);

        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider(name = "keyProvider")
    Object[][] dataProvider() {
        final List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[]{"myProp", "myVal"});
        data.add(new Object[]{"myProp2", "myVal2"});
        data.add(new Object[]{"myProp3", null});

        return data.toArray(new Object[0][0]);
    }
}
