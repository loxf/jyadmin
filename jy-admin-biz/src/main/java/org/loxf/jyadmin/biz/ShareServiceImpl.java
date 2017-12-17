package org.loxf.jyadmin.biz;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.ImageUtil;
import org.loxf.jyadmin.base.util.MatrixToImageWriter;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.client.dto.ShareDto;
import org.loxf.jyadmin.client.service.AccountService;
import org.loxf.jyadmin.client.service.CustBpDetailService;
import org.loxf.jyadmin.client.service.ShareService;
import org.loxf.jyadmin.dal.dao.ShareMapper;
import org.loxf.jyadmin.dal.po.Config;
import org.loxf.jyadmin.dal.po.Share;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("shareService")
public class ShareServiceImpl implements ShareService {
    @Autowired
    private ShareMapper shareMapper;
    @Autowired
    private AccountService accountService;
    @Value("#{configProperties['IMAGE.SERVER.PATH']}")
    private String IMG_SERVER_PATH;

    @Override
    public BaseResult<String> createQR(String nickName, String custId){
         String path = IMG_SERVER_PATH ;
        if (!new File(path + File.separator + "QR").exists()) {
            new File(path + File.separator + "QR").mkdir();
        }
        String qrFilePath = File.separator + "QR" + File.separator + "QR" + custId + ".jpg";
        if(!new File(IMG_SERVER_PATH + qrFilePath).exists()) {
            String text = String.format(BaseConstant.JYZX_INDEX_URL, custId); // 二维码内容
            String logoPath = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_COM, "COMPANY_LOGO",
                    "/CONFIG/0a94a6ec6695491c805ca0cd22741077.jpeg").getConfigValue();
            MatrixToImageWriter.createQR(text, "jpg", IMG_SERVER_PATH + qrFilePath, IMG_SERVER_PATH + logoPath);
        }
        String shareFilePath = File.separator + "QR" + File.separator + "SHARE" + custId + ".jpg";
        List<Map> infoList = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("value", "我是" + nickName);
        map1.put("posX", 190);
        map1.put("posY", 940);
        Map map2 = new HashMap();
        map2.put("value", "我为静怡雅学文化代言.");
        map2.put("posX", 190);
        map2.put("posY", 980);
        infoList.add(map1);
        infoList.add(map2);
        String bgFilePath = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_COM, "SHARE_BG_PIC").getConfigValue();
        ImageUtil.overlapImage(new File(IMG_SERVER_PATH + bgFilePath), new File(IMG_SERVER_PATH + qrFilePath), new int[]{0, -170},
                infoList, IMG_SERVER_PATH + shareFilePath);
        return new BaseResult<>(shareFilePath);
    }

    /**
     * @param custId
     * @param shareObj
     * @param type 分享类型：VIDEO/ACTIVE/RECOMMEND/PAGE
     * @return
     */
    @Override
    @Transactional
    public BaseResult shareInfo(String custId, String detailName, String shareObj, String type) {
        boolean addBp = false;
        if(shareMapper.existsByObjAndType(custId, type, shareObj)==0){
            addBp = true;
        }
        if(addBp){
            String bp;
            if(type.equals("VIDEO")) {
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "BP_SHARE_VIDEO", "10").getConfigValue();
            } else if(type.equals("ACTIVE")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "BP_SHARE_ACTIVE", "10").getConfigValue();
            } else if(type.equals("RECOMMEND")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "BP_SHARE_REGISTER", "30").getConfigValue();
            } else if(type.equals("PAGE")){
                bp = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_BP, "BP_SHARE_OTHER", "5").getConfigValue();
            } else {
                bp = "1";
            }
            // 新增积分
            BaseResult baseResult = accountService.increase(custId, null, new BigDecimal(bp), null, "分享" + detailName, null);
            if(baseResult.getCode()==BaseConstant.FAILED){
                return baseResult;
            }
        }
        // 新增分享记录
        Share share = new Share();
        share.setCustId(custId);
        share.setShareObj(shareObj);
        share.setType(type);
        shareMapper.insert(share);
        return new BaseResult();
    }
}
