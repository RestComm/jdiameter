package org.jdiameter.server.impl.helpers;

import org.jdiameter.api.Statistic;
import org.jdiameter.api.StatisticRecord;
import org.jdiameter.common.api.statistic.IStatistic;
import org.jdiameter.common.api.statistic.IStatisticRecord;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Set;

public class StatisticAdaptor {

  public static Statistic adapt(IStatistic statistic) {
    Object proxyObject = Proxy.newProxyInstance(
        StatisticAdaptor.class.getClassLoader(),
        new Class[]{Statistic.class},
        new MyStatisticHandler(statistic));
    return (Statistic) proxyObject;
  }

  static class MyStatisticHandler implements InvocationHandler {
    private IStatistic statistic;

    public MyStatisticHandler(IStatistic statistic) {
      this.statistic = statistic;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
      if (method.getName().equals("getName")) {
        return statistic.getName();
      }
      else if (method.getName().equals("getDescription")) {
        return statistic.getDescription();
      }
      else if (method.getName().equals("enable")) {
        statistic.enable((Boolean) args[0]);
        return null;
      }
      else if (method.getName().equals("isEnable")) {
        return statistic.isEnable();
      }
      else if (method.getName().equals("reset")) {
        statistic.reset();
        return null;
      }
      else if (method.getName().equals("getRecords")) {
        Set<StatisticRecord> list = new LinkedHashSet<StatisticRecord>();
        for (IStatisticRecord s : statistic.getRecords()) {
          list.add(
              (StatisticRecord) Proxy.newProxyInstance(
                  StatisticAdaptor.class.getClassLoader(),
                  new Class[]{StatisticRecord.class},
                  new MyStatisticRecordHandler(s))
          );
        }
        return list;
      }

      throw new IllegalArgumentException("Unknown method was called: " + method);
    }
  }

  static class MyStatisticRecordHandler implements InvocationHandler {
    private IStatisticRecord statisticRecord;

    public MyStatisticRecordHandler(IStatisticRecord statisticRecord) {
      this.statisticRecord = statisticRecord;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {

      if (method.getName().equals("getName")) {
        return statisticRecord.getName();
      }
      else if (method.getName().equals("getDescription")) {
        return statisticRecord.getDescription();
      }
      else if (method.getName().equals("getValueAsInt")) {
        return statisticRecord.getValueAsInt();
      }
      else if (method.getName().equals("getValueAsDouble")) {
        return statisticRecord.getValueAsDouble();
      }
      else if (method.getName().equals("getValueAsLong")) {
        return statisticRecord.getValueAsLong();
      }
      else if (method.getName().equals("getType")) {
        return statisticRecord.getType();
      }
      else if (method.getName().equals("getChilds")) {
        return statisticRecord.getChilds();
      }
      else if (method.getName().equals("reset")) {
        statisticRecord.reset();
        return null;
      }
      else if (method.getName().equals("hashCode")) {
        return statisticRecord.hashCode();
      }
      else if (method.getName().equals("equals")) {
        return statisticRecord.equals(args[0]);
      }
      else if (method.getName().equals("toString")) {
        return statisticRecord.toString();
      }

      throw new IllegalArgumentException("Unknown method was called: " + method);
    }
  }

}
