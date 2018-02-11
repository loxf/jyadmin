package org.loxf.jyadmin.biz.thread.event;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.DateUtils;
import org.loxf.jyadmin.base.util.ImageUtil;
import org.loxf.jyadmin.base.util.MatrixToImageWriter;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.CustCertifyDto;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.dto.CustScoreDto;
import org.loxf.jyadmin.client.service.CustCertifyService;
import org.loxf.jyadmin.client.service.CustScoreService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.CertifyConfigMapper;
import org.loxf.jyadmin.dal.po.CertifyConfig;
import org.loxf.jyadmin.dal.po.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * 新增成绩事件
 */
@Component("AddScoreEvent")
public class AddScoreEvent implements IEvent{
    @Autowired
    private CustCertifyService custCertifyService;
    @Autowired
    private CustScoreService custScoreService;
    @Autowired
    private CertifyConfigMapper certifyConfigMapper;
    @Autowired
    private CustService custService;
    @Value("#{configProperties['IMAGE.SERVER.PATH']}")
    private String IMG_SERVER_PATH;
    @Value("#{configProperties['JYZX.INDEX.URL']}")
    private String JYZX_INDEX_URL;
    @Override
    public BaseResult run(Event event) {
        // 新增成绩，先获取当前成绩内容
        BaseResult<CustScoreDto> custScoreDtoBaseResult = custScoreService.selectByScoreId(event.getEventKey());
        if(custScoreDtoBaseResult.getCode()==BaseConstant.FAILED){
            return custScoreDtoBaseResult;
        }
        CustScoreDto custScoreDto = custScoreDtoBaseResult.getData();
        Integer pass = custScoreDto.getIsPass();
        if(pass==1) {
            // 检查当前课程对应是否有配置证书？
            String offerId = custScoreDto.getOfferId();
            // 如有，获取对应证书的所有的课程
            List<CertifyConfig> certifyList = certifyConfigMapper.selectCertifyByOfferId(offerId);
            if(CollectionUtils.isNotEmpty(certifyList)){
                for(CertifyConfig certify : certifyList){
                    String[] offers = certify.getPriviArr().split(",");
                    // 检查一下是否有获取证书的资格
                    BaseResult<Boolean> baseResult = custScoreService.allPass(Arrays.asList(offers));
                    if(baseResult.getCode()== BaseConstant.SUCCESS && baseResult.getData()) {
                        // 如果有，检查是否已经获得过此证书
                        String custId = custScoreDto.getCustId();
                        BaseResult<Boolean> existsCertify = custCertifyService.existCertify(custId, certify.getCertifyId());
                        if (existsCertify.getCode() == BaseConstant.SUCCESS && !existsCertify.getData()) {
                            // 没有，就生成并发证书，并且通知微信
                            // 获取客户名称
                            BaseResult<CustDto> custDtoBaseResult = custService.queryCustByCustId(custId);
                            CustDto custDto = custDtoBaseResult.getData();
                            String name = StringUtils.isNotBlank(custDto.getRealName()) ? custDto.getRealName() : custDto.getNickName();
                            // 证书图片生成
                            BaseResult<String> picBaseResult = create(name, custId, custId + certify.getCertifyId(), certify.getPic());
                            // 生成证书数据
                            CustCertifyDto custCertify = new CustCertifyDto();
                            custCertify.setCertifyId(certify.getCertifyId());
                            custCertify.setCertifyName(certify.getCertifyName());
                            custCertify.setCustId(custId);
                            custCertify.setPic(picBaseResult.getData());
                            custCertifyService.addCertify(custCertify);
                            // 发微信
                            SendWeixinMsgUtil.sendGetCertifyNotice(custDto.getOpenid(), custDto.getNickName(),
                                    certify.getCertifyName(), DateUtils.formatHms(new Date()), certify.getDesc(), JYZX_INDEX_URL);
                        }
                    }
                }
            }
        }
        return new BaseResult();
    }

    public BaseResult<String> create(String name, String custId, String certifyId, String bgPic){
        String path = IMG_SERVER_PATH ;
        if (!new File(path + File.separator + "QR").exists()) {
            new File(path + File.separator + "QR").mkdir();
        }
        String qrFilePath = File.separator + "QR" + File.separator + "QR" + custId + ".jpg";
        if(!new File(IMG_SERVER_PATH + qrFilePath).exists()) {
            String text = String.format(JYZX_INDEX_URL + BaseConstant.JYZX_INDEX_RECOMMEND_URL, custId); // 二维码内容
            String logoPath = ConfigUtil.getConfig(BaseConstant.CONFIG_TYPE_COM, "COMPANY_LOGO",
                    "/CONFIG/0a94a6ec6695491c805ca0cd22741077.jpeg").getConfigValue();
            MatrixToImageWriter.createQR(text, null, IMG_SERVER_PATH + qrFilePath, IMG_SERVER_PATH + logoPath);
        }
        String shareFilePath = File.separator + "QR" + File.separator + certifyId + ".jpg";
        List<Map> infoList = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("value", name);
        map1.put("posX", 150);
        map1.put("posY", 180);
        map1.put("size", 25);
        map1.put("color", new Color(139, 25, 91));
        infoList.add(map1);
        Map map2 = new HashMap();
        map2.put("value", DateUtils.format(new Date()));
        map2.put("posX", 700);
        map2.put("posY", 510);
        map2.put("size", 20);
        map2.put("color", new Color(139, 25, 91));
        infoList.add(map2);
        ImageUtil.overlapImage(new File(IMG_SERVER_PATH + bgPic), new File(IMG_SERVER_PATH + qrFilePath), new int[]{750, 70, 100, 100},
                infoList, IMG_SERVER_PATH + shareFilePath);
        return new BaseResult<>(shareFilePath);
    }
}
