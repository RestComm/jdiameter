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
package org.mobicents.diameter.dictionary;

/**
 * 
 * <br>Project: mobicents-diameter-server
 * <br>6:18:48 PM May 27, 2009 
 * <br>
 *
 * >Class representing an AVP key, to be used in dictionary lookup.
 *
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class AvpKey {

  int avpCode;
  long vendorId;
  
  public AvpKey(int avpCode)
  {
    this.avpCode = avpCode;
  }
  
  public AvpKey(int avpCode, long vendorId)
  {
    this.avpCode = avpCode;
    this.vendorId = vendorId;
  }

  @Override
  public int hashCode()
  {
    return (int) ( avpCode * 31 + vendorId );
  }
  
  @Override
  public boolean equals( Object that )
  {
    if(this == that)
      return true;
    
    if(that instanceof AvpKey)
    {
      AvpKey other = (AvpKey) that;
      return (this.avpCode == other.avpCode) && (this.vendorId == other.vendorId); 
    }
    
    return false;
  }
}
