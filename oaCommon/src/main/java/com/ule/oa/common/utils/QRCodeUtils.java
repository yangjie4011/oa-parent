package com.ule.oa.common.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;

/**
  * @ClassName: QRCodeUtils
  * @Description: 生成和解析二维码
  * @author minsheng
  * @date 2017年11月27日 下午4:34:57
 */
public class QRCodeUtils {
	private static final int BLACK = 0xff000000;
	private static final int WHITE = 0xFFFFFFFF;
	
	public static void main(String[] args) {
		QRCodeUtils.encode("http://oa.uletm.com/oaWeb/", "C://test.png", BarcodeFormat.QR_CODE, 100, 100);
	}
	
	/**
	  * encode(生成二维码)
	  * @Title: encode
	  * @Description: 生成二维码
	  * @param contents
	  * @param filePath
	  * @param format
	  * @param width
	  * @param height
	  * void    返回类型
	  * @throws
	 */
	public static void encode(String contents, String filePath, BarcodeFormat format, int width, int height) {
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, format, width, height);
		    writeToFile(bitMatrix, "png", new File(filePath));
		} catch (Exception e) {
		}
	}
	
	/**
     * decode(解析二维码)
     * @Title: decode
     * @Description: 解析二维码
     * @param file    设定文件
     * void    返回类型
     * @throws
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
	public static void decode(File file) {
       try {
           BufferedImage image;
           try {
               image = ImageIO.read(file);
               if (image == null) {
                   System.out.println("Could not decode image");
               }
               LuminanceSource source = new BufferedImageLuminanceSource(image);
               BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
               Result result;
               Hashtable hints = new Hashtable();
               //解码设置编码方式为：utf-8
               hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
               result = new MultiFormatReader().decode(bitmap, hints);
               String resultStr = result.getText();
               System.out.println("解析后内容：" + resultStr);
           } catch (IOException ioe) {
               System.out.println(ioe.toString());
           } catch (ReaderException re) {
               System.out.println(re.toString());
           }
       } catch (Exception ex) {
           System.out.println(ex.toString());
       }
   }

	/**
	  * writeToFile(生成二维码图片)
	  * @Title: writeToFile
	  * @Description: 生成二维码图片
	  * @param matrix
	  * @param format
	  * @param file
	  * @throws IOException    设定文件
	  * void    返回类型
	  * @throws
	 */
	public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
	    BufferedImage image = toBufferedImage(matrix);
	    ImageIO.write(image, format, file);
	}
		 
	/**	
	  * toBufferedImage(生成二维码内容)
	  * @Title: toBufferedImage
	  * @Description: 生成二维码内容
	  * @param matrix
	  * @return    设定文件
	  * BufferedImage    返回类型
	  * @throws
	 */
    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) == true ? BLACK : WHITE);
            }
        }
        return image;
    }
}
