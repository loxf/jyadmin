package org.loxf.jyadmin.dal.po;

import java.util.Date;

public class CertifyConfig {
    private Long id;

    private String certifyId;

    private String certifyName;

    private String priviArr;

    private Date createdAt;

    private Date updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPriviArr() {
        return priviArr;
    }

    public void setPriviArr(String priviArr) {
        this.priviArr = priviArr == null ? null : priviArr.trim();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}