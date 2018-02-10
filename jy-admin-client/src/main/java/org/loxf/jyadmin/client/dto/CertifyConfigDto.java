package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

public class CertifyConfigDto extends BaseModel {
    private String certifyId;

    private String certifyName;

    private String pic;

    private String desc;

    private String priviArr;

    public String getCertifyId() {
        return certifyId;
    }

    public void setCertifyId(String certifyId) {
        this.certifyId = certifyId == null ? null : certifyId.trim();
    }

    public String getCertifyName() {
        return certifyName;
    }

    public void setCertifyName(String certifyName) {
        this.certifyName = certifyName == null ? null : certifyName.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPriviArr() {
        return priviArr;
    }

    public void setPriviArr(String priviArr) {
        this.priviArr = priviArr == null ? null : priviArr.trim();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}