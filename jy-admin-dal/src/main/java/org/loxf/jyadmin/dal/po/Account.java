package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.math.BigDecimal;
import java.util.Date;

public class Account extends BaseModel {
    private String custId;

    private BigDecimal balance;

    private BigDecimal bp;

    private String password;

    private Integer isLock;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getBp() {
        return bp;
    }

    public void setBp(BigDecimal bp) {
        this.bp = bp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

}