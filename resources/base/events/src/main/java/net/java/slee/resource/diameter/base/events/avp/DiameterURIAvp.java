/*
 * Diameter Sh Resource Adaptor Type
 *
 * Copyright (C) 2006 Open Cloud Ltd.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of version 2.1 of the GNU Lesser 
 * General Public License as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301  USA, or see the FSF site: http://www.fsf.org.
 */
package net.java.slee.resource.diameter.base.events.avp;


/**
 * Java class to represent the DiameterURI AVP type.
 *
 * The DiameterURI MUST follow the Uniform Resource Identifiers (URI) syntax [URI] rules specified below.

<pre>
      "aaa://" FQDN [ port ] [ transport ] [ protocol ]

                      ; No transport security

      "aaas://" FQDN [ port ] [ transport ] [ protocol ]

                      ; Transport security used

      FQDN               = Fully Qualified Host Name

      port               = ":" 1*DIGIT

                      ; One of the ports used to listen for incoming connections.
                      ; If absent, the default Diameter port (3868) is assumed.

      transport          = ";transport=" transport-protocol

                      ; One of the transports used to listen
                      ; for incoming connections.  If absent,
                      ; the default SCTP [SCTP] protocol is
                      ; assumed.  UDP MUST NOT be used when
                      ; the aaa-protocol field is set to
                      ; diameter.

      transport-protocol = ( "tcp" / "sctp" / "udp" )

      protocol           = ";protocol=" aaa-protocol

                      ; If absent, the default AAA protocol
                      ; is diameter.

      aaa-protocol       = ( "diameter" / "radius" / "tacacs+" )

      The following are examples of valid Diameter host identities:

      aaa://host.example.com;transport=tcp
      aaa://host.example.com:6666;transport=tcp
      aaa://host.example.com;protocol=diameter
      aaa://host.example.com:6666;protocol=diameter
      aaa://host.example.com:6666;transport=tcp;protocol=diameter
      aaa://host.example.com:1813;transport=udp;protocol=radius
</pre>

 *
 * @author Open Cloud
*/
public interface DiameterURIAvp {
    
	public boolean getSecure();

    public String getHostname() ;

    public int getPort();

    public int getProtocol() ;

    public int getTransport() ;

    /**
     * Return the contents of this URI as a byte array.  Technically this type derives from OctetString, so we
     * need to be able to return the value in the base type.
     * @return the result of toString().getBytes()
     */

    public byte[] getBytes() ;
}