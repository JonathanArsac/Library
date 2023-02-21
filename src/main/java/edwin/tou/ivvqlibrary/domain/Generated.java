package edwin.tou.ivvqlibrary.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation used to exempt classes and/or methods from Jacoco coverage report (usefull for POJO
 * classes full of generated getters, setters, hashcode, equals and toString methods)
 *
 * <p>/!\ DO NOT USE ON CLASS IMPLEMENTING BUSINESS LOGIC /!\
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Generated {
    String reason() default "";
}
