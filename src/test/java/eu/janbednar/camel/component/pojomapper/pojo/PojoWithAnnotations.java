package eu.janbednar.camel.component.pojomapper.pojo;

import eu.janbednar.camel.component.pojomapper.annotation.PojoMapper;

public class PojoWithAnnotations {
    @PojoMapper(ignore = true)
    private Object ignored;
    @PojoMapper(key = "objectHeader")
    private Object withCustomHeader;

    public Object getIgnored() {
        return ignored;
    }

    public Object getWithCustomHeader() {
        return withCustomHeader;
    }
}
