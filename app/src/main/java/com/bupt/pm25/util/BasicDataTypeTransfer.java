package com.bupt.pm25.util;

import java.io.UnsupportedEncodingException;

/**
 * Created by xuhuaiyu on 16/1/20.
 */


public class BasicDataTypeTransfer {

	public static BasicDataTypeTransfer basicDataTypeTransfer = null;

	public static BasicDataTypeTransfer getInstance() {
		if (basicDataTypeTransfer == null) {
			basicDataTypeTransfer = new BasicDataTypeTransfer();
		}
		return basicDataTypeTransfer;
	}

	/*
	 *将int转换为小字节序
	 **/
	public byte[] IntToByteArray(int n) {
		byte[] b = new byte[4];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);

		return b;
	}



	public int ByteArrayToInt(byte[] bAttr) {
		int n = 0;
		int leftmove;
		for (int i = 0; i < 4 && (i < bAttr.length); i++) {
			leftmove = i * 8;
			n += bAttr[i] << leftmove;
		}
		return n;

	}

	public byte[] longToByteArray(long n){
		byte[] b = new byte[8];
		b[0] = (byte) (n & 0xff);
		b[1] = (byte) (n >> 8 & 0xff);
		b[2] = (byte) (n >> 16 & 0xff);
		b[3] = (byte) (n >> 24 & 0xff);

		b[4] = (byte) (n >> 32 & 0xff);
		b[5] = (byte) (n >> 40 & 0xff);
		b[6] = (byte) (n >> 48 & 0xff);
		b[7] = (byte) (n >> 56 & 0xff);

		return b;
	}

	public long byteToLong(byte[] b) {
		long s = 0;
		long s0 = b[0] & 0xff;// 最低位
		long s1 = b[1] & 0xff;
		long s2 = b[2] & 0xff;
		long s3 = b[3] & 0xff;
		long s4 = b[4] & 0xff;// 最低位
		long s5 = b[5] & 0xff;
		long s6 = b[6] & 0xff;
		long s7 = b[7] & 0xff;

		// s0不变
		s1 <<= 8;
		s2 <<= 16;
		s3 <<= 24;
		s4 <<= 8 * 4;
		s5 <<= 8 * 5;
		s6 <<= 8 * 6;
		s7 <<= 8 * 7;
		s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;
		return s;
	}

	public byte[] doubleToByteArray(double n){
		return longToByteArray( Double.doubleToLongBits(n) );

	}

	public byte[] StringToByteArray(String str) {
		byte[] temp = new byte[100];
		try {
			temp = str.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	public String ByteArrayToString(byte[] bAttr, int maxLen) {
		int index = 0;
		while (index < bAttr.length && index < maxLen) {
			if (bAttr[index] == 0) {
				break;
			}
			index++;
		}
		byte[] tmp = new byte[index];
		System.arraycopy(bAttr, 0, tmp, 0, index);
		String str = null;
		try {
			str = new String(tmp, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;

	}

}

