package com.emptypointer.hellocdut.model;

/**
 * Created by Sequarius on 2015/11/2.
 */
public class CallLogItem {
    private String number;
    private String name;
    private String callTime;
    private long duration;
    private int status;

    public CallLogItem(String number, String name, String callTime,
                       long duration, int status) {
        super();
        this.number = number;
        this.name = name;
        this.callTime = callTime;
        this.duration = duration;
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CallLogItem other = (CallLogItem) obj;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        return true;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getCallTime() {
        return callTime;
    }

    public long getDuration() {
        return duration;
    }

    public int getStatus() {
        return status;
    }
}