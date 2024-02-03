package xyz.doikki.videocontroller.component;

import android.content.Context;
import android.net.TrafficStats;

public class UaoanWs {


    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;

    public String getNetSpeed(Context context) {
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid)==
                TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) *1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        if(speed >= 1024){
            speed = speed / 1024;
            return speed+" M/s";
        }else
        {
            return speed+" K/s";
        }
    }

}
