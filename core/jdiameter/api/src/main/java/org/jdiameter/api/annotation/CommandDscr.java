package org.jdiameter.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows describe Java POJO object as Diameter Command element
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandDscr {

  /**
   * Return command code
   * @return command code
   */
  int code();

  /**
   * Return command name
   * @return command name
   */
  String name() default "unknown";

  /**
   * Return array of command flags
   * @return array of command flags
   */
  CommandFlag[] flags() default{};

  /**
   * Return command application-id
   * @return command application-id
   */
  long appId() default 0;

  /**
   * Return array of command child avp
   * @return  array of command child avp
   */
  Child[] childs() default {};
}