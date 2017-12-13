package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.math.BigDecimal;
import java.util.Date;

public class ActiveDto extends BaseModel {
    private String activeId;

    private String activeName;

    private String activeDesc;

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

    private Integer activeStatus;

    private Integer studentsNbr;

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

    public String getActiveDesc() {
        return activeDesc;
    }

    public void setActiveDesc(String activeDesc) {
        this.activeDesc = activeDesc;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public Integer getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus() {
        //设置活动状态 0:进行中 1:即将开始 2:报名中 3:已经结束
        if (this.getActiveStartTime() != null) {
            long diff = this.getActiveStartTime().getTime() - System.currentTimeMillis();
            if (diff >= 48 * 60 * 1000 * 1000) {
                // 报名中 2天以上的
                this.activeStatus = 2;
            } else if (diff >= 0 && diff < 48 * 60 * 1000 * 1000) {
                // 1:即将开始
                this.activeStatus = 1;
            } else if (this.getActiveEndTime() != null && diff < 0 && this.getActiveEndTime().getTime() - System.currentTimeMillis() >= 0) {
                // 进行中
                this.activeStatus = 0;
            } else {
                // 已结束
                this.activeStatus = 3;
            }
        }
    }

    public Integer getStudentsNbr() {
        return studentsNbr;
    }

    public void setStudentsNbr(Integer studentsNbr) {
        this.studentsNbr = studentsNbr;
    }
}