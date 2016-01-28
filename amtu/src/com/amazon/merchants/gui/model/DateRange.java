/*
Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License"). You may
not use this file except in compliance with the License. A copy of the
License is located at

    http://aws.amazon.com/apache2.0/

or in the "license" file accompanying this file. This file is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
 */

package com.amazon.merchants.gui.model;

import java.util.Calendar;
import java.util.Date;

import com.amazon.merchants.Messages;

enum DateRangeConstant {
    TODAY(0), YESTERDAY(-1), TOMORROW(1), PAST3DAYS(-3), PAST7DAYS(-7), PAST30DAYS(
        -30), PAST180DAYS(-180);

    private int delta;


    DateRangeConstant(int delta) {
        this.delta = delta;
    }


    public int getDelta() {
        return delta;
    }
}

public enum DateRange {
    TODAY(DateRangeConstant.TODAY.getDelta(), DateRangeConstant.TOMORROW
        .getDelta(), Messages.DateRange_2.toString()), PAST3(
        DateRangeConstant.PAST3DAYS.getDelta(), DateRangeConstant.TOMORROW
            .getDelta(), Messages.DateRange_4.toString()), PAST7(
        DateRangeConstant.PAST7DAYS.getDelta(), DateRangeConstant.TOMORROW
            .getDelta(), Messages.DateRange_5.toString()), PAST30(
        DateRangeConstant.PAST30DAYS.getDelta(), DateRangeConstant.TOMORROW
            .getDelta(), Messages.DateRange_6.toString()), PAST180(
        DateRangeConstant.PAST180DAYS.getDelta(), DateRangeConstant.TOMORROW
            .getDelta(), Messages.DateRange_7.toString());

    private String str;
    private int from;
    private int to;


    DateRange(int from, int to, String str) {
        this.from = from;
        this.to = to;
        this.str = str;
    }


    public Date getFromDate() {
        return getDateFromDiff(from);
    }


    public Date getToDate() {
        return getDateFromDiff(to);
    }


    @Override
    public String toString() {
        return str;
    }


    private Date getDateFromDiff(int dateDiff) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, dateDiff);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
}
