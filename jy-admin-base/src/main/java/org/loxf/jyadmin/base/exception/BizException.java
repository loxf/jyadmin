package org.loxf.jyadmin.base.exception;

public class BizException extends RuntimeException {
    private String code ;
    private String name ;
    public BizException(Exception e){
        super(e);
    }
    public BizException(String name, Exception e){
        super(name, e);
    }

    public BizException(String name){
        super(name);
        this.name = name;
    }
    public BizException(String name, String code){
        super(code+":"+name);
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }


    public String getName() {
        return name;
    }
}
