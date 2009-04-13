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

import org.apache.log4j.Logger;
import org.jdiameter.api.Avp;
import org.jdiameter.api.AvpDataException;
import org.jdiameter.api.AvpSet;
import org.mobicents.diameter.dictionary.AvpDictionary;
import org.mobicents.diameter.dictionary.AvpRepresentation;
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

  private static final Logger logger = Logger.getLogger( MmsInformationImpl.class );

  /**
   * @param code
   * @param vendorId
   * @param mnd
   * @param prt
   * @param value
   */
  public MmsInformationImpl( int code, long vendorId, int mnd, int prt, byte[] value )
  {
    super( code, vendorId, mnd, prt, value );
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getAdaptations()
   */
  public Adaptations getAdaptations()
  {
    if(hasAdaptations())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Adaptations.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ADAPTATIONS);
        logger.error( "Failure while trying to obtain Adaptations AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getApplicId()
   */
  public String getApplicId()
  {
    if(hasApplicId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.APPLIC_ID);
        logger.error( "Failure while trying to obtain Applic-ID AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getAuxApplicInfo()
   */
  public String getAuxApplicInfo()
  {
    if(hasAuxApplicInfo())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.AUX_APPLIC_INFO);
        logger.error( "Failure while trying to obtain Aux-Applic-Info AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getContentClass()
   */
  public ContentClass getContentClass()
  {
    if(hasContentClass())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return ContentClass.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.CONTENT_CLASS);
        logger.error( "Failure while trying to obtain Content-Class AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getDeliveryReportRequested()
   */
  public DeliveryReportRequested getDeliveryReportRequested()
  {
    if(hasDeliveryReportRequested())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return DeliveryReportRequested.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED);
        logger.error( "Failure while trying to obtain Delivery-Report-Requested AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getDrmContent()
   */
  public DrmContent getDrmContent()
  {
    if(hasDrmContent())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return DrmContent.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.DRM_CONTENT);
        logger.error( "Failure while trying to obtain DRM-Content AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageClass()
   */
  public MessageClass getMessageClass()
  {
    if(hasMessageClass())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new MessageClassImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MESSAGE_CLASS);
        logger.error( "Failure while trying to obtain Message-Class AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageId()
   */
  public String getMessageId()
  {
    if(hasMessageId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MESSAGE_ID);
        logger.error( "Failure while trying to obtain Message-ID AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageSize()
   */
  public long getMessageSize()
  {
    if(hasMessageSize())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUnsigned32();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MESSAGE_SIZE);
        logger.error( "Failure while trying to obtain Message-Size AVP.", e );
      }
    }

    return -1;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMessageType()
   */
  public MessageType getMessageType()
  {
    if(hasMessageType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return MessageType.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MESSAGE_TYPE);
        logger.error( "Failure while trying to obtain Message-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getMmContentType()
   */
  public MmContentType getMmContentType()
  {
    if(hasMmContentType())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new MmContentTypeImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.MM_CONTENT_TYPE);
        logger.error( "Failure while trying to obtain MM-Content-Type AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getOriginatorAddress()
   */
  public OriginatorAddress getOriginatorAddress()
  {
    if(hasOriginatorAddress())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return new OriginatorAddressImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.ORIGINATOR_ADDRESS);
        logger.error( "Failure while trying to obtain Originator-Address AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getPriority()
   */
  public Priority getPriority()
  {
    if(hasPriority())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return Priority.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.PRIORITY);
        logger.error( "Failure while trying to obtain Priority AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getReadReplyReportRequested()
   */
  public ReadReplyReportRequested getReadReplyReportRequested()
  {
    if(hasReadReplyReportRequested())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return ReadReplyReportRequested.fromInt( rawAvp.getInteger32() );
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED);
        logger.error( "Failure while trying to obtain Read-Reply-Report-Requested AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getRecipientAddresses()
   */
  public RecipientAddress[] getRecipientAddresses()
  {
    RecipientAddress[] recipientAddresses = null;

    AvpSet rawAvps = super.avpSet.getAvps(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);

    if(rawAvps != null && rawAvps.size() > 0)
    {
      recipientAddresses = new RecipientAddress[rawAvps.size()];

      for(int i = 0; i < rawAvps.size(); i++)
      {
        try
        {
          Avp rawAvp = rawAvps.getAvpByIndex(i);
          recipientAddresses[i] = new RecipientAddressImpl(rawAvp.getCode(), rawAvp.getVendorId(), rawAvp.isMandatory() ? 1 : 0, rawAvp.isEncrypted() ? 1 : 0, rawAvp.getRaw());
        }
        catch (AvpDataException e) {
          reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.RECIPIENT_ADDRESS);
          logger.error( "Failure while trying to obtain Recipient-Address AVP (index:" + i + ").", e );
        }
      }
    }

    return recipientAddresses;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getReplyApplicId()
   */
  public String getReplyApplicId()
  {
    if(hasReplyApplicId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.REPLY_APPLIC_ID);
        logger.error( "Failure while trying to obtain Reply-Applic-ID AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getSubmissionTime()
   */
  public Date getSubmissionTime()
  {
    if(hasSubmissionTime())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getTime();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.SUBMISSION_TIME);
        logger.error( "Failure while trying to obtain Submission-Time AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getVasId()
   */
  public String getVasId()
  {
    if(hasVasId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.VAS_ID);
        logger.error( "Failure while trying to obtain VAS-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#getVaspId()
   */
  public String getVaspId()
  {
    if(hasVaspId())
    {
      Avp rawAvp = super.avpSet.getAvp(DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);

      try
      {
        return rawAvp.getUTF8String();
      }
      catch (AvpDataException e) {
        reportAvpFetchError(e.getMessage(), DiameterRoAvpCodes.VASP_ID);
        logger.error( "Failure while trying to obtain VASP-Id AVP.", e );
      }
    }

    return null;
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasAdaptations()
   */
  public boolean hasAdaptations()
  {
    return hasAvp( DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasApplicId()
   */
  public boolean hasApplicId()
  {
    return hasAvp( DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasAuxApplicInfo()
   */
  public boolean hasAuxApplicInfo()
  {
    return hasAvp( DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasContentClass()
   */
  public boolean hasContentClass()
  {
    return hasAvp( DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasDeliveryReportRequested()
   */
  public boolean hasDeliveryReportRequested()
  {
    return hasAvp( DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasDrmContent()
   */
  public boolean hasDrmContent()
  {
    return hasAvp( DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageClass()
   */
  public boolean hasMessageClass()
  {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageId()
   */
  public boolean hasMessageId()
  {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageSize()
   */
  public boolean hasMessageSize()
  {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMessageType()
   */
  public boolean hasMessageType()
  {
    return hasAvp( DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasMmContentType()
   */
  public boolean hasMmContentType()
  {
    return hasAvp( DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasOriginatorAddress()
   */
  public boolean hasOriginatorAddress()
  {
    return hasAvp( DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasPriority()
   */
  public boolean hasPriority()
  {
    return hasAvp( DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasReadReplyReportRequested()
   */
  public boolean hasReadReplyReportRequested()
  {
    return hasAvp( DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasReplyApplicId()
   */
  public boolean hasReplyApplicId()
  {
    return hasAvp( DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasSubmissionTime()
   */
  public boolean hasSubmissionTime()
  {
    return hasAvp( DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasVasId()
   */
  public boolean hasVasId()
  {
    return hasAvp( DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#hasVaspId()
   */
  public boolean hasVaspId()
  {
    return hasAvp( DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID );
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setAdaptations(net.java.slee.resource.diameter.ro.events.avp.Adaptations)
   */
  public void setAdaptations( Adaptations adaptations )
  {
    if(hasAdaptations())
    {
      throw new IllegalStateException("AVP Adaptations is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ADAPTATIONS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ADAPTATIONS, adaptations.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setApplicId(java.lang.String)
   */
  public void setApplicId( String applicId )
  {
    if(hasApplicId())
    {
      throw new IllegalStateException("AVP Applic-ID is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.APPLIC_ID, applicId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setAuxApplicInfo(java.lang.String)
   */
  public void setAuxApplicInfo( String auxApplicInfo )
  {
    if(hasAuxApplicInfo())
    {
      throw new IllegalStateException("AVP Aux-Applic-Info is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.AUX_APPLIC_INFO, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.AUX_APPLIC_INFO, auxApplicInfo, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setContentClass(net.java.slee.resource.diameter.ro.events.avp.ContentClass)
   */
  public void setContentClass( ContentClass contentClass )
  {
    if(hasContentClass())
    {
      throw new IllegalStateException("AVP Content-Class is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.CONTENT_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.CONTENT_CLASS, contentClass.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setDeliveryReportRequested(net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested)
   */
  public void setDeliveryReportRequested( DeliveryReportRequested deliveryReportRequested )
  {
    if(hasDeliveryReportRequested())
    {
      throw new IllegalStateException("AVP Delivery-Report-Requested is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.DELIVERY_REPORT_REQUESTED, deliveryReportRequested.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setDrmContent(net.java.slee.resource.diameter.ro.events.avp.DrmContent)
   */
  public void setDrmContent( DrmContent drmContent )
  {
    if(hasDrmContent())
    {
      throw new IllegalStateException("AVP DRM-Content is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.DRM_CONTENT, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.DRM_CONTENT, drmContent.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageClass(net.java.slee.resource.diameter.ro.events.avp.MessageClass)
   */
  public void setMessageClass( MessageClass messageClass )
  {
    if(hasMessageClass())
    {
      throw new IllegalStateException("AVP Message-Class is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_CLASS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_CLASS, messageClass.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageId(java.lang.String)
   */
  public void setMessageId( String messageId )
  {
    if(hasMessageId())
    {
      throw new IllegalStateException("AVP Message-ID is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_ID, messageId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageSize(long)
   */
  public void setMessageSize( long messageSize )
  {
    if(hasMessageSize())
    {
      throw new IllegalStateException("AVP Message-Size is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_SIZE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_SIZE, messageSize, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, true);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMessageType(net.java.slee.resource.diameter.ro.events.avp.MessageType)
   */
  public void setMessageType( MessageType messageType )
  {
    if(hasMessageType())
    {
      throw new IllegalStateException("AVP Message-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MESSAGE_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MESSAGE_TYPE, messageType.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setMmContentType(net.java.slee.resource.diameter.ro.events.avp.MmContentType)
   */
  public void setMmContentType( MmContentType mmContentType )
  {
    if(hasMmContentType())
    {
      throw new IllegalStateException("AVP MM-Content-Type is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.MM_CONTENT_TYPE, mmContentType.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setOriginatorAddress(net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress)
   */
  public void setOriginatorAddress( OriginatorAddress originatorAddress )
  {
    if(hasOriginatorAddress())
    {
      throw new IllegalStateException("AVP Originator-Address is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.ORIGINATOR_ADDRESS, originatorAddress.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setPriority(net.java.slee.resource.diameter.ro.events.avp.Priority)
   */
  public void setPriority( Priority priority )
  {
    if(hasPriority())
    {
      throw new IllegalStateException("AVP Priority is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.PRIORITY, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.PRIORITY, priority.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setReadReplyReportRequested(net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested)
   */
  public void setReadReplyReportRequested( ReadReplyReportRequested readReplyReportRequested )
  {
    if(hasReadReplyReportRequested())
    {
      throw new IllegalStateException("AVP Read-Reply-Report-Requested is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.READ_REPLY_REPORT_REQUESTED, readReplyReportRequested.getValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setRecipientAddress(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress)
   */
  public void setRecipientAddress( RecipientAddress recipientAddress )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    super.avpSet.addAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, recipientAddress.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setRecipientAddresses(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress[])
   */
  public void setRecipientAddresses( RecipientAddress[] recipientAddresses )
  {
    AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, DiameterRoAvpCodes.TGPP_VENDOR_ID);
    int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
    int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

    //super.avpSet.removeAvp(DiameterRoAvpCodes.);
    for(RecipientAddress recipientAddress : recipientAddresses)
    {
      super.avpSet.addAvp(DiameterRoAvpCodes.RECIPIENT_ADDRESS, recipientAddress.byteArrayValue(), DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setReplyApplicId(java.lang.String)
   */
  public void setReplyApplicId( String replyApplicId )
  {
    if(hasReplyApplicId())
    {
      throw new IllegalStateException("AVP Reply-Applic-ID is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.REPLY_APPLIC_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.REPLY_APPLIC_ID, replyApplicId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setSubmissionTime(java.util.Date)
   */
  public void setSubmissionTime( Date submissionTime )
  {
    if(hasSubmissionTime())
    {
      throw new IllegalStateException("AVP Submission-Time is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.SUBMISSION_TIME, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.SUBMISSION_TIME, submissionTime, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setVasId(java.lang.String)
   */
  public void setVasId( String vasId )
  {
    if(hasVasId())
    {
      throw new IllegalStateException("AVP VAS-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.VAS_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.VAS_ID, vasId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

  /* (non-Javadoc)
   * @see net.java.slee.resource.diameter.ro.events.avp.MmsInformation#setVaspId(java.lang.String)
   */
  public void setVaspId( String vaspId )
  {
    if(hasVaspId())
    {
      throw new IllegalStateException("AVP VASP-Id is already present in message and cannot be overwritten.");
    }
    else
    {
      AvpRepresentation avpRep = AvpDictionary.INSTANCE.getAvp(DiameterRoAvpCodes.VASP_ID, DiameterRoAvpCodes.TGPP_VENDOR_ID);
      int mandatoryAvp = avpRep.getRuleMandatory().equals("mustnot") || avpRep.getRuleMandatory().equals("shouldnot") ? 0 : 1;
      int protectedAvp = avpRep.getRuleProtected().equals("must") ? 1 : 0;

      //super.avpSet.removeAvp(DiameterRoAvpCodes.);
      super.avpSet.addAvp(DiameterRoAvpCodes.VASP_ID, vaspId, DiameterRoAvpCodes.TGPP_VENDOR_ID, mandatoryAvp == 1, protectedAvp == 1, false);
    }
  }

}
