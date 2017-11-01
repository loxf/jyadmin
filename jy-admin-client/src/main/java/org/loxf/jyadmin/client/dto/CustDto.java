package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

public class CustDto extends BaseModel {

    private String custId;

    private String nickName;

    private String realName;

    private String province;

    private String city;

    private Integer firstLvNbr;

    private Integer secondLvNbr;

    private String userLevel;

    private String phone;

    private String email;

    private Integer isChinese;

    private String recomend;

    private Integer isAgent;

    private String address;

    private String weixinId;

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId == null ? null : custId.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
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

    public Integer getFirstLvNbr() {
        return firstLvNbr;
    }

    public void setFirstLvNbr(Integer firstLvNbr) {
        this.firstLvNbr = firstLvNbr;
    }

    public Integer getSecondLvNbr() {
        return secondLvNbr;
    }

    public void setSecondLvNbr(Integer secondLvNbr) {
        this.secondLvNbr = secondLvNbr;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel == null ? null : userLevel.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Integer getIsChinese() {
        return isChinese;
    }

    public void setIsChinese(Integer isChinese) {
        this.isChinese = isChinese;
    }

    public String getRecomend() {
        return recomend;
    }

    public void setRecomend(String recomend) {
        this.recomend = recomend == null ? null : recomend.trim();
    }

    public Integer getIsAgent() {
        return isAgent;
    }

    public void setIsAgent(Integer isAgent) {
        this.isAgent = isAgent;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getWeixinId() {
        return weixinId;
    }

    public void setWeixinId(String weixinId) {
        this.weixinId = weixinId == null ? null : weixinId.trim();
    }
}
