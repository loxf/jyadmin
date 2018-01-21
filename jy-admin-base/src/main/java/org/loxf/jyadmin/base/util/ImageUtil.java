package org.loxf.jyadmin.base.util;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;

public class ImageUtil {
    private static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
    public static void main(String[] args) {
        String text = "http://www.jingyizaixian.com?recommend=CUSTIJHSFG89235UW4IDGI2H5JF298S5"; // 二维码内容
        String format = "jpg";// 二维码的图片格式
        String filePath = "C:\\Users\\lenovo\\Desktop\\ss\\qr.jpg";
        String logoPath = "C:\\Users\\lenovo\\Desktop\\ss\\logo.png";
        MatrixToImageWriter.createQR(text, format, filePath, logoPath);
        List<Map> infoList = new ArrayList<>();
        Map map1 = new HashMap();
        map1.put("value", "我是Face.");
        map1.put("posX", 180);
        map1.put("posY", 530);
        Map map2 = new HashMap();
        map2.put("value", "我为静怡雅学文化代言.");
        map2.put("posX", 180);
        map2.put("posY", 575);
        infoList.add(map1);
        infoList.add(map2);
        overlapImage(new File("C:\\Users\\lenovo\\Desktop\\ss\\temp1.jpg"), new File(filePath), new int[]{170, 200},
                infoList, "C:\\Users\\lenovo\\Desktop\\ss\\qrF1.jpg");
    }

    /**
     * @param bigFile
     * @param smallFile
     * @param offset posX,posY，小图片放置的绝对位置
     * @param infoList  写入的文字 value,posX,posY
     * @param outFile   输出路径
     * @Description: 小图片贴到大图片形成一张图(合成)
     */
    public static final void overlapImage(File bigFile, File smallFile, int[] offset, List<Map> infoList, String outFile) {
        try {
            BufferedImage big = ImageIO.read(bigFile);
            BufferedImage small = ImageIO.read(smallFile);
            overlapImage(big, small, offset, infoList, outFile);
        } catch (Exception e) {
            logger.error("合并图片失败", e);
            throw new RuntimeException("合并图片失败", e);
        }
    }

    /**
     * @param bigFile
     * @param smallFile
     * @param infoList  写入的文字 value,posX,posY
     * @param outFile   输出路径
     * @Description: 小图片贴到大图片形成一张图(合成)
     */
    public static final void overlapImage(File bigFile, File smallFile, List<Map> infoList, String outFile) {
        try {
            BufferedImage big = ImageIO.read(bigFile);
            BufferedImage small = ImageIO.read(smallFile);
            overlapImage(big, small, null, infoList, outFile);
        } catch (Exception e) {
            logger.error("合并图片失败", e);
            throw new RuntimeException("合并图片失败", e);
        }
    }

    /**
     * @param big
     * @param small
     * @param infoList 写入的文字 value,posX,posY
     * @param outFile  输出路径
     * @Description: 小图片贴到大图片形成一张图(合成)
     */
    public static final void overlapImage(BufferedImage big, BufferedImage small, int[] offset, List<Map> infoList, String outFile) {
        try {
            Graphics2D g = big.createGraphics();
            /*int x = (big.getWidth() - small.getWidth()) / 2;
            int y = (big.getHeight() - small.getHeight()) / 2;*/
            int xOffset = offset!=null&&offset.length>0?offset[0]:0;
            int yOffset = offset!=null&&offset.length>1?offset[1]:0;
            g.drawImage(small, xOffset, yOffset, small.getWidth(), small.getHeight(), null);
            g.setColor(Color.black);
            g.setFont(new Font("微软雅黑", Font.PLAIN, 30));
            if (CollectionUtils.isNotEmpty(infoList)) {
                for (Map infoMap : infoList) {
                    g.drawString((String) infoMap.get("value"), (int) infoMap.get("posX"), (int) infoMap.get("posY"));//绘制文字
                }
            }
            g.dispose();
            ImageIO.write(big, outFile.split("\\.")[1], new File(outFile));
        } catch (Exception e) {
            logger.error("合并图片失败", e);
            throw new RuntimeException(e);
        }
    }

    /*
         * 图片缩放,w，h为缩放的目标宽度和高度
         * src为源文件目录，dest为缩放后保存目录
         */
    public static BufferedImage zoomImage(BufferedImage image, int w, int h) {
        Image imgaeDest = image.getScaledInstance(w, h, image.SCALE_SMOOTH);
        double wr = w * 1.0 / image.getWidth(); //获取缩放比例
        double hr = h * 1.0 / image.getHeight();

        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        imgaeDest = ato.filter(image, null);
        return (BufferedImage) imgaeDest;
    }

    /*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     */
    public static void zoomImage(String src, String dest, int w, int h) {
        double wr = 0, hr = 0;
        File srcFile = new File(src);
        File destFile = new File(dest);

        try {
            BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
            Image Itemp = bufImg.getScaledInstance(w, h, bufImg.SCALE_SMOOTH);//设置缩放目标图片模板

            wr = w * 1.0 / bufImg.getWidth(); //获取缩放比例
            hr = h * 1.0 / bufImg.getHeight();

            AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
            Itemp = ato.filter(bufImg, null);
            ImageIO.write((BufferedImage) Itemp, dest.substring(dest.lastIndexOf(".") + 1), destFile); //写入缩减后的图片
        } catch (Exception ex) {
            logger.error("缩放图片失败", ex);
            throw new RuntimeException(ex);
        }
    }
}
