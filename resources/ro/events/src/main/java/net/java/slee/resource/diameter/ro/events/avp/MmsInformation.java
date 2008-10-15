package net.java.slee.resource.diameter.ro.events.avp;

import net.java.slee.resource.diameter.base.events.avp.AvpNotAllowedException;
import net.java.slee.resource.diameter.base.events.avp.DiameterAvp;
import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the MMS-Information grouped AVP type. From the Diameter Ro Reference Point Protocol Details (3GPP TS 32.299 V7.1.0) specification: 7.2.65 MMS-Information AVP The MMS-Information AVP (AVP code 877) is of type Grouped. Its purpose is to allow the transmission of additional MMS service specific information elements. It has the following ABNF grammar: (Note: the *[ AVP ] is not part of 3GPP TS 32.299, it was added to allow for more flexibility for extensions to Diameter Ro.) MMS-Information ::= AVP Header: 877 [ Originator-Address ] * [ Recipient-Address ] [ Submission-Time ] [ MM-Content-Type ] [ Priority ] [ Message-ID ] [ Message-Type ] [ Message-Size ] [ Message-Class ] [ Delivery-Report-Requested ] [ Read-Reply-Report-Requested ] [ MMBox-Storage-Information ] #exclude [ Applic-ID ] [ Reply-Applic-ID ] [ Aux-Applic-Info ] [ Content-Class ] [ DRM-Content ] [ Adaptations ] [ VASP-Id ] [ VAS-Id ] *[ AVP ]
 */
public interface MmsInformation extends GroupedAvp{
    /**
     * Returns the value of the Adaptations AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.Adaptations getAdaptations();

    /**
     * Returns the value of the Applic-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getApplicId();

    /**
     * Returns the value of the Aux-Applic-Info AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getAuxApplicInfo();

    /**
     * Returns the value of the Content-Class AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.ContentClass getContentClass();

    /**
     * Returns the value of the Delivery-Report-Requested AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested getDeliveryReportRequested();

    /**
     * Returns the value of the DRM-Content AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.DrmContent getDrmContent();

    /**
     * Returns the set of extension AVPs. The returned array contains the extension AVPs in the order they appear in the message. A return value of null implies that no extensions AVPs have been set.
     */
    abstract DiameterAvp[] getExtensionAvps();

    /**
     * Returns the value of the Message-Class AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MessageClass getMessageClass();

    /**
     * Returns the value of the Message-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getMessageId();

    /**
     * Returns the value of the Message-Size AVP, of type Unsigned32. A return value of null implies that the AVP has not been set.
     */
    abstract long getMessageSize();

    /**
     * Returns the value of the Message-Type AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MessageType getMessageType();

    /**
     * Returns the value of the MM-Content-Type AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.MmContentType getMmContentType();

    /**
     * Returns the value of the Originator-Address AVP, of type Grouped. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress getOriginatorAddress();

    /**
     * Returns the value of the Priority AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.Priority getPriority();

    /**
     * Returns the value of the Read-Reply-Report-Requested AVP, of type Enumerated. A return value of null implies that the AVP has not been set.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested getReadReplyReportRequested();

    /**
     * Returns the set of Recipient-Address AVPs. The returned array contains the AVPs in the order they appear in the message. A return value of null implies that no Recipient-Address AVPs have been set. The elements in the given array are RecipientAddress objects.
     */
    abstract net.java.slee.resource.diameter.ro.events.avp.RecipientAddress[] getRecipientAddresses();

    /**
     * Returns the value of the Reply-Applic-ID AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getReplyApplicId();

    /**
     * Returns the value of the Submission-Time AVP, of type Time. A return value of null implies that the AVP has not been set.
     */
    abstract java.util.Date getSubmissionTime();

    /**
     * Returns the value of the VAS-Id AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getVasId();

    /**
     * Returns the value of the VASP-Id AVP, of type UTF8String. A return value of null implies that the AVP has not been set.
     */
    abstract java.lang.String getVaspId();

    /**
     * Returns true if the Adaptations AVP is present in the message.
     */
    abstract boolean hasAdaptations();

    /**
     * Returns true if the Applic-ID AVP is present in the message.
     */
    abstract boolean hasApplicId();

    /**
     * Returns true if the Aux-Applic-Info AVP is present in the message.
     */
    abstract boolean hasAuxApplicInfo();

    /**
     * Returns true if the Content-Class AVP is present in the message.
     */
    abstract boolean hasContentClass();

    /**
     * Returns true if the Delivery-Report-Requested AVP is present in the message.
     */
    abstract boolean hasDeliveryReportRequested();

    /**
     * Returns true if the DRM-Content AVP is present in the message.
     */
    abstract boolean hasDrmContent();

    /**
     * Returns true if the Message-Class AVP is present in the message.
     */
    abstract boolean hasMessageClass();

    /**
     * Returns true if the Message-ID AVP is present in the message.
     */
    abstract boolean hasMessageId();

    /**
     * Returns true if the Message-Size AVP is present in the message.
     */
    abstract boolean hasMessageSize();

    /**
     * Returns true if the Message-Type AVP is present in the message.
     */
    abstract boolean hasMessageType();

