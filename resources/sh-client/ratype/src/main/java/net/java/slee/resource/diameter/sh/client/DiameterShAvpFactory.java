package net.java.slee.resource.diameter.sh.client;

import net.java.slee.resource.diameter.base.DiameterAvpFactory;
import net.java.slee.resource.diameter.base.events.avp.VendorSpecificApplicationIdAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedApplicationsAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.SupportedFeaturesAvp;
import net.java.slee.resource.diameter.sh.client.events.avp.UserIdentityAvp;

public interface DiameterShAvpFactory {

	
	public DiameterAvpFactory getBaseFactory();
	
	/**
     * Create a SupportedFeatures (Grouped AVP) instance using required AVP values.
     */
    SupportedFeaturesAvp createSupportedFeatures(
        long vendorId
        , long featureListId
        , long featureList
    );

    /**
     * Create an empty SupportedFeatures (Grouped AVP) instance.
     */
    SupportedFeaturesAvp createSupportedFeatures();

    /**
     * Create a SupportedApplications (Grouped AVP) instance using required AVP values.
     */
    SupportedApplicationsAvp createSupportedApplications(
        long authApplicationId
        , long acctApplicationId
        , VendorSpecificApplicationIdAvp vendorSpecificApplicationId
    );

    /**
     * Create an empty SupportedApplications (Grouped AVP) instance.
     */
    SupportedApplicationsAvp createSupportedApplications();

    /**
     * Create an empty UserIdentity (Grouped AVP) instance.
     */
    UserIdentityAvp createUserIdentity();
	
}
