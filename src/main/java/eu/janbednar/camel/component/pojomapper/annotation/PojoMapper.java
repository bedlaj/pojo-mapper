package eu.janbednar.camel.component.pojomapper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PojoMapper {
    boolean ignore() default false;

    String key() default "";

    boolean required() default false;
}
