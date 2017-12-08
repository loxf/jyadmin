package org.loxf.jyadmin.client.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 微信一级菜单数组，个数应为1~3个
 */
public class WeixinButton implements Serializable {
    private List<WeixinMenu> button;
}
