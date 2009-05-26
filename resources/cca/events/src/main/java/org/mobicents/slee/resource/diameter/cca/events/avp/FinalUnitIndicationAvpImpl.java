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
package org.mobicents.slee.resource.diameter.cca.events.avp;

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodesNotSupported;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * Start time:13:51:00 2008-11-10<br>
 * Project: mobicents-diameter-parent<br>
 * Implementation of AVP: {@link FinalUnitIndicationAvp}
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class FinalUnitIndicationAvpImpl extends GroupedAvpImpl implements FinalUnitIndicationAvp {

  private static transient Logger logger = Logger.getLogger(FinalUnitIndicationAvpImpl.class);

  public FinalUnitIndicationAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getFilterIds()
   */
  public String[] getFilterIds() {
    if (!hasAvp(DiameterAvpCodesNotSupported.FILTER_ID)) {
      return null;
    }

    AvpSet set = super.avpSet.getAvps(DiameterAvpCodesNotSupported.FILTER_ID);
    String[] result = new String[set.size()];

    for (int index = 0; index < set.size(); index++) {
      Avp rawAvp = set.getAvpByIndex(index);
      try {
        result[index] = rawAvp.getUTF8String();
      } catch (AvpDataException e) {
        reportAvpFetchError("Failed at index: " + index + ", " + e.getMessage(), DiameterAvpCodesNotSupported.FILTER_ID);
        logger.error("Failure while trying to obtain Filter-Id AVP.", e);
      }
    }

    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getFinalUnitAction()
   */
  public FinalUnitActionType getFinalUnitAction() {
    if (hasAvp(CreditControlAVPCodes.Final_Unit_Action)) {
      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Final_Unit_Action);
      try {
        return FinalUnitActionType.REDIRECT.fromInt(rawAvp.getInteger32());
      } catch (Exception e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Final_Unit_Action);
        logger.error("Failure while trying to obtain Final-Unit-Action AVP.", e);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getRedirectServer()
   */
  public RedirectServerAvp getRedirectServer() {
    if (hasRedirectServer()) {
      Avp rawAvp = super.avpSet.getAvp(CreditControlAVPCodes.Redirect_Server);
      try {

        return new RedirectServerAvpImpl(CreditControlAVPCodes.Redirect_Server, rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      } catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Redirect_Server);
        logger.error("Failure while trying to obtain Redirect-Server AVP.", e);
      }
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getRestrictionFilterRules()
   */
  public IPFilterRuleAvp[] getRestrictionFilterRules() {
    if (hasAvp(CreditControlAVPCodes.Restriction_Filter_Rule)) {
      AvpSet set = super.avpSet.getAvps(CreditControlAVPCodes.Restriction_Filter_Rule);
      IPFilterRuleAvp[] result = new IPFilterRuleAvp[set.size()];

      for (int index = 0; index < set.size(); index++) {
        Avp rawAvp = set.getAvpByIndex(index);
        try {
          result[index] = new IPFilterRuleAvp(rawAvp.getOctetString());
        } catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), CreditControlAVPCodes.Restriction_Filter_Rule);
          logger.error("Failure while trying to obtain Restriction-Filter-Rule AVP.", e);
        }
      }
      return result;
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#hasFinalUnitAction()
   */
  public boolean hasFinalUnitAction() {
    return hasAvp(CreditControlAVPCodes.Final_Unit_Action);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#hasRedirectServer()
   */
  public boolean hasRedirectServer() {
    return hasAvp(CreditControlAVPCodes.Final_Unit_Indication);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setFilterId(java.lang.String)
   */
  public void setFilterId(String filterId) {
    this.setFilterIds(new String[] { filterId });
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setFilterIds(java.lang.String[])
   */
  public void setFilterIds(String[] filterIds) {
    for(String filterId : filterIds) {
      setFilterId(filterId);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setFinalUnitAction
   * (net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType)
   */
  public void setFinalUnitAction(FinalUnitActionType finalUnitAction) {
    addAvp(CreditControlAVPCodes.Final_Unit_Action, finalUnitAction.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRedirectServer
   * (net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp)
   */
  public void setRedirectServer(RedirectServerAvp redirectServer) {
    addAvp(CreditControlAVPCodes.Redirect_Server, redirectServer);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRestrictionFilterRule
   * (net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp)
   */
  public void setRestrictionFilterRule(IPFilterRuleAvp restrictionFilterRule) {
    addAvp(CreditControlAVPCodes.Restriction_Filter_Rule, restrictionFilterRule);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRestrictionFilterRules
   * (net.java.slee.resource.diameter.base.events.avp.IPFilterRuleAvp[])
   */
  public void setRestrictionFilterRules(IPFilterRuleAvp[] restrictionFilterRules) {
    for (IPFilterRuleAvp restrictionFilterRule : (IPFilterRuleAvp[]) restrictionFilterRules) {
      setRestrictionFilterRule(restrictionFilterRule);
    }
  }

}
