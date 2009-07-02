package org.jdiameter.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows set child object to command / group avp and
 * set value to simple avp
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.CONSTRUCTOR,ElementType.METHOD})
public @interface Setter {
  /**
   * This enumeration defines for what Avp (known/not known) is used setter
   */
  enum Type {
    /**
     * For known avp
     */
    DEFINED,
    /**
     * For unknown avp
     */
    UNDEFINED
  }
  
  /**
   * Return  type of setter
   * @return type of setter
   */
  Type value() default Type.DEFINED;
}