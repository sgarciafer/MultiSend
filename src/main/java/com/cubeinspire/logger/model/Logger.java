package com.cubeinspire.logger.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Logger {

    private List<Log> log;

    public Logger() {
        log = new ArrayList<>();
    }

    public String info(String text) {
        Log newLog = new Log(Log.Type.INFO, new Date(), text);
        log.add(newLog);
        return newLog.toString();
    }

    public String warning(String text) {
        Log newLog = new Log(Log.Type.WARNING, new Date(), text);
        log.add(new Log(Log.Type.WARNING, new Date(), text));
        return newLog.toString();
    }

    public String error(String text) {
        Log newLog = new Log(Log.Type.ERROR, new Date(), text);
        log.add(new Log(Log.Type.ERROR, new Date(), text));
        return newLog.toString();
    }

    public String debug(String text) {
        Log newLog = new Log(Log.Type.DEBUG, new Date(), text);
        log.add(new Log(Log.Type.DEBUG, new Date(), text));
        return newLog.toString();
    }

    public List<Log> get(){
        return log;
    }

    public List<Log> get(Log.Type type){
        List<Log> filtered = null;
        if( type != null ) {
            for(Log item:log){ if (item.getType().equals(type)) filtered.add(item); }
        }
        return filtered;
    }

    public List<Log> getUntilLevel(int maxLevel) {
        List<Log> filtered = null;
        if( maxLevel > 0 ) {
            for(Log item:log){ if (item.getType().getLevel() <= maxLevel) filtered.add(item); }
        }
        return filtered;
    }
}
