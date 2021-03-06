package nl.ivonet.cdi_properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Produces {@link Property} annotated fields. It's responsible for supporting conversion between types (mainly from
 * String to any other type required by the user.)
 *
 * @author Ivo Woltring
 * @see PropertyResolver
 */
public class PropertyValueProducer {

    @Inject PropertyResolver resolver;

    /**
     * Main producer method - tries to find a property value using following keys:
     *
     * <ol><li><code>key</code> property of the {@link Property} annotation (if defined but no key is found - returns
     * null),</li>
     *
     * <li>fully qualified field class name, e.g. <code>nl.ivonet.MyBean.myField</code> (if value is null, go along with
     * the last resort),</li>
     *
     * <li>field name, e.g. <code>myField</code> for the example above (if the value is null, no can do - return
     * null)</li></ol>
     *
     * @param injectionPoint the {@link javax.enterprise.inject.spi.InjectionPoint}
     * @return value of the injected property or null if no value could be found.
     */
    @Produces
    @Property
    public String getStringConfigValue(final InjectionPoint injectionPoint) {

        final String memberName = injectionPoint.getMember()
                                                .getName();
        final String fullyQualifiedName = String.format("%s.%s", injectionPoint.getMember()
                                                                               .getDeclaringClass()
                                                                               .getName(), memberName);

        // Trying with explicit key defined on the field
        final String key = injectionPoint.getAnnotated()
                                         .getAnnotation(Property.class)
                                         .value();
        final boolean isKeyDefined = !key.trim()
                                         .isEmpty();

        if (isKeyDefined) {
            return this.resolver.getValue(key);
        }

        // Falling back to fully-qualified field name resolving.
        String value = this.resolver.getValue(fullyQualifiedName);

        // No luck... so perhaps just the field name?
        if (value == null) {
            value = this.resolver.getValue(memberName);
        }

        if ((value == null) && injectionPoint.getAnnotated()
                                             .getAnnotation(Property.class)
                                             .required()) {
            throw new IllegalStateException(
                    "No value defined for field: " + fullyQualifiedName + " but field was marked as required.");
        }

        return value;
    }

    /**
     * Produces {@link Double} type of property from {@link String} type.
     *
     * Will throw {@link NumberFormatException} if the value cannot be parsed into a {@link Double}
     *
     * @param injectionPoint the {@link javax.enterprise.inject.spi.InjectionPoint}
     * @return value of the injected property or null if no value could be found.
     * @see PropertyValueProducer#getStringConfigValue(InjectionPoint)
     */
    @Produces
    @Property
    public Double getDoubleConfigValue(final InjectionPoint injectionPoint) {
        final String value = getStringConfigValue(injectionPoint);
        return (value != null) ? Double.valueOf(value) : null;
    }

    /**
     * Produces {@link Integer} type of property from {@link String} type.
     *
     * Will throw {@link NumberFormatException} if the value cannot be parsed into an {@link Integer}
     *
     * @param injectionPoint the {@link javax.enterprise.inject.spi.InjectionPoint}
     * @return Integer representation of the property
     * @see PropertyValueProducer#getStringConfigValue(InjectionPoint)
     */
    @Produces
    @Property
    public Integer getIntegerConfigValue(final InjectionPoint injectionPoint) {
        final String value = getStringConfigValue(injectionPoint);
        return (value != null) ? Integer.valueOf(value) : null;
    }
}
