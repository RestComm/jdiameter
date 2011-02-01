package org.jdiameter.common.impl.concurrent;

import org.jdiameter.common.api.statistic.IStatisticRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {
	private static final Logger log = LoggerFactory.getLogger(DefaultRejectedExecutionHandler.class);

	private IStatisticRecord rejectedCount;

	/**
	 * @param rejectedCount
	 */
	public DefaultRejectedExecutionHandler(IStatisticRecord rejectedCount) {
		this.rejectedCount = rejectedCount;
	}

	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
		log.debug("Task rejected {}", r);
		if(rejectedCount.isEnabled())
				rejectedCount.inc();
	}
}