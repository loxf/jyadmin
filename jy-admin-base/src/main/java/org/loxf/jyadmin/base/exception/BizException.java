package org.loxf.jyadmin.base.exception;

public class BizException extends RuntimeException {
    private String code ;
    public BizException(Exception e){
        super(e);
    }
    public BizException(String name, Exception e){
        super(name, e);
    }

    public BizException(String name){
        super(name);
    }
    public BizException(String name, String code){
        super(code+":"+name);
    }

    public String getCode() {
        return code;
    }
}
