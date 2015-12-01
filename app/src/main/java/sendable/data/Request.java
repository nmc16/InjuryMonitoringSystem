package sendable.data;

import sendable.DataType;
import sendable.Sendable;

import java.util.Date;

public class Request implements Sendable {
    private final int type;
    private int uid;
    private long time;
    private long startTime;
    private long endTime;
    private int requestType;

    public Request() {
        type = DataType.REQUEST;
    }

    public Request(int uid, int requestType, long startTime, long endTime) {
        this();
        this.uid = uid;
        this.requestType = requestType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setUID(int uid) {
        this.uid = uid;
    }

    @Override
    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int getUID() {
        return uid;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public Date getDate() {
        return new Date(time);
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }
}
