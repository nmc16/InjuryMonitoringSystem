package sendable.data;

import sendable.DataType;
import sendable.Sendable;

import java.util.Date;

public class Initialization implements Sendable {
    private final int type;
    private int uid;
    private long time;

    public Initialization() {
        type = DataType.INIT;
    }

    public Initialization(int uid, long time) {
        this();
        this.uid = uid;
        this.time = time;
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
}
