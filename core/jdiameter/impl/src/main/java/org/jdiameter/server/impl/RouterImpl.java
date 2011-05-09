package org.jdiameter.server.impl;

import static org.jdiameter.client.impl.helpers.Parameters.AcctApplId;
import static org.jdiameter.client.impl.helpers.Parameters.ApplicationId;
import static org.jdiameter.client.impl.helpers.Parameters.AuthApplId;
import static org.jdiameter.client.impl.helpers.Parameters.OwnRealm;
import static org.jdiameter.client.impl.helpers.Parameters.RealmEntry;
import static org.jdiameter.client.impl.helpers.Parameters.RealmTable;
import static org.jdiameter.client.impl.helpers.Parameters.VendorId;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryExpTime;
import static org.jdiameter.server.impl.helpers.Parameters.RealmEntryIsDynamic;
import static org.jdiameter.server.impl.helpers.Parameters.RealmHosts;
import static org.jdiameter.server.impl.helpers.Parameters.RealmLocalAction;
import static org.jdiameter.server.impl.helpers.Parameters.RealmName;

import java.net.URISyntaxException;
import java.net.UnknownServiceException;

import org.jdiameter.api.ApplicationId;
import org.jdiameter.api.Configuration;
import org.jdiameter.api.LocalAction;
import org.jdiameter.api.MetaData;
import org.jdiameter.api.URI;
import org.jdiameter.client.api.controller.IRealmTable;
import org.jdiameter.client.impl.helpers.Parameters;
import org.jdiameter.common.api.concurrent.IConcurrentFactory;
import org.jdiameter.server.api.IRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterImpl extends org.jdiameter.client.impl.router.RouterImpl implements IRouter {

	private static final Logger logger = LoggerFactory.getLogger(RouterImpl.class);


	public RouterImpl(IConcurrentFactory concurrentFactory, IRealmTable realmTable, Configuration config, MetaData metaData) {
		super(concurrentFactory, realmTable, config, metaData);
	}

	

}
