package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

public class ActiveCustListDto extends ActiveDto {
    private String custId;

    private String phone;

    private String name;

    private String activeTicketNo;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getActiveTicketNo() {
        return activeTicketNo;
    }

    public void setActiveTicketNo(String activeTicketNo) {
        this.activeTicketNo = activeTicketNo;
    }
}
