package org.loxf.jyadmin.biz.msg;

import java.util.Map;

public interface ISender {
    boolean send(Map params, String target);
}
