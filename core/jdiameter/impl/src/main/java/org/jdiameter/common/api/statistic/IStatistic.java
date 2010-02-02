/*
 * Copyright (c) 2006 jDiameter.
 * https://jdiameter.dev.java.net/
 *
 * License: GPL v3
 *
 * e-mail: erick.svenson@yahoo.com
 *
 */
package org.jdiameter.common.api.statistic;

/**
 * This interface describe extends methods of base class
 */
public interface IStatistic {

  enum Groups {
    Peer("Peer statistic"),
    PeerFSM("Peer FSM statistic"),
    Network("Network statistic"),
    Concurrent(" Concurrent factory statistics"),
    ScheduledExecService("ScheduledExecutorService statistic");

    private int id;
    private String description;

    Groups(String description) {
      this.id = Counter.staticValue++;
      this.description = description;
    }

    public int getId() {
      return id;
    }

    public String getDescription() {
      return description;
    }
  }

  enum Counters {

    AppGenRequest("Count of app generated requests"),
    AppGenRejectedRequest("Count of rejected app generated requests"),
    AppGenResponse("Count of app generated responses"),
    AppGenRejectedResponse("Count of rejected app generated responses"),
    NetGenRequest("Count of network generated processed requests"),
    NetGenRejectedRequest("Count of network generated rejected requests"),
    NetGenResponse("Count of network generated processed responses"),
    NetGenRejectedResponse("Count of network generated rejected responses"),
    SysGenResponse("Count of platform generated responses"),

    AppGenRequestPerSecond("Count of app generated request per second"),
    AppGenResponsePerSecond("Count of app generated responses per second"),
    NetGenResponsePerSecond("Count of network generated responses per second"),
    NetGenRequestPerSecond("Count of network generated request per second"),

    RequestListenerCount("Count of network request appIdToNetListener"),
    SelectorCount("Count of network request selectorToNetListener"),

    HeapMemory("Heap memory usage"),
    NoHeapMemory("No-heap memory usage"),
    MessageProcessingTime("Average time of processing message"),

    ConcurrentThread("Count thread in default thread group"),
    ConcurrentScheduledExecutedServices("Count of ScheduledExecutorServices"),

    WorkingThread("Count of working thread"),
    CanceledTasks("Count of canceled thread"),
    ExecTimeTask("Average execution time of task"),
    WaitTimeTask("Average waiting time for execution task"),
    BrokenTasks("Count of broken thread"),
    RejectedTasks("Count of rejected tasks"),
    QueueSize("Peer FSM queue size");

    private int id;
    private String description;

    Counters(String description) {
      this.id = Counter.recordValue++;
      this.description = description;
    }

    public int getId() {
      return id;
    }

    public String getDescription() {
      return description;
    }
  }

  /**
   * Merge statistic
   *
   * @param rec external statistic
   */
  public IStatistic appendCounter(IStatisticRecord... rec);

  public IStatisticRecord getRecordByName(String name);

  //
  boolean isEnable();

  IStatisticRecord[] getRecords();

  String getName();

  String getDescription();

  void enable(boolean e);

  void reset();

  static class Counter {
    static int recordValue;
    static int staticValue;
  }

}
