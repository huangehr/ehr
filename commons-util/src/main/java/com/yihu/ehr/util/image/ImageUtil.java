package com.yihu.ehr.util.image;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片处理工具包装。
 *
 * @author hzp
 * @version 1.0
 * @created 2016.08.10 15:33
 */
public class ImageUtil {

    /**
     * 获取缩略图
     */
    public static byte[] scale (byte [] imgByte, int w, int h) {
        byte[] newImgByte = null;
        try {
            Image img = ImageIO.read(new ByteArrayInputStream(imgByte));
            // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
            int width = img.getWidth( null);
            int height = img.getHeight( null);
            if ((width * 1.0) / w < (height * 1.0) / h) {
                if (width > w) {
                    h = Integer.parseInt(new java.text.DecimalFormat("0" ).format(height * w / (width * 1.0)));
                }
            } else {
                if (height > h) {
                    w = Integer.parseInt(new java.text.DecimalFormat("0" ).format(width * h / (height * 1.0)));
                }
            }
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            g.drawImage(img, 0, 0, w, h, null, null);
            g.dispose();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (w == 640) {
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(byteArrayOutputStream);
                JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(bi);
                jep.setQuality(0.8f, true);
                encoder.encode(bi, jep);
            } else {
                ImageIO.write(bi, "PNG", byteArrayOutputStream);
            }
            newImgByte = byteArrayOutputStream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return newImgByte;
    }
}
