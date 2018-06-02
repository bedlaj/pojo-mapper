package eu.janbednar.camel.component.pojomapper;

import eu.janbednar.camel.component.pojomapper.annotation.PojoMapper;
import eu.janbednar.camel.component.pojomapper.enums.FieldLocation;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.CamelLogger;
import org.apache.camel.util.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The PojoMapper producer.
 */
public class PojoMapperProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(PojoMapperProducer.class);
    private PojoMapperEndpoint endpoint;

    private static final Map<Class<?>, Class<?>> PRIMITIVES_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>() {{
        put(boolean.class, Boolean.class);
        put(byte.class, Byte.class);
        put(char.class, Character.class);
        put(double.class, Double.class);
        put(float.class, Float.class);
        put(int.class, Integer.class);
        put(long.class, Long.class);
        put(short.class, Short.class);
        put(void.class, Void.class);
    }};

    public PojoMapperProducer(PojoMapperEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        switch (endpoint.getPojoDirection()) {
            case FROM:
                processDirectionFromPojo(exchange);
                return;
            case TO:
                processDirectionToPojo(exchange);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void processDirectionFromPojo(Exchange exchange) throws Exception {

        Message in = exchange.getIn();
        Object body = in.getMandatoryBody();

        ReflectionHelper.doWithFields(body.getClass(), new ReflectionHelper.FieldCallback() {
            @Override
            public void doWith(Field f) throws IllegalArgumentException, IllegalAccessException {
                if (isIgnore(f)) return;

                String key = computeKey(f);
                boolean oldIsAccessible = f.isAccessible();
                f.setAccessible(true);
                if (endpoint.getFieldLocation() == FieldLocation.PROPERTY) {
                    exchange.setProperty(key, f.get(body));
                } else if (endpoint.getFieldLocation() == FieldLocation.HEADER) {
                    exchange.getIn().setHeader(key, f.get(body));
                }
                f.setAccessible(oldIsAccessible);
            }
        });
    }

    private void processDirectionToPojo(Exchange exchange) throws Exception {
        Message in = exchange.getIn();
        Object body = in.getMandatoryBody();

        ReflectionHelper.doWithFields(body.getClass(), new ReflectionHelper.FieldCallback() {
            @Override
            public void doWith(Field f) throws IllegalArgumentException, IllegalAccessException {
                if (isIgnore(f)) return;

                String key = computeKey(f);

                if (!isValid(key, exchange)) {
                    String message = String.format("Field %s#%s of type %s, key %s not found",
                            f.getDeclaringClass().getSimpleName(), f.getName(), f.getType(), key);
                    CamelLogger.log(
                            LOG, endpoint.getMissingSourceLevel(),
                            message
                    );
                    if (endpoint.isThrowExceptionOnMissingSource() || isRequired(f)) {
                        throw new IllegalStateException(message);
                    }
                    return;
                }
                Class<?> toClass = wrap(f.getType());

                Object value = getValue(key, toClass, exchange);
                ReflectionHelper.setField(f, body, value);
            }

        });
        exchange.getIn().setBody(body);
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> wrap(Class<T> c) {
        return c.isPrimitive() ? (Class<T>) PRIMITIVES_TO_WRAPPERS.get(c) : c;
    }

    private String computeKey(Field field) {
        String key = endpoint.getPrefix() + field.getName();
        if (field.isAnnotationPresent(PojoMapper.class)) {
            String annotationHeader = field.getAnnotation(PojoMapper.class).key().trim();
            if (!annotationHeader.equals("")) {
                return annotationHeader;
            }
        }
        return key;
    }

    private <T> T getValue(String key, Class<T> clazz, Exchange exchange) {
        if (endpoint.getFieldLocation() == FieldLocation.HEADER) {
            return exchange.getIn().getHeader(key, clazz);
        }
        if (endpoint.getFieldLocation() == FieldLocation.PROPERTY) {
            return exchange.getProperty(key, clazz);
        }
        if (endpoint.getFieldLocation() == FieldLocation.HEADER_PROPERTY) {
            if (hasHeader(key, exchange)) {
                return exchange.getIn().getHeader(key, clazz);
            }
            if (hasProperty(key, exchange)) {
                return exchange.getProperty(key, clazz);
            }
        }
        if (endpoint.getFieldLocation() == FieldLocation.PROPERTY_HEADER) {
            if (hasProperty(key, exchange)) {
                return exchange.getProperty(key, clazz);
            }
            if (hasHeader(key, exchange)) {
                return exchange.getIn().getHeader(key, clazz);
            }
        }
        throw new IllegalStateException("This should never happen EVER");
    }


    private boolean isIgnore(Field field) {
        return field.isAnnotationPresent(PojoMapper.class) && field.getAnnotation(PojoMapper.class).ignore();
    }

    private boolean isRequired(Field field) {
        return field.isAnnotationPresent(PojoMapper.class) && field.getAnnotation(PojoMapper.class).required();
    }

    private boolean isValid(String key, Exchange exchange) {
        if (endpoint.getFieldLocation() == FieldLocation.HEADER) {
            return hasHeader(key, exchange);
        }
        if (endpoint.getFieldLocation() == FieldLocation.PROPERTY) {
            return hasProperty(key, exchange);
        }
        if (Arrays.asList(FieldLocation.HEADER_PROPERTY, FieldLocation.PROPERTY_HEADER).contains(endpoint.getFieldLocation())) {
            return hasProperty(key, exchange) || hasHeader(key, exchange);
        }
        throw new UnsupportedOperationException(endpoint.getFieldLocation() + " not implemented");
    }

    private boolean hasHeader(String key, Exchange exchange) {
        return exchange.getIn().hasHeaders() && exchange.getIn().getHeaders().containsKey(key);
    }

    private boolean hasProperty(String key, Exchange exchange) {
        return exchange.hasProperties() && exchange.getProperties().containsKey(key);
    }

}
