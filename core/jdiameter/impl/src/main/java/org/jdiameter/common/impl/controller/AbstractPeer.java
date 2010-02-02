package org.jdiameter.common.impl.controller;

import org.jdiameter.api.Peer;
import org.jdiameter.api.URI;
import org.jdiameter.client.impl.helpers.UIDGenerator;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticFactory;
import org.jdiameter.common.api.statistic.IStatisticRecord;

public class AbstractPeer implements Comparable<Peer> {

  public static final int INT_COMMON_APP_ID = 0xffffffff;
  protected static UIDGenerator uid = new UIDGenerator();
  // Statistic
  protected IStatistic statistic;
  protected URI uri;

  public AbstractPeer(URI uri, IStatisticFactory statisticFactory) {
    this.uri = uri;

    IStatisticRecord appGenRequestCounter = statisticFactory.newCounterRecord(IStatistic.Counters.AppGenRequest);
    IStatisticRecord appGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(
        IStatistic.Counters.AppGenRequestPerSecond, appGenRequestCounter);
    IStatisticRecord appGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatistic.Counters.AppGenRejectedRequest);

    IStatisticRecord appGenResponseCounter = statisticFactory.newCounterRecord(IStatistic.Counters.AppGenResponse);
    IStatisticRecord appGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(
        IStatistic.Counters.AppGenResponsePerSecond, appGenResponseCounter);
    IStatisticRecord appGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatistic.Counters.AppGenRejectedResponse);

    IStatisticRecord netGenRequestCounter = statisticFactory.newCounterRecord(IStatistic.Counters.NetGenRequest);
    IStatisticRecord netGenCPSRequestCounter = statisticFactory.newPerSecondCounterRecord(
        IStatistic.Counters.NetGenRequestPerSecond, netGenRequestCounter);
    IStatisticRecord netGenRejectedRequestCounter = statisticFactory.newCounterRecord(IStatistic.Counters.NetGenRejectedRequest);

    IStatisticRecord netGenResponseCounter = statisticFactory.newCounterRecord(IStatistic.Counters.NetGenResponse);
    IStatisticRecord netGenCPSResponseCounter = statisticFactory.newPerSecondCounterRecord(
        IStatistic.Counters.NetGenResponsePerSecond, netGenResponseCounter);
    IStatisticRecord netGenRejectedResponseCounter = statisticFactory.newCounterRecord(IStatistic.Counters.NetGenRejectedResponse);

    IStatisticRecord sysGenResponseCounter = statisticFactory.newCounterRecord(IStatistic.Counters.SysGenResponse);

    this.statistic = statisticFactory.newStatistic(IStatistic.Groups.Peer,
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
}
