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

import net.java.slee.resource.diameter.base.events.avp.DiameterAvpCodes;
import net.java.slee.resource.diameter.base.events.avp.IPFilterRule;
import net.java.slee.resource.diameter.cca.events.avp.CreditControlAVPCodes;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitActionType;
import net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp;
import net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp;

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

  public FinalUnitIndicationAvpImpl(int code, long vendorId, int mnd, int prt, byte[] value) {
    super(code, vendorId, mnd, prt, value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getFilterIds()
   */
  public String[] getFilterIds() {
    return getAvpsAsUTF8String(DiameterAvpCodes.FILTER_ID);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getFinalUnitAction()
   */
  public FinalUnitActionType getFinalUnitAction() {
    return (FinalUnitActionType) getAvpAsEnumerated(CreditControlAVPCodes.Final_Unit_Action,FinalUnitActionType.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getRedirectServer()
   */
  public RedirectServerAvp getRedirectServer() {
    return (RedirectServerAvp) getAvpAsCustom(CreditControlAVPCodes.Redirect_Server, RedirectServerAvpImpl.class);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#getRestrictionFilterRules()
   */
  public IPFilterRule[] getRestrictionFilterRules() {
    return (IPFilterRule[]) getAvpsAsEnumerated(CreditControlAVPCodes.Restriction_Filter_Rule, IPFilterRule.class);
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
    return hasAvp(CreditControlAVPCodes.Redirect_Server);
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setFilterId(java.lang.String)
   */
  public void setFilterId(String filterId) {
    addAvp(DiameterAvpCodes.FILTER_ID, filterId);
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
    addAvp(CreditControlAVPCodes.Final_Unit_Action, (long)finalUnitAction.getValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRedirectServer
   * (net.java.slee.resource.diameter.cca.events.avp.RedirectServerAvp)
   */
  public void setRedirectServer(RedirectServerAvp redirectServer) {
    addAvp(CreditControlAVPCodes.Redirect_Server, redirectServer.byteArrayValue());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRestrictionFilterRule
   * (net.java.slee.resource.diameter.base.events.avp.IPFilterRule)
   */
  public void setRestrictionFilterRule(IPFilterRule restrictionFilterRule) {
    addAvp(CreditControlAVPCodes.Restriction_Filter_Rule, restrictionFilterRule.getRuleString());
  }

  /*
   * (non-Javadoc)
   * 
   * @see net.java.slee.resource.diameter.cca.events.avp.FinalUnitIndicationAvp#setRestrictionFilterRules
   * (net.java.slee.resource.diameter.base.events.avp.IPFilterRule[])
   */
  public void setRestrictionFilterRules(IPFilterRule[] restrictionFilterRules) {
    for (IPFilterRule restrictionFilterRule : (IPFilterRule[]) restrictionFilterRules) {
      setRestrictionFilterRule(restrictionFilterRule);
    }
  }

}
