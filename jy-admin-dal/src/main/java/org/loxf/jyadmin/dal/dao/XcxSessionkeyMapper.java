package org.loxf.jyadmin.dal.dao;

import org.loxf.jyadmin.dal.po.XcxSessionkey;

public interface XcxSessionkeyMapper {
    int insert(XcxSessionkey record);

    XcxSessionkey selectByCustId(String id);

    int updateByCustId(XcxSessionkey record);
}