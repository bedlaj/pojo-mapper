package eu.janbednar.camel.component.pojomapper;

import eu.janbednar.camel.component.pojomapper.pojo.NestedNestedPojo;
import eu.janbednar.camel.component.pojomapper.pojo.PojoWithAnnotations;
import eu.janbednar.camel.component.pojomapper.pojo.PojoWithoutAnnotations;
import eu.janbednar.camel.component.pojomapper.pojo.RootPojo;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PojoMapperComponentToPojoTest extends CamelTestSupport {

    @Test
    public void testPojoMapperLongWithDate() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("fastTime", 123456L);
        sendBody("direct:defaults", new Date(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultDefaults");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals(123456L, mock.getExchanges().get(0).getIn().getBody(Date.class).getTime());


    }

    @Test
    public void testNestedPojo() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("root", "a");
        headers.put("nested", "b");
        headers.put("nestedNested", "c");
        sendBody("direct:defaults", new NestedNestedPojo(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultDefaults");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();

        NestedNestedPojo result = mock.getExchanges().get(0).getIn().getBody(NestedNestedPojo.class);

        assertEquals("a", result.getRoot());
        assertEquals("b", result.getNested());
        assertEquals("c", result.getNestedNested());
    }

    @Test
    public void testPojoMapperIntegerWithDate() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("fastTime", 123456);
        sendBody("direct:defaults", new Date(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultDefaults");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals(123456L, mock.getExchanges().get(0).getIn().getBody(Date.class).getTime());
    }

    @Test
    public void testPojoMapperFieldLocation_Header() throws Exception {
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("root", "header");
        exchange.setProperty("root", "property");
        exchange.getIn().setBody(new RootPojo());

        template.send("direct:fieldLocationHeader",exchange);
        MockEndpoint mock = getMockEndpoint("mock:resultFieldLocationHeader");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals("header", mock.getExchanges().get(0).getIn().getBody(RootPojo.class).getRoot());
    }

    @Test
    public void testPojoMapperFieldLocation_Property() throws Exception {
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("root", "header");
        exchange.setProperty("root", "property");
        exchange.getIn().setBody(new RootPojo());

        template.send("direct:fieldLocationProperty",exchange);
        MockEndpoint mock = getMockEndpoint("mock:resultFieldLocationProperty");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals("property", mock.getExchanges().get(0).getIn().getBody(RootPojo.class).getRoot());
    }

    @Test
    public void testPojoMapperFieldLocation_PropertyHeader() throws Exception {
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("root", "header");
        exchange.setProperty("root", "property");
        exchange.getIn().setBody(new RootPojo());

        template.send("direct:fieldLocationPropertyHeader",exchange);
        MockEndpoint mock = getMockEndpoint("mock:resultFieldLocationPropertyHeader");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals("property", mock.getExchanges().get(0).getIn().getBody(RootPojo.class).getRoot());
    }

    @Test
    public void testPojoMapperFieldLocation_HeaderProperty() throws Exception {
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("root", "header");
        exchange.setProperty("root", "property");
        exchange.getIn().setBody(new RootPojo());

        template.send("direct:fieldLocationHeaderProperty",exchange);
        MockEndpoint mock = getMockEndpoint("mock:resultFieldLocationHeaderProperty");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals("header", mock.getExchanges().get(0).getIn().getBody(RootPojo.class).getRoot());
    }

    @Test
    public void testPojoMapperWithAnnotations() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("ignored", 123);
        headers.put("objectHeader", 456);
        sendBody("direct:defaults", new PojoWithAnnotations(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultDefaults");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        PojoWithAnnotations result = mock.getExchanges().get(0).getIn().getBody(PojoWithAnnotations.class);
        assertEquals(456, result.getWithCustomHeader());
        assertNull(result.getIgnored());
    }

    @Test
    public void testLogMissingHeader() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        sendBody("direct:levelError", new PojoWithoutAnnotations(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultLevelError");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testMultipleItterations() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("root","root");
        sendBody("direct:multipleIterations", new RootPojo(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultMultipleIterations");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testThrowMissingHeader() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        sendBody("direct:throw", new PojoWithoutAnnotations(), headers);

        MockEndpoint mockResult = getMockEndpoint("mock:resultThrow");
        mockResult.expectedMinimumMessageCount(0);

        MockEndpoint mockException = getMockEndpoint("mock:exceptionThrow");
        mockException.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        assertEquals("Field PojoWithoutAnnotations#privateInt of type int, key privateInt not found",
            mockException.getExchanges().get(0).getIn().getBody()
        );
    }

    @Test
    public void testPojoMapperWithoutAnnotations_defaults() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("privateInt", 1);
        headers.put("privateBoolean", true);
        headers.put("privateByte", 1);
        headers.put("privateChar", "C");
        headers.put("privateDouble", 3d);
        headers.put("privateFloat", 4f);
        headers.put("privateLong", 5L);
        headers.put("privateShort", 6);
        headers.put("privateDate", new Date(123123123));
        headers.put("privateObject", "abc");
        headers.put("privateByteArray", new byte[]{1,0,1});
        headers.put("privateStringArray", new String[]{"a","b"});
        headers.put("privateListString", Arrays.asList("c","d"));

        sendBody("direct:defaults", new PojoWithoutAnnotations(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultDefaults");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();
        PojoWithoutAnnotations result = mock.getExchanges().get(0).getIn().getBody(PojoWithoutAnnotations.class);
        assertEquals(1,result.getPrivateInt());
        assertTrue(result.isPrivateBoolean());
        assertEquals(1, result.getPrivateByte());
        assertEquals('C', result.getPrivateChar());
        assertEquals(4f, result.getPrivateFloat(), 0f);
        assertEquals(5L, result.getPrivateLong());
        assertEquals(6, result.getPrivateShort());
        assertEquals(new Date(123123123), result.getPrivateDate());
        assertEquals("abc", result.getPrivateObject());

        assertEquals(1, result.getPrivateByteArray()[0]);
        assertEquals(0, result.getPrivateByteArray()[1]);
        assertEquals(1, result.getPrivateByteArray()[2]);

        assertEquals("a", result.getPrivateStringArray()[0]);
        assertEquals("b", result.getPrivateStringArray()[1]);

        assertEquals("c", result.getPrivateListString().get(0));
        assertEquals("d", result.getPrivateListString().get(1));

        assertEquals(3d, result.getPrivateDouble(),0d);

    }

    @Test
    public void testPojoMapperWithoutAnnotations_prefix() throws Exception {
        Map<String,Object> headers = new HashMap<>();
        headers.put("SomePrefixprivateInt", 1);
        headers.put("SomePrefixprivateBoolean", true);
        headers.put("SomePrefixprivateByte", 1);
        headers.put("SomePrefixprivateChar", 'C');
        headers.put("SomePrefixprivateDouble", 3d);
        headers.put("SomePrefixprivateFloat", 4f);
        headers.put("SomePrefixprivateLong", 5L);
        headers.put("SomePrefixprivateShort", 6);
        headers.put("SomePrefixprivateDate", new Date(123123123));
        headers.put("SomePrefixprivateObject", "abc");
        headers.put("SomePrefixprivateByteArray", new byte[]{1,0,1});
        headers.put("SomePrefixprivateStringArray", new String[]{"a","b"});
        headers.put("SomePrefixprivateListString", Arrays.asList("c","d"));

        sendBody("direct:prefix", new PojoWithoutAnnotations(), headers);

        MockEndpoint mock = getMockEndpoint("mock:resultPrefix");
        mock.expectedMinimumMessageCount(1);

        assertMockEndpointsSatisfied();

        PojoWithoutAnnotations result = mock.getExchanges().get(0).getIn().getBody(PojoWithoutAnnotations.class);
        assertEquals(1,result.getPrivateInt());
        assertTrue(result.isPrivateBoolean());
        assertEquals(1, result.getPrivateByte());
        assertEquals('C', result.getPrivateChar());
        assertEquals(4f, result.getPrivateFloat(), 0f);
        assertEquals(5L, result.getPrivateLong());
        assertEquals(6, result.getPrivateShort());
        assertEquals(new Date(123123123), result.getPrivateDate());
        assertEquals("abc", result.getPrivateObject());

        assertEquals(1, result.getPrivateByteArray()[0]);
        assertEquals(0, result.getPrivateByteArray()[1]);
        assertEquals(1, result.getPrivateByteArray()[2]);

        assertEquals("a", result.getPrivateStringArray()[0]);
        assertEquals("b", result.getPrivateStringArray()[1]);

        assertEquals("c", result.getPrivateListString().get(0));
        assertEquals("d", result.getPrivateListString().get(1));

        assertEquals(3d, result.getPrivateDouble(),0d);

    }

    private void assertHeaders(Exchange exchange, Map<String,Object> headers){
        headers.forEach((key, value) -> assertMessageHeader(exchange.getIn(), key, value));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                from("direct:defaults")
                        .to("pojo-mapper://defaults")
                        .to("mock:resultDefaults");

                from("direct:defaultsReverse")
                        .to("pojo-mapper://defaultsReverse?pojoDirection=FROM")
                        .to("mock:resultDefaults");

                from("direct:prefix")
                        .to("pojo-mapper://prefix?prefix=SomePrefix")
                        .to("mock:resultPrefix");

                from("direct:prefixReverse")
                        .to("pojo-mapper://prefixReverse?prefix=SomePrefix&pojoDirection=FROM")
                        .to("mock:resultPrefix");

                from("direct:levelError")
                        .to("pojo-mapper://levelError?missingSourceLevel=ERROR")
                        .to("mock:resultLevelError");

                from("direct:fieldLocationHeader")
                        .to("pojo-mapper://fieldLocationHeader?fieldLocation=HEADER")
                        .to("mock:resultFieldLocationHeader");

                from("direct:fieldLocationHeaderReverse")
                        .to("pojo-mapper://fieldLocationHeaderReverse?fieldLocation=HEADER&pojoDirection=FROM")
                        .to("mock:resultFieldLocationHeader");

                from("direct:fieldLocationProperty")
                        .to("pojo-mapper://fieldLocationProperty?fieldLocation=PROPERTY")
                        .to("mock:resultFieldLocationProperty");

                from("direct:fieldLocationPropertyReverse")
                        .to("pojo-mapper://fieldLocationPropertyReverse?fieldLocation=PROPERTY&pojoDirection=FROM")
                        .to("mock:resultFieldLocationProperty");

                from("direct:fieldLocationPropertyHeader")
                        .to("pojo-mapper://fieldLocationPropertyHeader?fieldLocation=PROPERTY_HEADER")
                        .to("mock:resultFieldLocationPropertyHeader");

                from("direct:fieldLocationHeaderProperty")
                        .to("pojo-mapper://fieldLocationHeaderProperty?fieldLocation=HEADER_PROPERTY")
                        .to("mock:resultFieldLocationHeaderProperty");

                from("direct:throw")
                        .onException(Exception.class)
                            .setBody(exceptionMessage())
                            .to("mock:exceptionThrow")
                            .end()
                        .to("pojo-mapper://throw?throwExceptionOnMissingSource=true")
                        .to("mock:resultThrow");

                RouteDefinition d = from("direct:multipleIterations");
                for (int i = 0;i<=10;i++){
                    d.to("pojo-mapper:"+i);
                }
                d.to("mock:resultMultipleIterations");
            }
        };
    }
}
