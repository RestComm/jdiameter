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
package org.jdiameter.api.validation;

import java.io.InputStream;

import org.jdiameter.api.Message;

/**
 * Second generation Dictionary/Validator. This interface is exposed directly.
 * 
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 * @author <a href="mailto:baranowb@gmail.com"> Bartosz Baranowski </a>
 * @version 1.1
 */
public interface Dictionary {

  /**
   * Fetches Avp representation for given code. If no such AVP is found,
   * <b>null</b> value is returned.
   * 
   * @param code
   *            - positive integer, equal to AVP code.
   * @return AvpRepresentation for given code or <b>null</b>
   */
  public AvpRepresentation getAvp(int code);

  /**
   * Fetches Avp representation for given code and vendorId. If no such AVP is
   * found, <b>null</b> value is returned.
   * 
   * @param code
   *            - positive integer, equal to AVP code.
   * @param vendorId
   *            - positive long representing vendor code.
   * @return AvpRepresentation for given code/vendor pair or <b>null</b>
   */
  public AvpRepresentation getAvp(int code, long vendorId);

  /**
   * Fetches Avp representation for given name. If no such AVP is found,
   * <b>null</b> value is returned.
   * 
   * @param name
   *            - name of AVP, like Session-Id.
   * @return AvpRepresentation for given name or <b>null</b>
   */
  public AvpRepresentation getAvp(String avpName);

  /**
   * 
   * @param commandCode
   *            - command code of message
   * @param isRequest
   *            - switches if lookup searches for request or answer
   * @return message representation for given code and flag value or
   *         <b>null</b>
   */
  public MessageRepresentation getMessage(int commandCode, boolean isRequest);

  /**
   * 
   * @param commandCode
   *            - command code of message
   * @param applicationId
   *            - application id present in message header
   * @param isRequest
   *            - switches if lookup searches for request or answer
   * 
   * @return message representation for given code, application and flag value
   *         or <b>null</b>
   */
  public MessageRepresentation getMessage(int commandCode, long applicationId, boolean isRequest);

  /**
   * Configures dictionary. It can be called multiple times, each call
   * reconfigures dictionary.
   * 
   * @param is
   */
  public void configure(InputStream is);

  // Validator ----------------------------------------------------------------

  /**
   * Gets whether validator is enabled.
   * 
   * @return <ul>
   *         <li><b>true</b> if validator is enabled</li>
   *         <li><b>false</b> if validator is disabled</li>
   *         </ul>
   */
  public boolean isEnabled();

  /**
   * Sets whether validator is enabled.
   * 
   * @param enabled true to enable the validator, false to disable it 
   */
  public void setEnabled(boolean enabled);

  /**
   * Gets whether validator is configured.
   * 
   * @return <ul>
   *         <li><b>true</b> if validator has been initialized</li>
   *         <li><b>false</b> if validator has not yet been initialized</li>
   *         </ul>
   */
  public boolean isConfigured();

  /**
   * Gets validator level for OUTGOING messages.
   * Possible values are defined at {@link ValidatorLevel}
   * 
   * @return an instance of {@link ValidatorLevel} representing the current level
   */
  public ValidatorLevel getSendLevel();

  /**
   * Gets validator level for OUTGOING messages.
   * Possible values are defined at {@link ValidatorLevel}
   * 
   * @param sendLevel an instance of {@link ValidatorLevel} representing the new level
   */
  public void setSendLevel(ValidatorLevel sendLevel);

  /**
   * Gets validator level for INCOMING messages.
   * Possible values are defined at {@link ValidatorLevel}
   * 
   * @return an instance of {@link ValidatorLevel} representing the current level
   */
  public ValidatorLevel getReceiveLevel();

  /**
   * Sets validator level for INCOMING messages.
   * Possible values are defined at {@link ValidatorLevel}
   * 
   * @param receiveLevel an instance of {@link ValidatorLevel} representing the new level
   */
  public void setReceiveLevel(ValidatorLevel receiveLevel);

  /**
   * Performs validation according to configured levels
   * 
   * @param message - message we want to validate
   * @param incoming - flag indicating if {@link #message} is incoming message or outgoing.
   * @throws AvpNotAllowedException - in case validation fails. Exception has details(avp code and similar) about failure.
   */
  public void validate(Message message, boolean incoming) throws AvpNotAllowedException;
}
