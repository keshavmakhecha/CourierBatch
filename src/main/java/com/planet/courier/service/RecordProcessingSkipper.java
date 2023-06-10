package com.planet.courier.service;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

import com.planet.courier.exception.MissingCountryException;

@Component
public class RecordProcessingSkipper implements SkipPolicy {

	//For temporary kept max_skip_count to max, as retry logic is not placed.
	private static final int MAX_SKIP_COUNT = Integer.MAX_VALUE;

	@Override
	public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
		if (t instanceof MissingCountryException && skipCount < MAX_SKIP_COUNT) {
			return true;
		}

		return false;
	}
}
