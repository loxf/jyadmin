package org.loxf.jyadmin.dal.po;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.util.Date;

public class Config extends BaseModel {
    private String catalog;

    private String configCode;

    private String configName;

    private String configValue;
    /**
     * TEXT：文本 NUMBER：数字，PIC：图片，MP4：视频，URL：链接，HTML：富文本ID
     */
    private String type;

    /**
     * 1:生效 0:失效
     */
    private Integer status;

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode == null ? null : configCode.trim();
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName == null ? null : configName.trim();
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue == null ? null : configValue.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}