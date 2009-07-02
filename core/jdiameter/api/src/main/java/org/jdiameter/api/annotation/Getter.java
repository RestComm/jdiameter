package org.jdiameter.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows get child object from command / group avp and
 * get value for simple avp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.CONSTRUCTOR,ElementType.METHOD})
public @interface Getter {

}