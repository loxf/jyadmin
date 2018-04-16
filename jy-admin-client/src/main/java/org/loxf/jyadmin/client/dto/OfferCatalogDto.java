package org.loxf.jyadmin.client.dto;

import org.loxf.jyadmin.base.bean.BaseModel;

public class OfferCatalogDto extends BaseModel {

    private String catalogId;

    private String catalogName;

    private String pic;

    private String indexPic;

    private Integer sort;

    private String buyPrivi;

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getIndexPic() {
        return indexPic;
    }

    public void setIndexPic(String indexPic) {
        this.indexPic = indexPic;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getBuyPrivi() {
        return buyPrivi;
    }

    public void setBuyPrivi(String buyPrivi) {
        this.buyPrivi = buyPrivi;
    }
}
