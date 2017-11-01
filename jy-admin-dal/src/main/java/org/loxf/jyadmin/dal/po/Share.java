package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.util.Date;

public class Share extends BaseModel {
    private String custId;

    private String type;

    private String shareObj;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getShareObj() {
        return shareObj;
    }

    public void setShareObj(String shareObj) {
        this.shareObj = shareObj == null ? null : shareObj.trim();
    }

}