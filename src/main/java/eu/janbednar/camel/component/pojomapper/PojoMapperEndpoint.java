package eu.janbednar.camel.component.pojomapper;

import eu.janbednar.camel.component.pojomapper.enums.FieldLocation;
import eu.janbednar.camel.component.pojomapper.enums.PojoDirection;
import org.apache.camel.Consumer;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a PojoMapper endpoint.
 */
@UriEndpoint(scheme = "pojo-mapper", title = "PojoMapper", syntax = "pojo-mapper:name",
        producerOnly = true, label = "custom")
public class PojoMapperEndpoint extends DefaultEndpoint {
    @UriPath
    @Metadata(required = "true")
    private String name;
    @UriParam(description = "Prefix of source")
    private String prefix = "";
    @UriParam(defaultValue = "WARN", description = "Logging level of missing source")
    private LoggingLevel missingSourceLevel = LoggingLevel.WARN;

    @UriParam(defaultValue = "false", description = "Throw exception, if source cannot be found")
    private boolean throwExceptionOnMissingSource = false;

    @UriParam(defaultValue = "HEADER", description = "Source to mapping")
    private FieldLocation fieldLocation = FieldLocation.HEADER;
    @UriParam(defaultValue = "TO", description = "Direction of mapping")
    private PojoDirection pojoDirection = PojoDirection.TO;

    public PojoMapperEndpoint() {
    }

    public PojoMapperEndpoint(String uri, PojoMapperComponent component) {
        super(uri, component);
    }

    public PojoMapperEndpoint(String endpointUri) {
        super(endpointUri);
    }

    public PojoDirection getPojoDirection() {
        return pojoDirection;
    }

    public void setPojoDirection(PojoDirection pojoDirection) {
        this.pojoDirection = pojoDirection;
    }

    public Producer createProducer() throws Exception {
        return new PojoMapperProducer(this);
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("consumer not implemented");
        //return new PojoMapperConsumer(this, processor);
    }

    public boolean isSingleton() {
        return true;
    }

    public String getName() {
        return name;
    }

    /**
     * Some description of this option, and what it does
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public LoggingLevel getMissingSourceLevel() {
        return missingSourceLevel;
    }

    public void setMissingSourceLevel(LoggingLevel missingSourceLevel) {
        this.missingSourceLevel = missingSourceLevel;
    }

    public boolean isThrowExceptionOnMissingSource() {
        return throwExceptionOnMissingSource;
    }

    public void setThrowExceptionOnMissingSource(boolean throwExceptionOnMissingSource) {
        this.throwExceptionOnMissingSource = throwExceptionOnMissingSource;
    }

    public FieldLocation getFieldLocation() {
        return fieldLocation;
    }

    public void setFieldLocation(FieldLocation fieldLocation) {
        this.fieldLocation = fieldLocation;
    }

}
