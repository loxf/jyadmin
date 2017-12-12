package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.math.BigDecimal;
import java.util.Date;

public class AccountDetail extends BaseModel {
    private String custId;

    private String custName;

    private String orderId;

    private String detailName;

    private Integer type;

    private BigDecimal balance;

    private BigDecimal changeBalance;

    private String sourceCustId;

    private String sourceCustName;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId == null ? null : orderId.trim();
    }

    public String getDetailName() {
        return detailName;
    }

    public void setDetailName(String detailName) {
        this.detailName = detailName == null ? null : detailName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getChangeBalance() {
        return changeBalance;
    }

    public void setChangeBalance(BigDecimal changeBalance) {
        this.changeBalance = changeBalance;
    }

    public String getSourceCustId() {
        return sourceCustId;
    }

    public void setSourceCustId(String sourceCustId) {
        this.sourceCustId = sourceCustId;
    }

    public String getSourceCustName() {
        return sourceCustName;
    }

    public void setSourceCustName(String sourceCustName) {
        this.sourceCustName = sourceCustName;
    }
}