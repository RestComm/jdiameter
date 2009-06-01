/*
 * Mobicents, Communications Middleware
 * 
 * Copyright (c) 2008, Red Hat Middleware LLC or third-party
 * contributors as
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
package org.mobicents.slee.resource.diameter.sh.client.handlers;

import javax.slee.resource.ActivityHandle;

import org.jdiameter.api.Answer;
import org.jdiameter.api.Request;
import org.jdiameter.api.sh.ClientShSession;

/**
 * 
 * Start time:11:02:25 2009-05-22<br>
 * Project: diameter-parent<br>
 * This interface is used by external resource (other than stack) to be notified
 * when session is termianted.
 * 
 * @author <a href="mailto:baranowb@gmail.com">Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface ShClientSessionListener {

	public static final String _UserDataAnswer = "net.java.slee.resource.diameter.sh.UserDataAnswer";
	public static final String _ProfileUpdateAnswer = "net.java.slee.resource.diameter.sh.ProfileUpdateAnswer";
	public static final String _SubscribeNotificationsAnswer = "net.java.slee.resource.diameter.sh.SubscribeNotificationsAnswer";
	public static final String _PushNotificationRequest = "net.java.slee.resource.diameter.sh.PushNotificationRequest";

	public static final String _ExtensionDiameterMessage = "net.java.slee.resource.diameter.base.events.ExtensionDiameterMessage";
	public static final String _ErrorAnswer = "net.java.slee.resource.diameter.base.events.ErrorAnswer";

	public void sessionDestroyed(String sessionId, ClientShSession session);

	/**
	 * Makes RA fire event with certain name.
	 * 
	 * @param sessionId
	 * @param name
	 * @param request
	 * @param answer
	 */
	public void fireEvent(ActivityHandle handle, String name, Request request, Answer answer);
}
