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
package net.java.slee.resource.diameter.cca.events.avp;

/**
 * <pre>
 *  &lt;b&gt;8.19. Used-Service-Unit AVP&lt;/b&gt;
 * 
 * 
 *   The Used-Service-Unit AVP is of type Grouped (AVP Code 446) and
 *   contains the amount of used units measured from the point when the
 *   service became active or, if interim interrogations are used during
 *   the session, from the point when the previous measurement ended.
 * 
 *   The Used-Service-Unit AVP is defined as follows (per the grouped-
 *   avp-def of RFC 3588 [DIAMBASE]):
 * 
 *      Used-Service-Unit ::= &lt; AVP Header: 446 &gt;
 *                            [ Tariff-Change-Usage ]
 *                            [ CC-Time ]
 *                            [ CC-Money ]
 *                            [ CC-Total-Octets ]
 *                            [ CC-Input-Octets ]
 *                            [ CC-Output-Octets ]
 *                            [ CC-Service-Specific-Units ]
 *                           *[ AVP ]
 * </pre>
 *      
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface UsedServiceUnitAvp extends RequestedServiceUnitAvp {

  // TODO: This extension implies another check in impl, but thats so much easier... :]

  /**
   * Sets the value of the Tariff-Change-Usage AVP, of type Enumerated. <br>
   * See:{@link TariffChangeUsageType}
   */
  public void setTariffChangeUsage(TariffChangeUsageType ttc);

  /**
   * Returns the value of the Tariff-Change-Usage AVP, of type Enumerated.
   * <br>
   * See:{@link TariffChangeUsageType}
   */
  public TariffChangeUsageType getTariffChangeUsage();

  /**
   * Returns true if Tariff-Change-Usage AVP is present in message.
   * 
   * @return
   */
  public boolean hasTariffChangeUsage();

}
