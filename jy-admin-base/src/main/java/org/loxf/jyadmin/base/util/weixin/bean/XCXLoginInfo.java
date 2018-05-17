package org.loxf.jyadmin.base.util.weixin.bean;

import java.io.Serializable;

/**
 * @author hongjia.lhj
 */
public class XCXLoginInfo implements Serializable{
    public String openid;
    public String session_key;
    public String unionid;
    public String custId;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }
}
