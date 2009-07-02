package org.jdiameter.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows describe Java POJO object as Diameter avp element
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AvpDscr {
  
  /**
   * Return avp code
   * @return avp code
   */
  int code();
  
  /**
   * Return avp name
   * @return avp name
   */
  String name() default "unknown";
  
  /**
   * Return avp type
   * @return avp type
   */
  AvpType type();
  
  /**
   * Return avp vendor id
   * @return avp vendor id
   */
  long vendorId() default 0;
  
  /**
   * Return array of mandatory flags
   * @return array of mandatory flags
   */
  AvpFlag[] must() default {};
  
  /**
   * Return array optional flags
   * @return array optional flags
   */
  AvpFlag[] may() default {};
  
  /**
   * Return array flags which should not be set
   * @return array flags which should not be set
   */
  AvpFlag[] shldNot() default {};
  
  /**
   * Return array flags which must not be set
   * @return array flags which must not be set
   */
  AvpFlag[] mustNot() default {};
  
  /**
   * Return true if the flag can be encrypted
   * @return true if the flag can be encrypted
   */
  boolean mayEncr() default false;
  
  /**
   * Return array of avp child ( for grouped avp )
   * @return array of avp child
   */
  Child[] childs() default {};
}