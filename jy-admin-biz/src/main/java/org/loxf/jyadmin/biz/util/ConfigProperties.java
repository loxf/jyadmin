package org.loxf.jyadmin.biz.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigProperties {
    @Value("#{configProperties['mq.topic']}")
    private String mqTopic;
    @Value("#{configProperties['IMAGE.SERVER.PATH']}")
    private String IMG_SERVER_PATH;

    public String getMqTopic() {
        return mqTopic;
    }

    public String getIMG_SERVER_PATH() {
        return IMG_SERVER_PATH;
    }

}
