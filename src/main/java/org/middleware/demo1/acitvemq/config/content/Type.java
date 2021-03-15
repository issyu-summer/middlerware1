package org.middleware.demo1.acitvemq.config.content;

/**
 * @author summer
 * @date 2021/3/14 19:43
 */
public enum Type {
    TEXT(0,"text"),
    FILE(1,"file"),
    NONE(2,"none");//意思是没有传入类型

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

    public static Type getEnum(Integer id){
        for(Type t:Type.values()){
            if(id.equals(t.code)){
                return t;
            }
        }
        return Type.NONE;
    }
}
