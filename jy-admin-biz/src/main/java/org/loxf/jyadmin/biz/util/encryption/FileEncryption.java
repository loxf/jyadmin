package org.loxf.jyadmin.biz.util.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 文件加密
 *
 */
public class FileEncryption {
	/**
	 * 将文件转成base64 字符串
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(File file) throws Exception {
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return new BASE64Encoder().encode(buffer);
	}


	/**
	 * 将base64字符解码保存文件
	 * @param base64Code
	 * @param os
	 * @throws Exception
	 */
	public static void decoderBase64File(String base64Code, OutputStream os) throws Exception {
		try {
			byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
			os.write(buffer);
		} finally{
			if (os != null) {
				try {
					os.flush();
				} finally{
					os.close();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(encodeBase64File(new File("C:\\Users\\Administrator\\Desktop\\sf\\dfg.png")));
	}
}
