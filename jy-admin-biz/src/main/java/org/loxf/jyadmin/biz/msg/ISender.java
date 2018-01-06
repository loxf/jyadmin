package org.loxf.jyadmin.biz.msg;

import org.loxf.jyadmin.base.bean.BaseResult;

import java.util.Map;

public interface ISender {
    BaseResult send(Map params, String target);
}
