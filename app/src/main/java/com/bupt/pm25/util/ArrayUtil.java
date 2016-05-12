package com.bupt.pm25.util;

import java.util.Arrays;

/**
 * Created by xuhuaiyu on 16/3/1.
 */
public class ArrayUtil{
	private ArrayUtil(){

	}

	public static ArrayUtil util = null;
	public static ArrayUtil getInstance(){
		return util == null ? new ArrayUtil() : util;
	}

	public byte[] concat(byte[] arrayHead, byte[] arrayTail){
		byte[] newArray = Arrays.copyOf(arrayHead, arrayHead.length + arrayTail.length);
		System.arraycopy(arrayTail, 0, newArray, arrayHead.length, arrayTail.length);
		return newArray;
	}
}
