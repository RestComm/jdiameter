 /*
  * TeleStax, Open Source Cloud Communications
  * Copyright 2011-2016, TeleStax Inc. and individual contributors
  * by the @authors tag.
  *
  * This program is free software: you can redistribute it and/or modify
  * under the terms of the GNU Affero General Public License as
  * published by the Free Software Foundation; either version 3 of
  * the License, or (at your option) any later version.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.
  *
  * You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>
  *
  * This file incorporates work covered by the following copyright and
  * permission notice:
  *
  *   JBoss, Home of Professional Open Source
  *   Copyright 2007-2011, Red Hat, Inc. and individual contributors
  *   by the @authors tag. See the copyright.txt in the distribution for a
  *   full listing of individual contributors.
  *
  *   This is free software; you can redistribute it and/or modify it
  *   under the terms of the GNU Lesser General Public License as
  *   published by the Free Software Foundation; either version 2.1 of
  *   the License, or (at your option) any later version.
  *
  *   This software is distributed in the hope that it will be useful,
  *   but WITHOUT ANY WARRANTY; without even the implied warranty of
  *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  *   Lesser General Public License for more details.
  *
  *   You should have received a copy of the GNU Lesser General Public
  *   License along with this software; if not, write to the Free
  *   Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  *   02110-1301 USA, or see the FSF site: http://www.fsf.org.
  */

package org.jdiameter.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows describe Java POJO object as Diameter avp element
 *
 * @author erick.svenson@yahoo.com
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
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