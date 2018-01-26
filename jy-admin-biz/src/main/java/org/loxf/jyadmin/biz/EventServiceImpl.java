package org.loxf.jyadmin.biz;

import org.apache.commons.collections.CollectionUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.base.bean.Pager;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.SpringApplicationContextUtil;
import org.loxf.jyadmin.biz.thread.event.IEvent;
import org.loxf.jyadmin.client.dto.EventDto;
import org.loxf.jyadmin.client.service.EventService;
import org.loxf.jyadmin.dal.dao.EventMapper;
import org.loxf.jyadmin.dal.po.Event;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("eventService")
public class EventServiceImpl implements EventService {
    public static int WAIT = 1;
    public static int SUCC = 3;
    public static int ERR = -3;

    @Autowired
    private EventMapper eventMapper;
    @Override
    @Transactional
    public BaseResult addEvent(String code, String key, String metaData) {
        Event event = new Event();
        event.setEventCode(code);
        event.setEventKey(key);
        if(eventMapper.count(event)>0){
            return new BaseResult(BaseConstant.FAILED, "事件已经存在", event);
        }
        event.setMetaData(metaData);
        event.setStatus(WAIT);
        if(eventMapper.insert(event)>0) {
            return new BaseResult(event);
        } else {
            return new BaseResult(BaseConstant.FAILED, "新增事件失败");
        }
    }

    @Override
    @Transactional
    public BaseResult refreshEvent(String code, String key, Integer status, String remark) {
        Event event = new Event();
        event.setEventCode(code);
        event.setEventKey(key);
        event.setStatus(status);
        event.setRemark(remark);
        if(eventMapper.update(event)>0) {
            return new BaseResult();
        } else {
            return new BaseResult(BaseConstant.FAILED, "更新失败");
        }
    }

    @Override
    public PageResult<EventDto> pager(EventDto eventDto, Integer page, Integer size) {
        Event event = new Event();
        if(eventDto!=null){
            BeanUtils.copyProperties(eventDto, event);
        }
        event.setPager(new Pager(page, size));
        int total = eventMapper.count(event);
        List<EventDto> dtos = new ArrayList<>();
        if(total>0) {
            List<Event> eventList = eventMapper.list(event);
            if(CollectionUtils.isNotEmpty(eventList)) {
                for (Event po : eventList) {
                    EventDto tmp = new EventDto();
                    BeanUtils.copyProperties(po, tmp);
                    dtos.add(tmp);
                }
            }
        }
        int totalPage = total/eventDto.getPager().getSize() + (total%eventDto.getPager().getSize()==0?0:1);
        return new PageResult<EventDto>(totalPage, eventDto.getPager().getPage(), total, dtos);
    }

    @Override
    @Transactional
    public void runEvent(EventDto eventDto) {
        Event event = new Event();
        BeanUtils.copyProperties(eventDto, event);
        try{
            // 创建事件处理器
            IEvent iEvent = createEvent(eventDto);
            BaseResult baseResult = iEvent.run(event);
            if(baseResult.getCode()==BaseConstant.SUCCESS) {
                update(event, SUCC, "处理成功");
            } else {
                update(event, ERR, baseResult.getMsg());
            }
        } catch (Exception e){
            update(event, ERR, e.getMessage());
        }
    }

    private IEvent createEvent(EventDto event){
        String serviceName = event.getEventCode() + "Event";
        return SpringApplicationContextUtil.getBean(serviceName, IEvent.class);
    }

    private int update(Event event, int status, String remark){
        event.setRemark(remark);
        event.setStatus(status);
        return eventMapper.update(event);
    }

}
