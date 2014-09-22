package org.lewch.bit64todouble;

import org.lewch.util.NumConverUtil;

/**
 * <pre>
 * @function  64位十六进制流转双精度浮点数工具类方法
 *  
 *  1.调用bit64HexStrToDouble静态方法即可实现
 *  2.要使用二进制表示的64位数据转双精度浮点数去除bit64HexStrToDouble中的String bit64Binary = hexStrToBinary(hexStr);并将参数改为二进制串即可
 *  3.若需要将32位流数据转不单精度浮点数，请参考IEEE754标准，参照64位->double的做法扩展
 *  
 *  @author liuchunhe 
 *  @date 2014-4-11
 * 
 * </pre>
 * 
 * **/
public class Bit64HexBinaryToDoubleUtil {

	public static void main(String[] args) {

		try {
			System.out.println(NumConverUtil.fomatScientificNotation(String
					.valueOf(bit64HexStrToDouble("C1330347CCCCCCCD"))));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.out.println(calcmantissaBit("101"));
	}

	public static double bit64HexStrToDouble(String hexStr) throws Exception {
		// 十六进制转二进制
		String bit64Binary = hexStrToBinary(hexStr);

		if (bit64Binary == null || bit64Binary.length() != 64) {
			throw new Exception("输入的十六进串[" + hexStr + "]不是64位的字符串");
		}
		// 符号位（最高位）0为正，1为负
		String signBit = bit64Binary.substring(0, 1);
		int sign = 1;
		if ("1".equals(signBit)) {
			sign = -1;
		}

		// 指数位，双精度浮点数偏移量为1023
		String exponentBit = bit64Binary.substring(1, 12);

		String exponent = NumConverUtil.baseNumConver(exponentBit, 2, 10);
		// 指数
		int exponentIntVal = Integer.parseInt(exponent) - 1023;
		// System.out.println(exponentIntVal);

		double mantissaBit;// 尾数
		// 指数全为0或1
		if (exponentAllZero(exponentBit) || exponentAllOne(exponentBit)) {
			mantissaBit = 0 + calcmantissaBit(bit64Binary.substring(12));
		} else {// 指数不全为0或1
			mantissaBit = 1 + calcmantissaBit(bit64Binary.substring(12));
		}
		double result = sign * Math.pow(2, exponentIntVal) * mantissaBit;
		return result;
	}

	public static String hexStrToBinary(String hexStr) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hexStr.length(); i++) {
			try {
				sb.append(getBinaryFromHexChar(hexStr.substring(i, i + 1)));
			} catch (Exception e) {
				throw new Exception("输入的十六进串[" + hexStr + "]含非0-9或a-f或A-F的字符");
			}
		}
		return sb.toString();
	}

	public static double calcmantissaBit(String mantissaBit) {
		double val = 0;
		for (int i = 0; i < mantissaBit.length(); i++) {
			String curBitVal = mantissaBit.substring(i, i + 1);
			int curBitIntVal = Integer.parseInt(curBitVal);
			double temp = curBitIntVal * Math.pow(2, -1 * (i + 1));
			val += temp;
		}
		return val;
	}

	public static boolean exponentAllZero(String exponent) {
		for (int i = 0; i < exponent.length(); i++) {
			if ("1".equals(exponent.substring(i, i + 1))) {
				return false;
			}
		}
		return true;
	}

	public static boolean exponentAllOne(String exponent) {
		for (int i = 0; i < exponent.length(); i++) {
			if ("0".equals(exponent.substring(i, i + 1))) {
				return false;
			}
		}
		return true;
	}

	public static String getBinaryFromHexChar(String hexChar) throws Exception {
		hexChar = hexChar.toUpperCase();
		if ("0".endsWith(hexChar)) {
			return "0000";
		} else if ("1".endsWith(hexChar)) {
			return "0001";
		} else if ("2".endsWith(hexChar)) {
			return "0010";
		} else if ("3".endsWith(hexChar)) {
			return "0011";
		} else if ("4".endsWith(hexChar)) {
			return "0100";
		} else if ("5".endsWith(hexChar)) {
			return "0101";
		} else if ("6".endsWith(hexChar)) {
			return "0110";
		} else if ("7".endsWith(hexChar)) {
			return "0111";
		} else if ("8".endsWith(hexChar)) {
			return "1000";
		} else if ("9".endsWith(hexChar)) {
			return "1001";
		} else if ("A".endsWith(hexChar)) {
			return "1010";
		} else if ("B".endsWith(hexChar)) {
			return "1011";
		} else if ("C".endsWith(hexChar)) {
			return "1100";
		} else if ("D".endsWith(hexChar)) {
			return "1101";
		} else if ("E".endsWith(hexChar)) {
			return "1110";
		} else if ("F".endsWith(hexChar)) {
			return "1111";
		} else {
			throw new Exception("参数异常，只能输入[0-9|a-f|A-F]");
		}
	}

}
