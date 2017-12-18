package org.loxf.jyadmin.biz;

import com.alibaba.fastjson.JSONObject;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.client.service.NoticeService;
import org.loxf.jyadmin.dal.dao.NoticeMapper;
import org.loxf.jyadmin.dal.po.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("noticeService")
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public BaseResult insert(String type, String obj, Map metaData) {
        if(metaData==null){
            return new BaseResult(BaseConstant.FAILED, "参数为空");
        }
        Notice notice = new Notice();
        notice.setNoticeType(type);
        notice.setNoticeObj(obj);
        notice.setMetaData(JSONObject.toJSONString(metaData));
        noticeMapper.insert(notice);
        return new BaseResult();
    }
}
