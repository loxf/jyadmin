package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.math.BigDecimal;
import java.util.Date;

public class Active extends BaseModel {
    private String activeId;

    private String activeName;

    private BigDecimal price;

    private String activePrivi;

    private String pic;

    private String province;

    private String city;

    private String addr;

    private Date activeStartTime;

    private Date activeEndTime;

    private String htmlId;

    private String metaData;

    private Integer status;

    public String getActiveId() {
        return activeId;
    }

    public void setActiveId(String activeId) {
        this.activeId = activeId == null ? null : activeId.trim();
    }

    public String getActiveName() {
        return activeName;
    }

    public void setActiveName(String activeName) {
        this.activeName = activeName == null ? null : activeName.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getActivePrivi() {
        return activePrivi;
    }

    public void setActivePrivi(String activePrivi) {
        this.activePrivi = activePrivi == null ? null : activePrivi.trim();
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Date getActiveStartTime() {
        return activeStartTime;
    }

    public void setActiveStartTime(Date activeStartTime) {
        this.activeStartTime = activeStartTime;
    }

    public Date getActiveEndTime() {
        return activeEndTime;
    }

    public void setActiveEndTime(Date activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    public String getHtmlId() {
        return htmlId;
    }

    public void setHtmlId(String htmlId) {
        this.htmlId = htmlId == null ? null : htmlId.trim();
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

}