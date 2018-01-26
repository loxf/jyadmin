package org.loxf.jyadmin.biz.thread.event;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.constant.BaseConstant;
import org.loxf.jyadmin.base.util.ImageUtil;
import org.loxf.jyadmin.base.util.MatrixToImageWriter;
import org.loxf.jyadmin.biz.util.ConfigUtil;
import org.loxf.jyadmin.biz.util.SendWeixinMsgUtil;
import org.loxf.jyadmin.client.dto.CertifyConfigDto;
import org.loxf.jyadmin.client.dto.CustCertifyDto;
import org.loxf.jyadmin.client.dto.CustDto;
import org.loxf.jyadmin.client.dto.CustScoreDto;
import org.loxf.jyadmin.client.service.CertifyConfigService;
import org.loxf.jyadmin.client.service.CustCertifyService;
import org.loxf.jyadmin.client.service.CustScoreService;
import org.loxf.jyadmin.client.service.CustService;
import org.loxf.jyadmin.dal.dao.CertifyConfigMapper;
import org.loxf.jyadmin.dal.dao.CustScoreMapper;
import org.loxf.jyadmin.dal.po.CertifyConfig;
import org.loxf.jyadmin.dal.po.CustCertify;
import org.loxf.jyadmin.dal.po.CustScore;
import org.loxf.jyadmin.dal.po.Event;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 新增成绩事件
 */
@Component
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
                    if(baseResult.getCode()== BaseConstant.SUCCESS && baseResult.getData()){
                        // 如果有，就生成并发证书，并且通知微信
                        String custId = custScoreDto.getCustId();
                        // 获取客户名称
                        BaseResult<CustDto> custDtoBaseResult = custService.queryCustByCustId(custId);
                        String name = StringUtils.isNotBlank(custDtoBaseResult.getData().getRealName())?
                                custDtoBaseResult.getData().getRealName():custDtoBaseResult.getData().getNickName();
                        // 证书图片生成
                        BaseResult<String> picBaseResult = create(name, custId, custId+certify.getCertifyId(), certify.getPic());
                        // 生成证书数据
                        CustCertifyDto custCertify = new CustCertifyDto();
                        custCertify.setCertifyId(certify.getCertifyId());
                        custCertify.setCertifyName(certify.getCertifyName());
                        custCertify.setCustId(custId);
                        custCertify.setPic(picBaseResult.getData());
                        custCertifyService.addCertify(custCertify);
                        // TODO 发微信
                        // SendWeixinMsgUtil.sendActiveInNotice();
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
            MatrixToImageWriter.createQR(text, "jpg", IMG_SERVER_PATH + qrFilePath, IMG_SERVER_PATH + logoPath);
        }
        String shareFilePath = File.separator + "QR" + File.separator + certifyId + ".jpg";
        List<Map> infoList = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("value", name);
        map1.put("posX", 20);
        map1.put("posY", 100);
        infoList.add(map1);
        ImageUtil.overlapImage(new File(IMG_SERVER_PATH + bgPic), new File(IMG_SERVER_PATH + qrFilePath), new int[]{20, 400},
                infoList, IMG_SERVER_PATH + shareFilePath);
        return new BaseResult<>(shareFilePath);
    }
}
