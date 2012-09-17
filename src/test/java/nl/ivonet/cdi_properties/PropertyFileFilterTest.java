package nl.ivonet.cdi_properties;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PropertyFileFilterTest {

    PropertyFileFilter cut;

    @BeforeMethod
    void initCut() {
        cut = new PropertyFileFilter();
    }

    @Test(dataProvider = "acceptDataProvider")
    public void accept(final File inputFile, final boolean expected) {
        final boolean actual = cut.accept(inputFile);

        assertThat(actual).isEqualTo(expected);
    }

    @DataProvider(name = "acceptDataProvider")
    Object[][] acceptDataProvider() {
        final List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[]{new File("/classes/test"), false});
        data.add(new Object[]{new File("/classes/test.properties"), true});
        data.add(new Object[]{new File("/myDirectory/classes/properties"), false});
        data.add(new Object[]{new File("/myDirectory/file.properties"), true});
        data.add(new Object[]{new File("/myDirectory/classes/weird.filenam.e.properties"), true});
        data.add(new Object[]{new File("test.properties"), true});
        data.add(new Object[]{new File(""), false});
        data.add(new Object[]{null, false});

        return data.toArray(new Object[0][0]);
    }

    @Test(dataProvider = "extensionDataProvider")
    public void getExtension(final String filename, final String expectedExtension) {
        final String actual = cut.getExtension(filename);

        assertThat(actual).isEqualTo(expectedExtension);
    }

    @DataProvider(name = "extensionDataProvider")
    Object[][] extensionDataProvider() {
        final List<Object[]> data = new ArrayList<Object[]>();

        data.add(new Object[]{"myFileproperties", ""});
        data.add(new Object[]{"myFile.properties", "properties"});
        data.add(new Object[]{"anotherFile.with.multiple.dots", "dots"});
        data.add(new Object[]{null, ""});
        data.add(new Object[]{"", ""});
        data.add(new Object[]{"...", ""});

        return data.toArray(new Object[0][0]);
    }
}
