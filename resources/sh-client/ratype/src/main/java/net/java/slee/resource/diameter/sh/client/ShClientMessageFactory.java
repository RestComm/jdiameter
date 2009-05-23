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
 */package net.java.slee.resource.diameter.sh.client;

import net.java.slee.resource.diameter.sh.client.events.avp.DataReferenceType;
import net.java.slee.resource.diameter.sh.client.events.avp.SubsReqType;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;
import net.java.slee.resource.diameter.sh.server.events.ProfileUpdateRequest;
import net.java.slee.resource.diameter.sh.server.events.PushNotificationAnswer;
import net.java.slee.resource.diameter.sh.server.events.SubscribeNotificationsRequest;
import net.java.slee.resource.diameter.sh.server.events.UserDataRequest;

/**
 * The Sh client interface to the Diameter MessageFactory used by applications to create Diameter Sh messages.
 */
public interface ShClientMessageFactory extends MessageFactory {

    /**
     * Create a UserDataRequest using the given parameters to populate the User-Identity and Data-Reference AVPs.
     * @return a UserDataRequest object that can be sent using {@link ShClientActivity#sendUserDataRequest(net.java.slee.resource.diameter.sh.types.UserDataRequest)} 
     */
    UserDataRequest createUserDataRequest(UserIdentityAvp userIdentity, DataReferenceType reference);

    /**
     * Create an empty UserDataRequest that will need to have AVPs set on it before being sent.
     * @return a UserDataRequest object that can be sent using {@link ShClientActivity#sendUserDataRequest(net.java.slee.resource.diameter.sh.types.UserDataRequest)} 
     */
    UserDataRequest createUserDataRequest();
    
    /**
     * Create a ProfileUpdateRequest using the given parameters to populate the User-Identity, Data-Reference and User-Data AVPs.
     * @return a ProfileUpdateRequest object that can be sent using {@link ShClientActivity#sendProfileUpdateRequest(net.java.slee.resource.diameter.sh.types.ProfileUpdateRequest)} 
     */
    ProfileUpdateRequest createProfileUpdateRequest(UserIdentityAvp userIdentity,
                                                    DataReferenceType reference,
                                                    byte[] userData);

    /**
     * Create an empty ProfileUpdateRequest that will need to have AVPs set on it before being sent.
     * @return a ProfileUpdateRequest object that can be sent using {@link ShClientActivity#sendProfileUpdateRequest(net.java.slee.resource.diameter.sh.types.ProfileUpdateRequest)} 
     */
    ProfileUpdateRequest createProfileUpdateRequest();

    /**
     * Create a SubscribeNotificationsRequest using the given parameters to populate the User-Identity, Data-Reference and Subs-Req-Type AVPs.
     * @return a SubscribeNotificationsRequest object that can be sent using {@link ShClientActivity#sendSubscribeNotificationsRequest(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsRequest)} 
     * or {@link ShClientNotificationActivity#sendSubscribeNotificationsRequest(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsRequest)}
     */
    SubscribeNotificationsRequest createSubscribeNotificationsRequest(UserIdentityAvp userIdentity,
                                                                      DataReferenceType reference,
                                                                      SubsReqType subscriptionType);

    /**
     * Create an empty SubscribeNotificationsRequest that will need to have AVPs set on it before being sent.
     * @return a SubscribeNotificationsRequest object that can be sent using {@link ShClientActivity#sendSubscribeNotificationsRequest(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsRequest)} 
     * or {@link ShClientNotificationActivity#sendSubscribeNotificationsRequest(net.java.slee.resource.diameter.sh.types.SubscribeNotificationsRequest)}
     */
    SubscribeNotificationsRequest createSubscribeNotificationsRequest();

    /**
     * Create a PushNotificationAnswer containing a Result-Code or Experimental-Result AVP populated with the given value.
     * If <code>isExperimentalResultCode</code> is <code>true</code>, the <code>resultCode</code> parameter will be set
     * in a {@link org.mobicents.slee.resource.diameter.base.types.ExperimentalResultAvp} AVP, if it is <code>false</code> it 
     * will be sent as a standard Result-Code AVP. 
     * @return a PushNotificationAnswer object that can be sent using {@link ShClientNotificationActivity#sendPushNotificationAnswer(net.java.slee.resource.diameter.sh.types.PushNotificationAnswer)} 
     */
    PushNotificationAnswer createPushNotificationAnswer(long resultCode, boolean isExperimentalResultCode);

    /**
     * Create an empty PushNotificationAnswer that will need to have AVPs set on it before being sent.
     * @return a PushNotificationAnswer object that can be sent using {@link ShClientNotificationActivity#sendPushNotificationAnswer(net.java.slee.resource.diameter.sh.types.PushNotificationAnswer)}
     */
    PushNotificationAnswer createPushNotificationAnswer();
}
