package nl.ivonet.cdi_properties;

import java.io.IOException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * This test simulates client-usage of the {@link PropertyValueProducer}.
 *
 * @author Piotr Nowicki
 */
// TODO: Test cases where CDI should crash during deploy because of :
// 1. not met dependencies (required=true),
// 2. invalid conversion between String and Double/Integer.
public class PropertyValueProducerTest extends Arquillian {

    /*
     * A bunch of injected values which are valid.
     */
    @Inject @Property String myProp;

    @Inject @Property("myProp2") String myPropWithDifferentName;

    @Inject @Property String myProp3;

    @Inject @Property("myArbitraryKey") Double myDoubleProp;

    @Inject @Property("myArbitraryKeyInt") Integer myIntegerProp;

    /*
     * A bunch of values that cannot be injected (no key defined) but won't crash the CDI at the deploy time.
     */
    @Inject @Property(required = false) String myInvalidProp;

    @Inject @Property(value = "invalidPropKey", required = false) String myInvalidProp2;

    @Deployment
    public static Archive<?> createDeployment() throws IOException {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
                                        .addPackage(PropertyResolver.class.getPackage())
                                        .addAsResource("another.properties")
                                        .addAsResource("testProperties.properties");

        return archive;
    }

    @Test
    public void getDoubleConfigValue() {
        assertThat(myDoubleProp).isEqualTo(22.15);
    }

    @Test
    public void getIntegerConfigValue() {
        assertThat(myIntegerProp).isEqualTo(9);
    }

    @Test
    public void getStringConfigValue() {
        // Properly injected properties
        assertThat(myProp).isEqualTo("myVal");
        assertThat(myPropWithDifferentName).isEqualTo("myVal2");
        assertThat(myProp3).isEqualTo("myVal3");

        // Invalid properties, but no exception - they have 'required = false'
        assertThat(myInvalidProp).isNull();
        assertThat(myInvalidProp2).isNull();
    }
}
