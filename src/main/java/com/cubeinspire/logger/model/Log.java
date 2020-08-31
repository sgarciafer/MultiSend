package com.cubeinspire.logger.model;

import java.util.*;

public class Log {

    private String text;
    private Date date;
    private Type type;

    public enum Type {
        INFO("info", 1),
        WARNING("warning", 2),
        ERROR("error", 3),
        DEBUG("debug", 4);

        private String name;
        private int level;
        Type(String text, int level) {
            name = text;
            this.level = level;
        }
        @Override
        public String toString() {
            return name;
        }
        public int getLevel() { return level; }
    }

    public Log(Type typed, Date dated, String texted) {
        date = dated;
        text = texted;
        type = typed;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        String result = "[ "+type.toString().toUpperCase()+" ] [ "+date.toString()+" ]: "+text;
        return result;
    }
}
