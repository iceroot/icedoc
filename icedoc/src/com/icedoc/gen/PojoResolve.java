package com.icedoc.gen;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.icedoc.gen.Param;
import com.xiaoleilu.hutool.io.FileUtil;

public class PojoResolve {
	public static ReturnPojo resolve(String fileName) {
		List<String> readUtf8Lines = FileUtil.readUtf8Lines(fileName);
		List<Param> list = new ArrayList<Param>();
		String className = null;
		String packageName = null;
		if (readUtf8Lines == null) {
			throw new RuntimeException("对应的实体类文件 " + fileName + " 可能不存在");
		}
		int status = 0;
		for (int i = 0; i < readUtf8Lines.size(); i++) {
			String line = readUtf8Lines.get(i);
			line = line.trim();
			line = line.replace("\t", " ");
			line = line.replaceAll("\\s+", " ");
			if (status == 0) {
				if (line.startsWith("package ")) {
					line = StringUtils.removeStart(line, "package ");
					line = StringUtils.removeEnd(line, ";");
					packageName = line.trim();
				} else if (line.contains(" class ")) {
					line = StringUtils.removeStart(line, "public");
					line = StringUtils.removeEnd(line, "{");
					line = line.trim();
					line = StringUtils.removeStart(line, "class");
					line = line.trim();
					className = line;
					status = 1;
				}
			} else if (status == 1) {
				String curLine = line;
				String left = null;
				String comment = null;
				if (line.contains("//")) {
					left = StringUtils.substringBefore(line, "//");
					comment = StringUtils.substringAfter(line, "//");
					comment = comment.trim();
				} else {
					left = line;
					comment = null;
				}
				left = left.trim();
				left = StringUtils.removeEnd(left, ";");
				if (!left.contains("(") && !left.contains(")") && left.startsWith("private")) {
					left = StringUtils.removeStart(left, "private");
					left = left.trim();
					if (left.contains(" ")) {
						String type = StringUtils.substringBeforeLast(left, " ");
						String name = StringUtils.substringAfterLast(left, " ");
						if (type.contains("[") && type.contains("]")) {
							type = type.replace(" ", "");
						}
						if (!name.contains(" ")) {
							if (comment == null) {
								comment = "";
							}
							Param param = new Param(type, name, comment);
							list.add(param);
						}
					}
				}
			}

		}
		ReturnPojo returnPojo = new ReturnPojo();
		returnPojo.setClassName(className);
		returnPojo.setPackageName(packageName);
		returnPojo.setFields(list);
		return returnPojo;
	}
}
