package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

import java.math.BigDecimal;
import java.util.List;

public class OfferDto extends BaseModel {
    private String offerId;

    private String offerName;

    private String catalogId;

    private String catalogName;

    private String offerType;

    private BigDecimal saleMoney;

    private String offerPic;

    private String mainMedia;

    private String buyPrivi;

    private String metaData;

    private String htmlId;

    private Integer status;

    private Integer playTime;

    private List<OfferDto> relOffers;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId == null ? null : offerId.trim();
    }

    public String getOfferName() {
        return offerName;
    }

    public void setOfferName(String offerName) {
        this.offerName = offerName == null ? null : offerName.trim();
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId == null ? null : catalogId.trim();
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType == null ? null : offerType.trim();
    }

    public BigDecimal getSaleMoney() {
        return saleMoney;
    }

    public void setSaleMoney(BigDecimal saleMoney) {
        this.saleMoney = saleMoney;
    }

    public String getOfferPic() {
        return offerPic;
    }

    public void setOfferPic(String offerPic) {
        this.offerPic = offerPic;
    }

    public String getMainMedia() {
        return mainMedia;
    }

    public void setMainMedia(String mainMedia) {
        this.mainMedia = mainMedia == null ? null : mainMedia.trim();
    }

    public String getBuyPrivi() {
        return buyPrivi;
    }

    public void setBuyPrivi(String buyPrivi) {
        this.buyPrivi = buyPrivi == null ? null : buyPrivi.trim();
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData == null ? null : metaData.trim();
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

    public Integer getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Integer playTime) {
        this.playTime = playTime;
    }

    public List<OfferDto> getRelOffers() {
        return relOffers;
    }

    public void setRelOffers(List<OfferDto> relOffers) {
        this.relOffers = relOffers;
    }
}
