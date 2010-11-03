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
package org.jdiameter.api.rf.events;

import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.app.AppAnswerEvent;

/**
 * The Accounting Answer (ACA) messages, indicated by the Command-Code field set to 271 is sent by
 * the CDF to the CTF in order to reply to the ACR. 
 * 
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public interface RfAccountingAnswer extends AppAnswerEvent {

  public static final String _SHORT_NAME = "ACA";
  public static final String _LONG_NAME = "Accounting-Answer";

  public static final int code = 271;

  /**
   * @return Record type of answer
   * @throws org.jdiameter.api.AvpDataException if result code avp is not integer
   */
  int getAccountingRecordType() throws AvpDataException;

  /**
   * @return record number
   * @throws AvpDataException if result code avp is not integer
   */
  long getAccountingRecordNumber() throws AvpDataException;

}
