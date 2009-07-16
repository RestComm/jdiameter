package org.jdiameter.client.api.fsm;

import java.util.concurrent.ExecutorService;

public interface ExecutorFactory {

  ExecutorService getExecutor();

}
