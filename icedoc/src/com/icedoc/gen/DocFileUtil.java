package com.icedoc.gen;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;

public class DocFileUtil {

	public static String getName(File fileName) {
		return fileName.getAbsolutePath().replace("\\", "/");
	}

	public static boolean contain(List<String> readLines, String word) {
		if (readLines == null || readLines.size() == 0) {
			return false;
		}
		for (String line : readLines) {
			if (line.trim().startsWith(word)) {
				return true;
			}
		}
		return false;
	}

	public static String getPackageName(List<String> readLines) {
		if (readLines == null || readLines.size() == 0) {
			return null;
		}
		for (String line : readLines) {
			line = line.trim().replace("\t", " ");
			if (line.startsWith("package ")) {
				line = StringUtils.removeEnd(line, ";");
				line = StringUtils.removeStart(line, "package");
				line = line.trim();
				return line;
			}
		}
		return null;
	}

	public static String getBasePath(String fullName, String className) {
		fullName = fullName.replace("\\", "/");
		className = className.replace(".", "/");
		String basePath = StringUtils.substringBeforeLast(fullName, className);
		return StringUtils.removeEnd(basePath, "/");
	}

	public static String now() {
		return DateUtil.formatDate(new Date());
	}

	public static String getProjectName(String basePath) {
		String path = StringUtils.removeEnd(basePath, "/main/java");
		path = StringUtils.removeEnd(path, "/src");
		path = StringUtils.substringAfterLast(path, "/");
		return path;
	}

	public static String[] split(String paramBlockStr) {
		if (paramBlockStr == null || "".equals(paramBlockStr)) {
			return new String[] {};
		}
		char[] chs = paramBlockStr.toCharArray();
		StringBuilder sb = new StringBuilder();
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < chs.length; i++) {
			char ch = chs[i];
			if (ch == ',') {
				list.add(sb.toString());
				sb = new StringBuilder();
			} else if (ch == '(') {
				String before = before(chs, i);
				if ("@RequestParam".equals(before)) {
					int rightIndex = paramBlockStr.indexOf(")", i + 1);
					if (rightIndex == -1) {
						throw new ParamException("参数括号不匹配");
					}
					String mid = paramBlockStr.substring(i + 1, rightIndex);
					String paramName = getParamName(mid);
					int comma = paramBlockStr.indexOf(",", rightIndex + 1);
					int bracket = paramBlockStr.indexOf(")", rightIndex + 1);
					int min = min(comma, bracket);
					String rightStr = null;
					if (min == -1) {
						rightStr = paramBlockStr.substring(rightIndex + 1);
					} else {
						rightStr = paramBlockStr.substring(rightIndex + 1, min);
					}
					rightStr = rightStr.trim();
					if (!rightStr.contains(" ")) {
						throw new RuntimeException("【错误】，必须包含空格");
					}
					String paramType = StringUtils.substringBefore(rightStr, " ");
					list.add(paramType + " " + paramName);
					sb = new StringBuilder();
					if (min != -1) {
						i = min;
					} else {
						break;
					}
				}
			} else if (ch == ')') {

			} else {
				sb.append(ch);
			}
		}
		String cur = sb.toString();
		if (StringUtils.isNotBlank(cur)) {
			list.add(sb.toString());
		}
		String[] result = new String[list.size()];
		return list.toArray(result);
	}

	private static String getParamName(String str) {
		String result = str;
		if (str.contains("required")) {
			result = StringUtils.substringBefore(str, "required");
			result = result.trim();
		}
		result = StringUtils.removeEnd(result, ",");
		result = result.trim();
		result = StringUtils.removeStart(result, "value");
		result = result.trim();
		result = StringUtils.removeStart(result, "=");
		result = result.trim();
		result = StringUtils.removeStart(result, "\"");
		result = StringUtils.removeEnd(result, "\"");
		result = result.trim();
		return result;
	}

	private static int min(int comma, int bracket) {
		if (comma == -1) {
			if (bracket == -1) {
				return -1;
			} else {
				return bracket;
			}
		} else {
			if (bracket == -1) {
				return comma;
			} else {
				if (comma < bracket) {
					return comma;
				} else {
					return bracket;
				}
			}
		}
	}

	private static String before(char[] chs, int index) {
		StringBuilder result = new StringBuilder();
		while (index-- >= 0) {
			if (chs[index] == ' ') {
				String resultStr = result.toString();
				if (!"".equals(resultStr)) {
					return StrUtil.reverse(resultStr);
				}
			} else if (chs[index] == '@' || !Character.isLetter(chs[index])) {
				result.append(chs[index]);
				return StrUtil.reverse(result.toString());
			} else {
				result.append(chs[index]);
			}
		}
		return "";
	}

	public static String getBasePathConf() {
		File file = new File(".");
		String canonicalPath = file.getAbsolutePath();
		try {
			canonicalPath = file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		canonicalPath = canonicalPath.replace("\\", "/");
		return canonicalPath;
	}

}
