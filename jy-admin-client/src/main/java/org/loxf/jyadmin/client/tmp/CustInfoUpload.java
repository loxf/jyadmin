package org.loxf.jyadmin.client.tmp;

import java.io.Serializable;

public class CustInfoUpload implements Serializable {
    private String nickName;
    private String phone;
    private String createdAt;
    private String vipDate;
    private String recommend;
    private String userLevel;
    private String allAmount;
    private String balance;
    private String bp;
    private String adminSet;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getVipDate() {
        return vipDate;
    }

    public void setVipDate(String vipDate) {
        this.vipDate = vipDate;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getAllAmount() {
        return allAmount;
    }

    public void setAllAmount(String allAmount) {
        this.allAmount = allAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBp() {
        return bp;
    }

    public void setBp(String bp) {
        this.bp = bp;
    }

    public String getAdminSet() {
        return adminSet;
    }

    public void setAdminSet(String adminSet) {
        this.adminSet = adminSet;
    }
}
