package sendable.data;


import sendable.DataType;
import sendable.Sendable;

import java.util.Date;
import java.util.List;

public class Service<T extends Sendable> implements Sendable {
    private final int type;
    private int uid;
    private long time;
    private List<T> data;
    private int dataType;

    public Service() {
        type = DataType.SERVICE;
    }

    public Service(int uid, long time, List<T> data, int dataType) {
        this();
        this.uid = uid;
        this.time = time;
        this.data = data;
        this.dataType = dataType;
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

    public List<? extends Sendable> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
