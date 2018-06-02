package eu.janbednar.camel.component.pojomapper.enums;

import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum FieldLocation {
    HEADER,
    PROPERTY,
    HEADER_PROPERTY,
    PROPERTY_HEADER
}
