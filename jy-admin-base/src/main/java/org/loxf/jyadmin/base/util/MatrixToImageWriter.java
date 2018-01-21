package org.loxf.jyadmin.base.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

public class MatrixToImageWriter {
    private static Logger logger = LoggerFactory.getLogger(MatrixToImageWriter.class);
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static int width = 300; // 二维码图片宽度
    private static int height = 300; // 二维码图片高度

    private MatrixToImageWriter() { }

    public static void main(String[] args) throws Exception {
        String text = "http://www.jingyizaixian.com?recommend=CUSTIJHSFG89235UW4IDGI2H5JF298S5"; // 二维码内容
        String format = "jpg";// 二维码的图片格式
        String filePath = "C:\\Users\\lenovo\\Desktop\\ss\\qr.jpg";
        String logoPath = "C:\\Users\\lenovo\\Desktop\\ss\\logo.jpg";
        createQR(text, format, filePath, logoPath);
    }

    public static void createQR(String text, String format, String filePath, String logoFile){
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");   // 内容所使用字符集编码

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            File logo = new File(logoFile);
            BufferedImage qrImage = toBufferedImage(matrix); //读取图片
            BufferedImage logoImage = ImageIO.read(logo); //读取图片
            logoImage = ImageUtil.zoomImage(logoImage, 50, 50);
            ImageUtil.overlapImage(qrImage, logoImage, new int[]{125,125},null, filePath);
        } catch (Exception e){
            logger.error("生成二维码失败", e);
            throw new RuntimeException("生成二维码失败", e);
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    public static void writeToFile(BitMatrix matrix, String format, String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        if (!ImageIO.write(image, format, stream)) {
            throw new IOException("Could not write an image of format " + format);
        }
    }
}
