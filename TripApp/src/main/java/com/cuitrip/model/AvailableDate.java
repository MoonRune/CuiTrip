package com.cuitrip.model;

import java.util.List;

/**
 * Created on 7/15.
 */
public class AvailableDate {
//    "availableDate": ["1438012800000"],
//            "sid": "40"

    private List<Long> availableDate;

    private List<Long> bookedDate;

    public List<Long> getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(List<Long> availableDate) {
        this.availableDate = availableDate;
    }

    public List<Long> getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(List<Long> bookedDate) {
        this.bookedDate = bookedDate;
    }
}

