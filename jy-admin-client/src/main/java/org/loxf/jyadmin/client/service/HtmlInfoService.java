package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;

public interface HtmlInfoService {
    public BaseResult<String> getHtml(String htmlId);
    public BaseResult<String> insertHtml(String htmlInfo);
    public BaseResult<String> updateHtml(String htmlId, String htmlInfo);
}
