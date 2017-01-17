package cn.wycode.wengine.utils;


public class Timer {

    private long startCount;
    private long lastRecord;

    public Timer() {
        startCount = System.currentTimeMillis();
        lastRecord = startCount;
    }

    public boolean elapse(int ms) {
        if (System.currentTimeMillis() - lastRecord > ms) {
            lastRecord = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public long getElapse() {
        long dt = System.currentTimeMillis() - lastRecord;
        lastRecord = System.currentTimeMillis();
        return dt;
    }

    public long getElapseNotReset(){
        return System.currentTimeMillis() - startCount;
    }

    public void reset(){
        startCount = System.currentTimeMillis();
        lastRecord = startCount;
    }
}
