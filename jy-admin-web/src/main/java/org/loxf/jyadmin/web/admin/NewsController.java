package org.loxf.jyadmin.web.admin;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.JedisUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.constant.NewsType;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.dto.NewsDto;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.client.service.HtmlInfoService;
import org.loxf.jyadmin.client.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/admin/news")
public class NewsController extends BaseControl<NewsDto>{
    @Autowired
    private NewsService newsService;
    @Autowired
    private HtmlInfoService htmlInfoService;
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private CustService custService;
    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;

    @RequestMapping("/index/{type}")
    public String toNews(Model model, @PathVariable String type){
        setType(model, type);
        return "news/news_list";
    }

    @RequestMapping("/toAddNews/{type}")
    public String toAddNews(Model model, @PathVariable String type){
        setType(model, type);
        return "main/news/addNews";
    }

    @RequestMapping("/toPreView")
    public String toPreView(Model model, String titleId){
        BaseResult<NewsDto> baseResult = newsService.getNewsDetail(titleId);
        if(baseResult.getCode()== BaseConstant.FAILED||baseResult.getData()==null){
            model.addAttribute("errorMsg", "新闻不存在");
            return "main/error";
        } else {
            NewsDto news = baseResult.getData();
            String htmlId = news.getContextId();
            String htmlInfo = htmlInfoService.getHtml(htmlId).getData();
            model.addAttribute("htmlInfo", htmlInfo);
            model.addAttribute("title", news.getTitle());
            model.addAttribute("deployTime", (news.getDeployTime()==null?
                    DateUtils.format(new Date(), "yyyy年MM月dd日 HH:mm:ss"):DateUtils.format(news.getDeployTime(), "yyyy年MM月dd日 HH:mm:ss")));
            model.addAttribute("source", news.getSource());
            return "main/news/preViewNews";
        }
    }

    @RequestMapping("/toEditNews")
    public String toEditNews(Model model, String titleId){
        BaseResult<NewsDto> baseResult = newsService.getNewsDetail(titleId);
        if(baseResult.getCode()== BaseConstant.FAILED||baseResult.getData()==null){
            model.addAttribute("errorMsg", "新闻不存在");
            return "main/error";
        } else {
            NewsDto news = baseResult.getData();
            if(news.getStatus()==1){
                model.addAttribute("errorMsg", "新闻已发布，不能编辑");
                return "main/error";
            } else {
                String htmlId = news.getContextId();
                String htmlInfo = htmlInfoService.getHtml(htmlId).getData();
                model.addAttribute("htmlInfo", htmlInfo);
                model.addAttribute("htmlId", htmlId);
                model.addAttribute("news", news);
                return "main/news/editNews";
            }
        }
    }

    private void setType(Model model, String type){
        if(NewsType.NEWS.name().equalsIgnoreCase(type)){
            model.addAttribute("newsType", NewsType.NEWS.value);
            model.addAttribute("typeDesc", NewsType.NEWS.desc);
        } else {
            model.addAttribute("newsType", NewsType.CLASS.value);
            model.addAttribute("typeDesc", NewsType.CLASS.desc);
        }
    }

    @RequestMapping("/createNews")
    @ResponseBody
    public BaseResult createNews(NewsDto newsDto){
        return newsService.createNews(newsDto);
    }

    @RequestMapping("/updateNews")
    @ResponseBody
    public BaseResult updateNews(NewsDto newsDto){
        return newsService.updateNews(newsDto);
    }

    @RequestMapping("/deleteNews")
    @ResponseBody
    public BaseResult deleteNews(String titleId){
        return newsService.deleteNews(titleId);
    }

    @RequestMapping("/pager")
    @ResponseBody
    public PageResult<NewsDto> getNewsList(NewsDto newsDto){
        initRangeDate(newsDto);
        return newsService.getNewsList(newsDto);
    }

    /**
     * @param titleId
     * @param oper 1:发布 0：取消发布
     * @return
     */
    @RequestMapping("/deployNewsOrNot")
    @ResponseBody
    public BaseResult deployNewsOrNot(String titleId, Integer oper){
        return newsService.deployNewsOrNot(titleId, oper);
    }

    @RequestMapping("/sendWeiXin")
    @ResponseBody
    public BaseResult sendWeiXin(NewsDto newsDto){
        String key = "SEND_NEWS_NOTICE_" + newsDto.getTitleId();
        if(jedisUtil.setnx(key,"true", 300)>0) {
            BaseResult<NewsDto> baseResult = newsService.getNewsDetail(newsDto.getTitleId());
            NewsDto news = baseResult.getData();
            CustDto custDto = new CustDto();
            custDto.setPager(new Pager(1, 100000));
            List<CustDto> custDtoList = custService.pager(custDto).getData();
            if (CollectionUtils.isNotEmpty(custDtoList)) {
                String url = String.format(JYZX_INDEX_URL + BaseConstant.NEWS_DETAIL_URL, news.getTitleId());
                for (CustDto cust : custDtoList) {
                    if (StringUtils.isNotBlank(cust.getOpenid())) {
                        /*SendWeixinMsgUtil.sendNewsNotice(cust.getOpenid(), newsDto.getTitle(),
                                newsDto.getDescription(), newsDto.getDeployTime(), newsDto.getType(), url);*/
                        SendWeixinMsgUtil.sendClassOfferNotice(cust.getOpenid(), news.getTitle(),
                                (news.getType()==1?"学馆新闻":"面授课程"), "静怡雅学文化", url);
                    }
                }
            }
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "五分钟内，不能重复发送。");
        }
    }
}
