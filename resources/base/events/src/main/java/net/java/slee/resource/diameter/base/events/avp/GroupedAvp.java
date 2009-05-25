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
package net.java.slee.resource.diameter.base.events.avp;

/**
 * A GroupedAvp represents a Diameter Grouped AVP type.
 * This is the superinterface for the RA defined Grouped AVP types.
 * An array of the AVPs contained in the class can be accessed using
 * {@link org.mobicents.slee.resource.diameter.base.DiameterAvp#groupedAvpValue()}.
 * 
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 */
public interface GroupedAvp extends DiameterAvp {

	/**
	 * Sets the set of extension AVPs with all the values in the given array.
	 * The AVPs will be added to message in the order in which they appear in
	 * the array. Note: the array must not be altered by the caller following
	 * this call, and getExtensionAvps() is not guaranteed to return the same
	 * array instance, e.g. an "==" check would fail.
	 * 
	 * @param extensions
	 * @throws AvpNotAllowedException 
	 */
	public void setExtensionAvps(DiameterAvp[] extensions) throws AvpNotAllowedException;

	/**
	 * Returns the set of extension AVPs. The returned array contains the
	 * extension AVPs in the order they appear in the message. A return value of
	 * null implies that no extensions AVPs have been set.
	 * 
	 * @return
	 */
	public DiameterAvp[] getExtensionAvps();

	public boolean hasExtensionAvps();
	
}
