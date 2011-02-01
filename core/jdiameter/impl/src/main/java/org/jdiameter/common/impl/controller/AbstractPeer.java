package org.jdiameter.common.impl.controller;

import java.util.ArrayList;
import java.util.List;

import org.jdiameter.api.IllegalDiameterStateException;
import org.jdiameter.api.InternalException;
import org.jdiameter.api.Peer;
import org.jdiameter.api.URI;
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticManager;
import org.jdiameter.common.api.statistic.IStatisticRecord;

public class AbstractPeer implements Comparable<Peer> {

  public static final int INT_COMMON_APP_ID = 0xffffffff;
  protected static UIDGenerator uid = new UIDGenerator();
  // Statistic
  protected IStatistic statistic;
  protected List<IStatisticRecord> perSecondRecords = new ArrayList<IStatisticRecord>();
  protected URI uri;
  protected IStatisticManager statisticFactory;
  public AbstractPeer(URI uri, IStatisticManager statisticFactory) {
    this.uri = uri;
    this.statisticFactory = statisticFactory;
    String uriString = uri == null ? "local":uri.toString();
    
    IStatisticRecord appGenRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRequest);
    IStatisticRecord appGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.AppGenRequestPerSecond, appGenRequestCounter);
    IStatisticRecord appGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRejectedRequest);

    perSecondRecords.add(appGenCPSRequestCounter);
    
    IStatisticRecord appGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenResponse);
    IStatisticRecord appGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.AppGenResponsePerSecond, appGenResponseCounter);
    IStatisticRecord appGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.AppGenRejectedResponse);

    perSecondRecords.add(appGenCPSResponseCounter);
    
    IStatisticRecord netGenRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRequest);
    IStatisticRecord netGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.NetGenRequestPerSecond, netGenRequestCounter);
    IStatisticRecord netGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRejectedRequest);

    perSecondRecords.add(netGenCPSRequestCounter);
    
    IStatisticRecord netGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenResponse);
    IStatisticRecord netGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(uriString,IStatisticRecord.Counters.NetGenResponsePerSecond, netGenResponseCounter);
    IStatisticRecord netGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.NetGenRejectedResponse);

    perSecondRecords.add(netGenCPSResponseCounter);
    
    IStatisticRecord sysGenResponseCounter = statisticFactory.newCounterRecord(IStatisticRecord.Counters.SysGenResponse);
    //FIXME: remove NULL?
    this.statistic = statisticFactory.newStatistic(uriString,IStatistic.Groups.Peer,
        appGenRequestCounter, appGenCPSRequestCounter, appGenRejectedRequestCounter,
        appGenResponseCounter, appGenCPSResponseCounter, appGenRejectedResponseCounter,
        netGenRequestCounter, netGenCPSRequestCounter, netGenRejectedRequestCounter,
        netGenResponseCounter, netGenCPSResponseCounter, netGenRejectedResponseCounter,
        sysGenResponseCounter
    );
  }

  public int compareTo(Peer o) {
    return uri.compareTo(o.getUri());
  }

	/**
	 * @throws IllegalDiameterStateException 
	 * @throws InternalException 
	 * 
	 */
	protected void disconnect() throws InternalException, IllegalDiameterStateException {
		for(IStatisticRecord rec:this.perSecondRecords)
		{
			this.statisticFactory.removePerSecondCounterRecord(rec);
		}
		
		this.statisticFactory.removeStatistic(this.statistic);

	}
}
