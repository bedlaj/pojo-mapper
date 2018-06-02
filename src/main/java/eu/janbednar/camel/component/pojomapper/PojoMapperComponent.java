package eu.janbednar.camel.component.pojomapper;

import java.util.Map;

import eu.janbednar.camel.component.pojomapper.enums.FieldLocation;
import eu.janbednar.camel.component.pojomapper.enums.PojoDirection;
import org.apache.camel.Endpoint;

import org.apache.camel.impl.DefaultComponent;

/**
 * Represents the component that manages {@link PojoMapperEndpoint}.
 */
public class PojoMapperComponent extends DefaultComponent {
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        PojoMapperEndpoint endpoint = new PojoMapperEndpoint(uri, this);
        setProperties(endpoint, parameters);
        validateConfiguration(endpoint);
        return endpoint;
    }

    private void validateConfiguration(PojoMapperEndpoint endpoint){
        if (endpoint.getPojoDirection()== PojoDirection.FROM){
            if (endpoint.getFieldLocation() == FieldLocation.HEADER_PROPERTY || endpoint.getFieldLocation() == FieldLocation.PROPERTY_HEADER){
                throw new UnsupportedOperationException("pojoDiraction FROM cannot be used in combination with FieldLocation="+endpoint.getFieldLocation().name());
            }
        }
    }


}
