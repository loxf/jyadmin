package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.util.IdGenerator;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.service.HtmlInfoService;
import org.loxf.jyadmin.dal.dao.HtmlInfoMapper;
import org.loxf.jyadmin.dal.po.HtmlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("htmlInfoService")
public class HtmlInfoServiceImpl implements HtmlInfoService {
    private static String prefix = "HTML";
    @Autowired
    private HtmlInfoMapper htmlInfoMapper;

    @Override
    public BaseResult<String> getHtml(String htmlId) {
        HtmlInfo info = htmlInfoMapper.selectByHtmlId(htmlId);
        if(info!=null)
            return new BaseResult<>(info.getHtmlInfo());
        else
            return new BaseResult<>();
    }

    @Override
    @Transactional
    public BaseResult<String> insertHtml(String htmlInfo) {
        String htmlId = IdGenerator.generate(prefix);
        if(htmlInfoMapper.insert(htmlId, htmlInfo)>0) {
            return new BaseResult<>(htmlId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "插入HTML文本失败");
        }
    }

    @Override
    @Transactional
    public BaseResult<String> updateHtml(String htmlId, String htmlInfo) {
        if(htmlInfoMapper.update(htmlId, htmlInfo)>0) {
            return new BaseResult<>(htmlId);
        } else {
            return new BaseResult<>(BaseConstant.FAILED, "更新HTML文本失败");
        }
    }
}
