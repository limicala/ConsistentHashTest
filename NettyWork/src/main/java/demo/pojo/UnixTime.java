package demo.pojo;

import java.io.Serializable;
import java.util.Date;

public class UnixTime implements Serializable {
    private final long time;

    public UnixTime(){
        time = System.currentTimeMillis();
    }
    public UnixTime(long time){
        this.time = time;
    }

    public long value() { return time; }

    public String toString(){
        return new Date((time - 2208988800L) * 1000L).toString();
    }
}
