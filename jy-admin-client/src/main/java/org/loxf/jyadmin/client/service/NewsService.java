package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.NewsDto;

public interface NewsService {
    BaseResult<NewsDto> getNewsDetail(String titleId);

    /**
     * 获取新闻列表以及当前客户的阅读标志
     * @param newsDto
     * @param custId
     * @return
     */
    PageResult<NewsDto> getNewsListByCustId(NewsDto newsDto, String custId);
    /**
     * 获取新闻列表
     * @param newsDto
     * @return
     */
    PageResult<NewsDto> getNewsList(NewsDto newsDto);
    BaseResult<String> createNews(NewsDto newsDto);
    BaseResult<String> updateNews(NewsDto newsDto);
    BaseResult<String> deleteNews(String titleId);
    BaseResult<String> deployNewsOrNot(String titleId, Integer oper);
    public BaseResult<String> addViewRecord(String titleId, String custId);
}
