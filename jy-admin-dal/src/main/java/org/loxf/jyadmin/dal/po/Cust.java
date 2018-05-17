package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.util.Date;

public class Cust extends BaseModel {

    private String custId;

    private String nickName;

    private String realName;

    private Integer firstLvNbr;

    private Integer secondLvNbr;

    private String userLevel;

    private String phone;

    private String email;

    private Integer isChinese;

    private String recommend;

    private String recommendLink;

    private Integer isAgent;

    private Integer sex;

    private String country;

    private String province;

    private String city;

    private String address;

    private String headImgUrl;

    private String privilege;

    private String metaData;

    private String xcxOpenid;

    private String openid;

    private String unionid;

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

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getRecommendLink() {
        return recommendLink;
    }

    public void setRecommendLink(String recommendLink) {
        this.recommendLink = recommendLink;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getPrivilege() {
        return privilege;
    }

    public void setPrivilege(String privilege) {
        this.privilege = privilege;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getXcxOpenid() {
        return xcxOpenid;
    }

    public void setXcxOpenid(String xcxOpenid) {
        this.xcxOpenid = xcxOpenid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}