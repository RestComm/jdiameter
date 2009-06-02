/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Middleware LLC.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 *
 * Boston, MA  02110-1301  USA
 */
package net.java.slee.resource.diameter.sh.client;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

/**
 * 
 * Start time:16:25:30 2009-05-23<br>
 * Project: diameter-parent<br>
 * Diameter Sh avp factory interface defining methods to create Sh specific avps.
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface DiameterShAvpFactory {

  public DiameterAvpFactory getBaseFactory();

  /**
   * Create a SupportedFeatures (Grouped AVP) instance using required AVP values.
   */
  SupportedFeaturesAvp createSupportedFeatures(long vendorId, long featureListId, long featureList);

  /**
   * Create an empty SupportedFeatures (Grouped AVP) instance.
   */
  SupportedFeaturesAvp createSupportedFeatures();

  /**
   * Create a SupportedApplications (Grouped AVP) instance using required AVP values.
   */
  SupportedApplicationsAvp createSupportedApplications(long authApplicationId, long acctApplicationId, VendorSpecificApplicationIdAvp vendorSpecificApplicationId);

  /**
   * Create an empty SupportedApplications (Grouped AVP) instance.
   */
  SupportedApplicationsAvp createSupportedApplications();

  /**
   * Create an empty UserIdentity (Grouped AVP) instance.
   */
  UserIdentityAvp createUserIdentity();


  /**
   * Validates User data against XML schema.
   * 
   * @return - Returns <b>true</b> xml validation passed - this requiers data
   *         to be valid xml document and must follow user data xml schema.
   */
  boolean validateUserData(byte[] b);

}
