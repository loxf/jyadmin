package org.loxf.jyadmin.base.exception;

/**
 * Created by luohj on 2017/8/30.
 */
public class JyException extends RuntimeException {
    private String code ;
    public JyException(Exception e){
        super(e);
    }
    public JyException(String name, Exception e){
        super(name, e);
    }

    public JyException(String name){
        super(name);
    }
    public JyException(String name, String code){
        super(code+":"+name);
    }

    public String getCode() {
        return code;
    }
}