    /**
     * Returns true if the MM-Content-Type AVP is present in the message.
     */
    abstract boolean hasMmContentType();

    /**
     * Returns true if the Originator-Address AVP is present in the message.
     */
    abstract boolean hasOriginatorAddress();

    /**
     * Returns true if the Priority AVP is present in the message.
     */
    abstract boolean hasPriority();

    /**
     * Returns true if the Read-Reply-Report-Requested AVP is present in the message.
     */
    abstract boolean hasReadReplyReportRequested();

    /**
     * Returns true if the Reply-Applic-ID AVP is present in the message.
     */
    abstract boolean hasReplyApplicId();

    /**
     * Returns true if the Submission-Time AVP is present in the message.
     */
    abstract boolean hasSubmissionTime();

    /**
     * Returns true if the VAS-Id AVP is present in the message.
     */
    abstract boolean hasVasId();

    /**
     * Returns true if the VASP-Id AVP is present in the message.
     */
    abstract boolean hasVaspId();

    /**
     * Sets the value of the Adaptations AVP, of type Enumerated.
     */
    abstract void setAdaptations(net.java.slee.resource.diameter.ro.events.avp.Adaptations adaptations);

    /**
     * Sets the value of the Applic-ID AVP, of type UTF8String.
     */
    abstract void setApplicId(java.lang.String applicId);

    /**
     * Sets the value of the Aux-Applic-Info AVP, of type UTF8String.
     */
    abstract void setAuxApplicInfo(java.lang.String auxApplicInfo);

    /**
     * Sets the value of the Content-Class AVP, of type Enumerated.
     */
    abstract void setContentClass(net.java.slee.resource.diameter.ro.events.avp.ContentClass contentClass);

    /**
     * Sets the value of the Delivery-Report-Requested AVP, of type Enumerated.
     */
    abstract void setDeliveryReportRequested(net.java.slee.resource.diameter.ro.events.avp.DeliveryReportRequested deliveryReportRequested);

    /**
     * Sets the value of the DRM-Content AVP, of type Enumerated.
     */
    abstract void setDrmContent(net.java.slee.resource.diameter.ro.events.avp.DrmContent drmContent);

    /**
     * Sets the set of extension AVPs with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getExtensionAvps() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
     */
    abstract void setExtensionAvps(DiameterAvp[] avps) throws AvpNotAllowedException;

    /**
     * Sets the value of the Message-Class AVP, of type Grouped.
     */
    abstract void setMessageClass(net.java.slee.resource.diameter.ro.events.avp.MessageClass messageClass);

    /**
     * Sets the value of the Message-ID AVP, of type UTF8String.
     */
    abstract void setMessageId(java.lang.String messageId);

    /**
     * Sets the value of the Message-Size AVP, of type Unsigned32.
     */
    abstract void setMessageSize(long messageSize);

    /**
     * Sets the value of the Message-Type AVP, of type Enumerated.
     */
    abstract void setMessageType(net.java.slee.resource.diameter.ro.events.avp.MessageType messageType);

    /**
     * Sets the value of the MM-Content-Type AVP, of type Grouped.
     */
    abstract void setMmContentType(net.java.slee.resource.diameter.ro.events.avp.MmContentType mmContentType);

    /**
     * Sets the value of the Originator-Address AVP, of type Grouped.
     */
    abstract void setOriginatorAddress(net.java.slee.resource.diameter.ro.events.avp.OriginatorAddress originatorAddress);

    /**
     * Sets the value of the Priority AVP, of type Enumerated.
     */
    abstract void setPriority(net.java.slee.resource.diameter.ro.events.avp.Priority priority);

    /**
     * Sets the value of the Read-Reply-Report-Requested AVP, of type Enumerated.
     */
    abstract void setReadReplyReportRequested(net.java.slee.resource.diameter.ro.events.avp.ReadReplyReportRequested readReplyReportRequested);

    /**
     * Sets a single Recipient-Address AVP in the message, of type Grouped.
     */
    abstract void setRecipientAddress(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress recipientAddress);

    /**
     * Sets the set of Recipient-Address AVPs, with all the values in the given array. The AVPs will be added to message in the order in which they appear in the array. Note: the array must not be altered by the caller following this call, and getRecipientAddresses() is not guaranteed to return the same array instance, e.g. an "==" check would fail.
     */
    abstract void setRecipientAddresses(net.java.slee.resource.diameter.ro.events.avp.RecipientAddress[] recipientAddresses);

    /**
     * Sets the value of the Reply-Applic-ID AVP, of type UTF8String.
     */
    abstract void setReplyApplicId(java.lang.String replyApplicId);

    /**
     * Sets the value of the Submission-Time AVP, of type Time.
     */
    abstract void setSubmissionTime(java.util.Date submissionTime);

    /**
     * Sets the value of the VAS-Id AVP, of type UTF8String.
     */
    abstract void setVasId(java.lang.String vasId);

    /**
     * Sets the value of the VASP-Id AVP, of type UTF8String.
     */
    abstract void setVaspId(java.lang.String vaspId);

}
