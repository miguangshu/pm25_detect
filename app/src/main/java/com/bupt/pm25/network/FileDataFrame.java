package com.bupt.pm25.network;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhuaiyu on 16/3/1.
 * socket连接中的文件模块
 */



/*
* 数据流结构：
* DoubleVar(8B) | File1(FileDesc) | File2(FileDesc) | File3(FileDesc) | Content1 | Content2 | Content3
* ContentX是文件内容，长度等于与之对应的FileX中的FileLen的值。
*
* 上述FileDesc结构:
* FileNameLen(4B) | FileName (FileNameLen B) | FileLen(8B)
*
* */
public class FileDataFrame {
	double proportion;          //大小图片比例
	List<FileDesc> fileDescs;//文件描述符
	List<File> fileContent; //文件内容

	public FileDataFrame(double proportion, List<File> files){
		this.proportion = proportion;
		fileContent = files;
		fileDescs = new ArrayList<FileDesc>();
		for(File f : files){
			fileDescs.add(new FileDesc(f.getName().length(), f.getName(), f.length()));
			Log.d("sockettest", f.getName().length() + " " + f.getName());
		}
	}

	class FileDesc{
		int fileNameLen;
		String fileName;
		long fileLen;

		public FileDesc(int fileNameLen, String fileName, long fileLen){
			this.fileNameLen = fileNameLen;
			this.fileName = fileName;
			this.fileLen = fileLen;
		}
	}

	public List<File> getFileContent() {
		return fileContent;
	}

	public void setFileContent(List<File> fileContent) {
		this.fileContent = fileContent;
	}

	public List<FileDesc> getFileDescs() {
		return fileDescs;
	}

	public void setFileDescs(List<FileDesc> fileDescs) {
		this.fileDescs = fileDescs;
	}

	public double getProportion() {
		return proportion;
	}

	public void setProportion(double proportion) {
		this.proportion = proportion;
	}


}
