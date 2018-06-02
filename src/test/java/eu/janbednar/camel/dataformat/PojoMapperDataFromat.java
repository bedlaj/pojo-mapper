package eu.janbednar.camel.dataformat;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

import java.io.InputStream;
import java.io.OutputStream;

@Deprecated
class PojoMapperDataFromat implements DataFormat {
    @Override
    public void marshal(Exchange exchange, Object graph, OutputStream stream) {
        throw new UnsupportedOperationException("Not implemented yes");
    }

    @Override
    public Object unmarshal(Exchange exchange, InputStream stream) {
        throw new UnsupportedOperationException("Not implemented yes");
    }
}
