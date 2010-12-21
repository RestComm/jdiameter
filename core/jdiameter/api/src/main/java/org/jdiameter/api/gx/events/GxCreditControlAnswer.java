/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 * 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package org.jdiameter.api.gx.events;

import org.jdiameter.api.Avp;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Credit-Control-Answer (CCA) messages, indicated by the Command-Code field set to 272 is sent
 * by the OCF to the CTF in order to reply to the CCR.
 *
 * @author <a href="mailto:carl-magnus.bjorkell@emblacom.com"> Carl-Magnus Björkell </a>
 */
public interface GxCreditControlAnswer extends AppAnswerEvent {

    public static final String _SHORT_NAME = "CCA";
    public static final String _LONG_NAME = "Credit-Control-Answer";
    public static final int code = 272;

    boolean isCreditControlFailureHandlingAVPPresent();

    int getCredidControlFailureHandlingAVPValue();

    boolean isDirectDebitingFailureHandlingAVPPresent();

    int getDirectDebitingFailureHandlingAVPValue();

    boolean isRequestTypeAVPPresent();

    int getRequestTypeAVPValue();

    Avp getValidityTimeAvp();
}
