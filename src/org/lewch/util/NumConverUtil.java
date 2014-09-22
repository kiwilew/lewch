package org.lewch.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * <pre>
 * *@description 进制转换工具类
 * @funtion 十六进制内的数字互转
 * @company 
 * @author  
 * 
 * ****************************** 修改记录 ******************************
 * ---------------------------------------------------------------------
 *    时间           |    版本号        |    修改人        |    修改原因
 *  20120504       v1.0		         进制转换工具类初始版
 * 
 * ---------------------------------------------------------------------
 * 
 * </pre>
 */
public class NumConverUtil {
	/** * 支持的最小进制 */
	public static int MIN_RADIX = 2;

	/** * 支持的最大进制 */
	public static int MAX_RADIX = 16;

	// /** 数据格式，精确到35位* */
	// public static String DECIMAL_FOMAT_35 =
	// "0.00000000000000000000000000000000000";
	// /** 数据格式，精确到19位* */
	// public static String DECIMAL_FOMAT_19 = "0.0000000000000000000";
	/** 数据格式，精确到19位* */
	public static String DECIMAL_FOMAT_2 = "0.00";

	/** * 锁定创建 */
	private NumConverUtil() {
	}

	/** * 0-9A-F表示16进制内的 0到15。 */
	private static final String num16 = "0123456789ABCDEF";

	/**
	 * <pre>
	 * 十进制转其他进制
	 * 
	 * @param dec 需要转换的数字
	 * @param toRadix 输出进制。当不在转换范围内时，此参数会被设定为 2，以便及时发现。
	 * @return 指定输出进制的数字
	 * </pre>
	 */
	private static String dec2Any(long dec, int toRadix) {
		if (toRadix < MIN_RADIX || toRadix > MAX_RADIX) {
			toRadix = 2;
		}
		if (toRadix == 10) {
			return String.valueOf(dec);
		}
		// -Long.MIN_VALUE 转换为 2 进制时长度为65
		char[] buf = new char[65];
		int charPos = 64;
		boolean isNegative = (dec < 0);
		if (!isNegative) {
			dec = -dec;
		}
		while (dec <= -toRadix) {
			buf[charPos--] = num16.charAt((int) (-(dec % toRadix)));
			dec = dec / toRadix;
		}
		buf[charPos] = num16.charAt((int) (-dec));
		if (isNegative) {
			buf[--charPos] = '-';
		}
		return new String(buf, charPos, (65 - charPos));
	}

	/**
	 * <pre>
	 * 返回一字符串，包含 number 以 10 进制的表示。
	 * fromBase 只能在 2 和16 之间（包括 2 和 16）。
	 * 
	 * @param number 输入数字 *
	 * @param fromRadix 输入进制
	 * @return 十进制数字
	 * &lt;、pre&gt;
	 * 
	 */
	public static long any2Dec(String number, int fromRadix) {
		long dec = 0;
		long digitValue = 0;
		int len = number.length() - 1;
		for (int t = 0; t <= len; t++) {
			digitValue = num16.indexOf(number.charAt(t));
			dec = dec * fromRadix + digitValue;
		}
		return dec;
	}

	/**
	 * <pre>
	 * 返回一字符串，包含 number 以 toRadix 进制的表示。
	 * number 本身的进制由 fromRadix 指定。fromRadix 和 toRadix 都只能在 2 和16 之间（包括 2 和 16）。
	 * 高于十进制的数字用字母A-F 表示，例如 a 表示 10，b 表示 11 以及 f 表示15。
	 * 
	 * @param number 需要转换的数字 
	 * @param fromRadix 输入进制 
	 * @param toRadix 输出进制 
	 * @return  指定输出进制的数字
	 * </pre>
	 */
	private static String comNumConver(String number, int fromRadix, int toRadix) {
		long dec = any2Dec(number, fromRadix);
		return dec2Any(dec, toRadix);
	}

	/**
	 * <pre>
	 * 返回一字符串，包含 number 以 toRadix 进制的表示。
	 * number 本身的进制由 fromRadix 指定。fromRadix 和 toRadix 都只能在 2 \8\10\16 中间取值，否则返回原值
	 * 
	 * @param number 需要转换的数字 
	 * @param fromRadix 输入进制 
	 * @param toRadix 输出进制 
	 * @return  指定输出进制的数字
	 * </pre>
	 */
	public static String baseNumConver(String number, int fromRadix, int toRadix) {
		boolean isFromRadixBase = (fromRadix == 2 || fromRadix == 8
				|| fromRadix == 10 || fromRadix == 16);
		boolean isToRadixBase = (toRadix == 2 || toRadix == 8 || toRadix == 10 || toRadix == 16);
		// 进行转换并返回
		if (isFromRadixBase && isToRadixBase) {
			return comNumConver(number, fromRadix, toRadix);
		}
		// 返回原值
		return number;
	}

	/**
	 * 转化BYTE[]为十六进制编码字符串
	 * 
	 * @param bytes
	 *            []
	 * @return
	 */
	public static String encodeByte(byte[] bytes) {
		// 根据默认编码获取字节数组
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			sb.append(num16.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(num16.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	public static int byteConverToNumber(byte[] bytes, int fromRadix,
			int toRadix) {
		String byteNumStr = encodeByte(bytes);
		String numVal = baseNumConver(byteNumStr, fromRadix, toRadix);
		return Integer.parseInt(numVal);
	}

	// 字节到浮点转换
	public static double byteConverToDouble(byte[] b) {
		long l;
		l = b[0];
		l &= 0xff;
		l |= ((long) b[1] << 8);
		l &= 0xffff;
		l |= ((long) b[2] << 16);
		l &= 0xffffff;
		l |= ((long) b[3] << 24);
		l &= 0xffffffffl;
		l |= ((long) b[4] << 32);
		l &= 0xffffffffffl;
		l |= ((long) b[5] << 40);
		l &= 0xffffffffffffl;
		l |= ((long) b[6] << 48);
		l |= ((long) b[7] << 56);
		return Double.longBitsToDouble(l);
	}

	/**
	 * 科学计数数据格式化
	 * 
	 * @param scientificNum
	 * @return
	 */
	public static String fomatScientificNotation(String scientificNum) {
		String flag = "";
		BigDecimal b = new BigDecimal(scientificNum);
		if (b.compareTo(new BigDecimal("0")) > 0) {
			flag = "+";
		}
		DecimalFormat df = new DecimalFormat(DECIMAL_FOMAT_2);
		return flag + df.format(b);
	}

}
