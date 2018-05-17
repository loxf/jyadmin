package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.NewsDto;
import org.loxf.jyadmin.client.dto.NewsDto;
import org.loxf.jyadmin.client.service.NewsService;
import org.loxf.jyadmin.dal.dao.NewsMapper;
import org.loxf.jyadmin.dal.dao.NewsViewMapper;
import org.loxf.jyadmin.dal.po.News;
import org.loxf.jyadmin.dal.po.News;
import org.loxf.jyadmin.dal.po.NewsView;
import org.loxf.jyadmin.dal.po.WatchRecord;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("newsService")
public class NewsServiceImpl implements NewsService {
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private NewsViewMapper newsViewMapper;

    @Override
    public BaseResult<NewsDto> getNewsDetail(String titleId) {
        News news = newsMapper.selectByTitleId(titleId);
        if(news==null){
            return new BaseResult<>(BaseConstant.FAILED, "新闻不存在");
        }
        NewsDto dto = new NewsDto();
        BeanUtils.copyProperties(news, dto);
        return new BaseResult<>(dto);
    }

    @Override
    public PageResult<NewsDto> getNewsList(NewsDto newsDto) {
        return getNewsListByCustId(newsDto, null);
    }

    /**
     * 获取新闻列表以及当前客户的阅读标志
     * @param newsDto
     * @param custId
     * @return
     */
    @Override
    public PageResult<NewsDto> getNewsListByCustId(NewsDto newsDto, String custId) {
        if(newsDto==null){
            return new PageResult<>(BaseConstant.FAILED, "入参不能为空");
        }
        News news = new News();
        BeanUtils.copyProperties(newsDto, news);
        int total = newsMapper.count(news);
        List<String> titles = new ArrayList<>();
        List<NewsDto> dtos = new ArrayList<>();
        if(total>0) {
            if(StringUtils.isNotBlank(custId)){
                news.setCurrentCustId(custId);
            }
            List<News> offerList = newsMapper.list(news);
            if(CollectionUtils.isNotEmpty(offerList)) {
                for (News po : offerList) {
                    NewsDto tmp = new NewsDto();
                    BeanUtils.copyProperties(po, tmp);
                    dtos.add(tmp);
                    titles.add(po.getTitleId());
                }
            }
        }
        int totalPage = total/newsDto.getPager().getSize() + (total%newsDto.getPager().getSize()==0?0:1);
        return new PageResult(totalPage, newsDto.getPager().getPage(), total, dtos);
    }

    @Override
    @Transactional
    public BaseResult<String> createNews(NewsDto newsDto) {
        News news = new News();
        String titleId = IdGenerator.generate("NEWS");
        BeanUtils.copyProperties(newsDto, news);
        news.setTitleId(titleId);
        if(newsMapper.insert(news)>0) {
            return new BaseResult<>(titleId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "新增失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> updateNews(NewsDto newsDto) {
        News news = new News();
        BeanUtils.copyProperties(newsDto, news);
        if(newsMapper.updateByTitleId(news)>0) {
            return new BaseResult<>(newsDto.getTitleId());
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "更新失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> deleteNews(String titleId) {
        if(newsMapper.deleteByTitleId(titleId)>0) {
            return new BaseResult(titleId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "删除失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> deployNewsOrNot(String titleId, Integer oper) {
        if(newsMapper.deployNewsOrNot(titleId, oper)>0) {
            return new BaseResult(titleId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, (oper==1?"发布":"取消发布") + "失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> addViewRecord(String titleId, String custId) {
        NewsView newsView = new NewsView();
        newsView.setCustId(custId);
        newsView.setTitleId(titleId);
        if(newsViewMapper.exists(newsView)>0){
            newsViewMapper.updateReadTimes(newsView);
        } else {
            try {
                newsViewMapper.insert(newsView);
            } catch (Exception e){
                // 联合唯一主键冲突
                newsViewMapper.updateReadTimes(newsView);
            }
        }
        return new BaseResult<>(titleId);
    }
}
