/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.apiman.gateway.engine.rates;

import io.apiman.gateway.engine.components.IRateLimiterComponent;

import java.io.Serializable;
import java.util.Calendar;

/**
 * May be used by {@link IRateLimiterComponent} implementations.
 *
 * @author eric.wittmann@redhat.com
 */
public class RateLimiterBucket implements Serializable {

    private static final long serialVersionUID = 7322973438395289398L;
    
    public int count = 0;
    public long last = System.currentTimeMillis();
    
    /**
     * Constructor.
     */
    public RateLimiterBucket() {
    }
    
    /**
     * Resets the count if the period boundary has been crossed.
     * @param period
     */
    public void resetIfNecessary(RateBucketPeriod period) {
        long periodBoundary = getLastPeriodBoundary(period);
        if (System.currentTimeMillis() >= periodBoundary) {
            count = 0;
        }
    }
    
    /**
     * Returns the number of millis until the period resets.
     * @param period
     */
    public long getResetMillis(RateBucketPeriod period) {
        long now = System.currentTimeMillis();
        long periodBoundary = getPeriodBoundary(now, period);
        return periodBoundary - now;
    }

    /**
     * Gets the period boundary for the period bounding the last
     * request.
     * @param period
     */
    private long getLastPeriodBoundary(RateBucketPeriod period) {
        return getPeriodBoundary(last, period);
    }
    
    /**
     * Gets the boundary timestamp for the given rate bucket period.  In other words,
     * returns the timestamp associated with when the rate period will reset.
     * @param timestamp
     * @param period
     */
    private static long getPeriodBoundary(long timestamp, RateBucketPeriod period) {
        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(timestamp);
        switch (period) {
        case Second:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.add(Calendar.SECOND, 1);
            return lastCal.getTimeInMillis();
        case Minute:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.set(Calendar.SECOND, 0);
            lastCal.add(Calendar.MINUTE, 1);
            return lastCal.getTimeInMillis();
        case Hour:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.set(Calendar.SECOND, 0);
            lastCal.set(Calendar.MINUTE, 0);
            lastCal.add(Calendar.HOUR_OF_DAY, 1);
            return lastCal.getTimeInMillis();
        case Day:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.set(Calendar.SECOND, 0);
            lastCal.set(Calendar.MINUTE, 0);
            lastCal.set(Calendar.HOUR_OF_DAY, 0);
            lastCal.add(Calendar.DAY_OF_YEAR, 1);
            return lastCal.getTimeInMillis();
        case Month:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.set(Calendar.SECOND, 0);
            lastCal.set(Calendar.MINUTE, 0);
            lastCal.set(Calendar.HOUR_OF_DAY, 0);
            lastCal.add(Calendar.DAY_OF_MONTH, 1);
            return lastCal.getTimeInMillis();
        case Year:
            lastCal.set(Calendar.MILLISECOND, 0);
            lastCal.set(Calendar.SECOND, 0);
            lastCal.set(Calendar.MINUTE, 0);
            lastCal.set(Calendar.HOUR_OF_DAY, 0);
            lastCal.set(Calendar.DAY_OF_YEAR, 0);
            lastCal.add(Calendar.YEAR, 1);
            return lastCal.getTimeInMillis();
        }
        return Long.MAX_VALUE;
    }
}