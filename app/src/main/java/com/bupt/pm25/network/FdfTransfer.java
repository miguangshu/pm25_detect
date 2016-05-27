package com.bupt.pm25.network;

import android.util.Log;


import com.bupt.pm25.util.ArrayUtil;
import com.bupt.pm25.util.BasicDataTypeTransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by xuhuaiyu on 16/3/1.
 */
public class FdfTransfer {
	private static FdfTransfer transfer = null; //FileDataFrame Transfer
	private BasicDataTypeTransfer bdtt = BasicDataTypeTransfer.getInstance();
	private ArrayUtil au = ArrayUtil.getInstance();

	private FdfTransfer() {

	}

	public static FdfTransfer getInstance() {
		return transfer == null ? new FdfTransfer() : transfer;
	}

	/*
	file data frame to byte array
	 */
	public byte[] toByteArray(FileDataFrame fdf) {

//		byte[] propArray = bdtt.doubleToByteArray(fdf.proportion);// proportion array,8B
//		Log.d("sockettest", "proportion:" + propArray.length);

		byte[] fdArray = fileDescsToByteArray(fdf.fileDescs);  // file descriptor array
		Log.d("sockettest", "file desc:" + fdArray.length);
		byte[] fcArray = fileContentToByteArray(fdf.fileContent);// file content array
		Log.d("sockettest", "file content:" + fcArray.length);

//		byte[] array = au.concat(propArray, au.concat(fdArray, fcArray));


		byte[] array = au.concat(fdArray, fcArray);
		Log.d("sockettest", "total length:" + array.length);

		long totalLen = 8 + array.length;//报文总长度
		array = au.concat(bdtt.longToByteArray(totalLen), array);
		Log.d("sockettest", "total_size" + totalLen);
		return array;
	}

	/*
	file descriptors to byte array
	 */
	private byte[] fileDescsToByteArray(List<FileDataFrame.FileDesc> fdList) {

		byte[] result = null;
		for (FileDataFrame.FileDesc desc : fdList) {
			byte[] fnlArray = bdtt.IntToByteArray(desc.fileNameLen);//file name len array, 4B
			byte[] fnArray = bdtt.StringToByteArray(desc.fileName);// file name array, file name len byte
			byte[] flArray = bdtt.longToByteArray(desc.fileLen);//file length array, 8B

			byte[] array = au.concat(fnlArray, au.concat(fnArray, flArray));
			result = result == null ? array : au.concat(result, array);
//			au.concat(result, au.concat(au.concat(fnlArray, fnArray), flArray));

			//拼接三个字节数组，再拼接所有文件的字节数组
		}
		return result;
	}

	/*
	file content to byte array
	*/
	private byte[] fileContentToByteArray(List<File> fcList) {
		byte[] result = null;
		for (File f : fcList) {
			byte[] array = getFileContent(f);
			result = result == null ? array : au.concat(result, array);
		}
		return result;
	}

	/**
	 * NIO way
	 *
	 * @param f
	 * @return
	 */
	public static byte[] getFileContent(File f) {

		FileChannel channel = null;
		FileInputStream fs = null;
		ByteBuffer byteBuffer = null;
		try {
			fs = new FileInputStream(f);
			channel = fs.getChannel();
			byteBuffer = ByteBuffer.allocate((int) channel.size());
			while ((channel.read(byteBuffer)) > 0) {
				// do nothing
				// System.out.println("reading");
			}
			return byteBuffer.array();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return byteBuffer.array();
	}

}
