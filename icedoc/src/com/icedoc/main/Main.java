package com.icedoc.main;

import com.icedoc.gen.DocService;

public class Main {
    /**
     * 接口文档生成工具
     * 修改folder为你需要扫描的工程的文件夹
     * 修改folderOut为你生成的目录的文件夹
     * conf.txt 配置工程需要修改的信息(可缺省)
     * param.txt 配置常用参数名称(可缺省)
     * @param args
     */
	public static void main(String[] args) {
		// String folder="C:/Users/Administrator/Desktop";
		String folder = "D:/works/workspace1/SMBMS";
		// String folder="C:/Users/Administrator/Desktop/SMBMS/SMBMS";
		// String folder="E:/workspace";
		String folderOut = "C:/Users/Administrator/Desktop/SMBMS";
		String host = "http://localhost:8080/xxx";
		String postType = null;
		DocService.service(folder, folderOut, host, postType);
		System.out.println("生成成功,请查看:" + folderOut);
	}
}
