package org.middleware.demo1.acitvemq.config.content;

/**
 * @author summer
 * @date 2021/3/14 19:43
 */
public enum Type {
    TEXT(0,"text"),
    FILE(1,"file");
    private int code;
    private String type;

    Type(int code,String type){
        this.code=code;
        this.type=type;
    }

    public int getCode(){
        return this.code;
    }

    public String getType(){
        return this.type;
    }
}
