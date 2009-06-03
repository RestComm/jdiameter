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
package org.mobicents.slee.resource.diameter.ro.events.avp;

import java.util.Date;

import net.java.slee.resource.diameter.ro.events.avp.Adaptations;
import net.java.slee.resource.diameter.ro.events.avp.ContentClass;
import net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested;
import net.java.slee.resource.diameter.ro.events.avp.DrmContent;
import net.java.slee.resource.diameter.ro.events.avp.MessageClass;
import net.java.slee.resource.diameter.ro.events.avp.MessageType;
import net.java.slee.resource.diameter.ro.events.avp.MmContentType;
import net.java.slee.resource.diameter.ro.events.avp.MmsInformation;
import net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress;
import net.java.slee.resource.diameter.ro.events.avp.Priority;
import net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested;
import net.java.slee.resource.diameter.ro.events.avp.RecipientAddress;

import org.mobicents.slee.resource.diameter.base.events.avp.GroupedAvpImpl;

/**
 * MmsInformationImpl.java
 *
 * <br>Project:  mobicents
 * <br>9:33:22 AM Apr 13, 2009 
 * <br>
 * @author <a href="mailto:brainslog@gmail.com"> Alexandre Mendonca </a>
 */
public class MmsInformationImpl extends GroupedAvpImpl implements MmsInformation {

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MmsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value ) {
    super( code, vendorId, mnd, prt, value );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getAdaptations()
   */
  public Adaptations getAdaptations() {
    return (Adaptations) getAvpAsEnumerated(DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID, Adaptations.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getApplicId()
   */
  public String getApplicId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getAuxApplicInfo()
   */
  public String getAuxApplicInfo() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getContentClass()
   */
  public ContentClass getContentClass() {
    return (ContentClass) getAvpAsEnumerated(DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, ContentClass.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getDeliveryReportRequested()
   */
  public DeliveryReportRequested getDeliveryReportRequested() {
    return (DeliveryReportRequested) getAvpAsEnumerated(DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, DeliveryReportRequested.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getDrmContent()
   */
  public DrmContent getDrmContent() {
    return (DrmContent) getAvpAsEnumerated(DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, DrmContent.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageClass()
   */
  public MessageClass getMessageClass() {
    return (MessageClass) getAvpAsCustom(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, MessageClassImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageId()
   */
  public String getMessageId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageSize()
   */
  public long getMessageSize() {
    return getAvpAsUnsigned32(DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageType()
   */
  public MessageType getMessageType() {
    return (MessageType) getAvpAsEnumerated(DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, MessageType.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMmContentType()
   */
  public MmContentType getMmContentType() {
    return (MmContentType) getAvpAsCustom(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, MmContentTypeImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getOriginatorAddress()
   */
  public OriginatorAddress getOriginatorAddress() {
    return (OriginatorAddress) getAvpAsCustom(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, OriginatorAddressImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getPriority()
   */
  public Priority getPriority() {
    return (Priority) getAvpAsEnumerated(DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID, Priority.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getReadReplyReportRequested()
   */
  public ReadReplyReportRequested getReadReplyReportRequested() {
    return (ReadReplyReportRequested) getAvpAsEnumerated(DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, ReadReplyReportRequested.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getRecipientAddresses()
   */
  public RecipientAddress[] getRecipientAddresses() {
    return (RecipientAddress[]) getAvpAsCustom(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, RecipientAddressImpl.class);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getReplyApplicId()
   */
  public String getReplyApplicId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getSubmissionTime()
   */
  public Date getSubmissionTime() {
    return getAvpAsTime(DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getVasId()
   */
  public String getVasId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getVaspId()
   */
  public String getVaspId() {
    return getAvpAsUTF8String(DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasAdaptations()
   */
  public boolean hasAdaptations() {
    return hasAvp( DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasApplicId()
   */
  public boolean hasApplicId() {
    return hasAvp( DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasAuxApplicInfo()
   */
  public boolean hasAuxApplicInfo() {
    return hasAvp( DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasContentClass()
   */
  public boolean hasContentClass() {
    return hasAvp( DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasDeliveryReportRequested()
   */
  public boolean hasDeliveryReportRequested() {
    return hasAvp( DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasDrmContent()
   */
  public boolean hasDrmContent() {
    return hasAvp( DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageClass()
   */
  public boolean hasMessageClass() {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageId()
   */
  public boolean hasMessageId() {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageSize()
   */
  public boolean hasMessageSize() {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageType()
   */
  public boolean hasMessageType() {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMmContentType()
   */
  public boolean hasMmContentType() {
    return hasAvp( DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasOriginatorAddress()
   */
  public boolean hasOriginatorAddress() {
    return hasAvp( DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasPriority()
   */
  public boolean hasPriority() {
    return hasAvp( DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasReadReplyReportRequested()
   */
  public boolean hasReadReplyReportRequested() {
    return hasAvp( DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasReplyApplicId()
   */
  public boolean hasReplyApplicId() {
    return hasAvp( DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasSubmissionTime()
   */
  public boolean hasSubmissionTime() {
    return hasAvp( DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasVasId()
   */
  public boolean hasVasId() {
    return hasAvp( DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasVaspId()
   */
  public boolean hasVaspId() {
    return hasAvp( DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setAdaptations(net.java.slee.resource.diameter.ro.events.avp.Adaptations)
   */
  public void setAdaptations( Adaptations adaptations ) {
    addAvp(DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID, adaptations.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setApplicId(java.lang.String)
   */
  public void setApplicId( String applicId ) {
    addAvp(DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, applicId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setAuxApplicInfo(java.lang.String)
   */
  public void setAuxApplicInfo( String auxApplicInfo ) {
    addAvp(DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID, auxApplicInfo);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setContentClass(net.java.slee.resource.diameter.ro.events.avp.ContentClass)
   */
  public void setContentClass( ContentClass contentClass ) {
    addAvp(DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, contentClass.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setDeliveryReportRequested(net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested)
   */
  public void setDeliveryReportRequested( DeliveryReportRequested deliveryReportRequested ) {
    addAvp(DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, deliveryReportRequested.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setDrmContent(net.java.slee.resource.diameter.ro.events.avp.DrmContent)
   */
  public void setDrmContent( DrmContent drmContent ) {
    addAvp(DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID, drmContent.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageClass(net.java.slee.resource.diameter.ro.events.avp.MessageClass)
   */
  public void setMessageClass( MessageClass messageClass ) {
    addAvp(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID, messageClass.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageId(java.lang.String)
   */
  public void setMessageId( String messageId ) {
    addAvp(DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, messageId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageSize(long)
   */
  public void setMessageSize( long messageSize ) {
    addAvp(DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID, messageSize);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageType(net.java.slee.resource.diameter.ro.events.avp.MessageType)
   */
  public void setMessageType( MessageType messageType ) {
    addAvp(DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, messageType.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMmContentType(net.java.slee.resource.diameter.ro.events.avp.MmContentType)
   */
  public void setMmContentType( MmContentType mmContentType ) {
    addAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID, mmContentType.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setOriginatorAddress(net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress)
   */
  public void setOriginatorAddress( OriginatorAddress originatorAddress ) {
    addAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, originatorAddress.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setPriority(net.java.slee.resource.diameter.ro.events.avp.Priority)
   */
  public void setPriority( Priority priority ) {
    addAvp(DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID, priority.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setReadReplyReportRequested(net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested)
   */
  public void setReadReplyReportRequested( ReadReplyReportRequested readReplyReportRequested ) {
    addAvp(DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID, readReplyReportRequested.getValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setRecipientAddress(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress)
   */
  public void setRecipientAddress( RecipientAddress recipientAddress ) {
    addAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID, recipientAddress.byteArrayValue());
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setRecipientAddresses(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress[])
   */
  public void setRecipientAddresses( RecipientAddress[] recipientAddresses ) {
    for(RecipientAddress recipientAddress : recipientAddresses) {
      setRecipientAddress(recipientAddress);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setReplyApplicId(java.lang.String)
   */
  public void setReplyApplicId( String replyApplicId ) {
    addAvp(DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, replyApplicId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setSubmissionTime(java.util.Date)
   */
  public void setSubmissionTime( Date submissionTime ) {
    addAvp(DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID, submissionTime);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setVasId(java.lang.String)
   */
  public void setVasId( String vasId ) {
    addAvp(DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, vasId);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setVaspId(java.lang.String)
   */
  public void setVaspId( String vaspId ) {
    addAvp(DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID, vaspId);
  }

}
