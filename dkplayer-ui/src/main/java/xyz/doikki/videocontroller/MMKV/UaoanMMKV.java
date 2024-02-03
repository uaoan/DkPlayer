package xyz.doikki.videocontroller.MMKV;

import android.content.Context;

public class UaoanMMKV {
    public static com.tencent.mmkv.MMKV mm;

    //初始化
    public UaoanMMKV init(Context context){
        com.tencent.mmkv.MMKV.initialize(context);
        mm= com.tencent.mmkv.MMKV.defaultMMKV();
        return this;
    }

    //写入数据到mmkv
    public void setMMKV(String nr,String nr1){
        mm.encode(nr,nr1);
    }
    //写入数据到mmkv
    public void setMMKV(String nr,int nr1){
        mm.encode(nr,nr1);
    }
    //写入数据到mmkv
    public void setMMKV(String nr,boolean nr1){
        mm.encode(nr,nr1);
    }
    //写入数据到mmkv
    public void setMMKV(String nr,float nr1){
        mm.encode(nr,nr1);
    }
    //写入数据到mmkv
    public void setMMKV(String nr,Long nr1){
        mm.encode(nr,nr1);
    }
    //删除mmkv指定数据
    public void setDeleMMKV(String nr){
        mm.removeValueForKey(nr);
    }
    //判断mmkv某条数据是否存在
    public boolean isContainsMMKVKey(String nr){
        return mm.containsKey(nr);
    }
    //获取mmkv的某条数据内容
    public String getMMKVString(String nr){
        return mm.decodeString(nr);
    }
    public int getMMKVInt(String nr){
        return mm.decodeInt(nr);
    }
    public boolean getMMKVBoolean(String nr){
        return mm.decodeBool(nr);
    }
    public Long getMMKVLong(String nr){
        return mm.decodeLong(nr);
    }
    public float getMMKVFloat(String nr){
        return mm.decodeLong(nr);
    }
}
