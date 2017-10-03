package com.icedoc.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.icedoc.doc.Doc;
import com.icedoc.doc.Params;
import com.xiaoleilu.hutool.io.FileUtil;
import com.xiaoleilu.hutool.util.ArrayUtil;
import com.xiaoleilu.hutool.util.StrUtil;

public class SimpleService {

	public static void gen(String javaFile, String host, String[] exceptClass, String[] exceptReturnClass,
			String[] exceptParamClass, String postType, Map<String, String> paramNames) {
		List<String> readLines = FileUtil.readLines(javaFile, "UTF-8");
		Map<String, String> impMap = new HashMap<String, String>();
		int status = 0;
		String clasMapping = "";
		String describe = "";
		List<Param> paramBlockListResult = null;
		int docInex = DocContext.getIndex();
		List<Doc> docList = new ArrayList<Doc>();
		for (int i = 0; i < readLines.size(); i++) {
			postType = DocContext.getPostType();
			Doc doc = new Doc();
			String line = readLines.get(i);
			if (line == null) {
				continue;
			}
			line = line.trim();
			if (line.startsWith("import")) {
				String impo = StrUtil.removePrefix(line, "import");
				impo = impo.trim();
				impo = StrUtil.removeSuffix(impo, ";");
				String simpleName = StringUtils.substringAfterLast(impo, ".");
				if (ArrayUtil.contains(exceptClass, simpleName)) {
					continue;
				}
				impMap.put(simpleName, impo);
			} else if (line.startsWith("package")) {
				String pack = StrUtil.removePrefix(line, "package");
				pack = pack.trim();
				pack = StrUtil.removeSuffix(pack, ";");
			} else if (line.contains("class") && line.contains("public")) {
				String cls = StrUtil.removePrefix(line, "public");
				cls = cls.trim();
				cls = StrUtil.removeSuffix(cls, "{");
				cls = StrUtil.removePrefix(cls, "class");
				status = 2;
			} else if (line.startsWith("@Controller")) {
				status = 1;
			} else {
				if (status == 1) {
					if (line.startsWith("@RequestMapping")) {
						String classMapping = StrUtil.removePrefix(line, "@RequestMapping");
						classMapping = StrUtil.removePrefix(classMapping, "(");
						classMapping = StrUtil.removeSuffix(classMapping, ")");
						classMapping = classMapping.trim();
						classMapping = StrUtil.removePrefix(classMapping, "\"");
						classMapping = StrUtil.removeSuffix(classMapping, "\"");
						clasMapping = classMapping;
					}
				} else if (status == 2) {
					String next = next(readLines, i);
					if (line.startsWith("@RequestMapping") && next.contains("(")) {
						String classMappingParam = StrUtil.removePrefix(line, "@RequestMapping");
						classMappingParam = StrUtil.removePrefix(classMappingParam, "(");
						classMappingParam = StrUtil.removeSuffix(classMappingParam, ")");
						classMappingParam = classMappingParam.trim();
						classMappingParam = getUrl(classMappingParam);
						if (classMappingParam.contains("\1")) {
							String oldMapping = classMappingParam;
							classMappingParam = StringUtils.substringBefore(classMappingParam, "\1");
							if (postType == null || "auto".equalsIgnoreCase(postType)) {
								postType = StringUtils.substringAfter(oldMapping, "\1");
							}
						}
						Pair<String, List<String>> pair = getParamStr(readLines, i + 1);
						try {
							getComment(readLines, i);
						} catch (Exception e) {
							e.printStackTrace();
						}
						List<String> commentLines = getComment(readLines, i);
						commentLines = reverse(commentLines);
						Map<String, String> commentMap = commentMap(commentLines);
						describe = commentMap.get("__describe__");
						commentMap.remove("__describe__");
						String paramBlockStr = pair.getLeft();
						List<String> paramBlockList = pair.getRight();
						String returnBlockStr = StringUtils.substringBefore(paramBlockStr, "(");
						returnBlockStr = getReturn(returnBlockStr);
						returnBlockStr = getPojoReturn(returnBlockStr, exceptReturnClass, impMap);
						paramBlockStr = StringUtils.substringAfter(paramBlockStr, "(");
						paramBlockStr = StringUtils.substringBeforeLast(paramBlockStr, ")");
						paramBlockStr = paramBlockStr.trim();
						List<Param> listParam = null;
						try {
							listParam = getListParam(paramBlockStr, exceptParamClass);
						} catch (ParamException e) {
							throw new ParamException(e.getMessage() + ",文件位于：" + javaFile);
						}
						List<Param> listParamInBlock = getListParam(paramBlockList);
						listParam.addAll(listParamInBlock);
						paramBlockListResult = listParam;
						doc.setIndex(docInex++ + "");
						doc.setName(defaultNull(describe));
						String url = removeLast(host) + addFirst(clasMapping) + addFirst(classMappingParam);
						doc.setUrl(url);
						if (postType == null) {
							doc.setType("POST");
						} else {
							doc.setType(postType);
						}
						doc.setParams(convert(paramBlockListResult, commentMap, paramNames));
						if (StrUtil.isNotBlank(returnBlockStr)) {
							doc.setReturns(ParamUtils.str2ReturnList(returnBlockStr));
						}
						docList.add(doc);
					}
				}
			}
		}
		DocContext.setIndex(docInex);
		DocContext.getDocList().addAll(docList);
	}

	private static String getUrl(String str) {
		str = str.trim();
		if (str.startsWith("value") && str.contains("=")) {
			String valueStr = StringUtils.substringBefore(str, "method");
			valueStr = valueStr.trim();
			valueStr = StringUtils.removeEnd(valueStr, ",");
			valueStr = StringUtils.removeStart(valueStr, "value");
			valueStr = valueStr.trim();
			valueStr = StringUtils.removeStart(valueStr, "=");
			valueStr = valueStr.trim();
			valueStr = StringUtils.removeStart(valueStr, "{");
			valueStr = StringUtils.removeEnd(valueStr, "}");
			valueStr = valueStr.trim();
			if (valueStr.contains(",")) {
				valueStr = StringUtils.substringBefore(valueStr, ",");
				valueStr = valueStr.trim();
			}
			valueStr = StringUtils.removeStart(valueStr, "\"");
			valueStr = StringUtils.removeEnd(valueStr, "\"");
			if (str.contains("method")) {
				String postTypeStr = StringUtils.substringAfter(str, "method");
				postTypeStr = postTypeStr.trim();
				postTypeStr = StringUtils.removeStart(postTypeStr, "=");
				postTypeStr = postTypeStr.trim();
				postTypeStr = StringUtils.removeStart(postTypeStr, "{");
				postTypeStr = StringUtils.removeEnd(postTypeStr, "}");
				postTypeStr = postTypeStr.trim();
				postTypeStr = postTypeStr.replace("RequestMethod.", "");
				postTypeStr = postTypeStr.replace(" ", "");
				return valueStr + "\1" + postTypeStr;
			}
			return valueStr;
		} else {
			String valueStr = StringUtils.removeStart(str, "\"");
			valueStr = StringUtils.removeEnd(valueStr, "\"");
			return valueStr;
		}
	}

	private static String getPojoReturn(String str, String[] exceptReturnClass, Map<String, String> impMap) {
		if (str == null || "void".equals(str)) {
			return "";
		}
		if (ArrayUtil.contains(exceptReturnClass, str)) {
			return "";
		}
		if (str.contains(".")) {
			return str;
		}
		String className = impMap.get(str);
		if (className != null) {
			return className;
		}
		return str;
	}

	private static String getReturn(String str) {
		if (str == null) {
			return "void";
		}
		str = str.trim();
		str = StrUtil.removePrefix(str, "public");
		str = StrUtil.removePrefix(str, "private");
		str = StrUtil.removePrefix(str, "protected");
		str = str.trim();
		str = StrUtil.removePrefix(str, "static");
		str = str.trim();
		str = StrUtil.removePrefix(str, "final");
		str = str.trim();
		str = str.replaceAll("//s+", " ");
		str = StringUtils.substringBeforeLast(str, " ");
		str = str.trim();
		if (str.contains("<")) {
			str = StringUtils.substringBefore(str, "<");
		}
		str = str.trim();
		return str;
	}

	private static String addFirst(String str) {
		if (str == null || "".equals(str)) {
			return "";
		}
		if (!str.startsWith("/")) {
			return "/" + str;
		}
		return str;
	}

	private static String removeLast(String str) {
		if (str == null) {
			return "";
		}
		if (str.endsWith("/")) {
			return str.substring(0, str.length() - 1);
		}
		return str;
	}

	private static String defaultNull(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	private static List<Params> convert(List<Param> paramBlockListResult, Map<String, String> commentMap,
			Map<String, String> paramNames) {
		if (paramBlockListResult == null) {
			return new ArrayList<Params>();
		}
		List<Params> list = new ArrayList<Params>();
		for (int i = 0; i < paramBlockListResult.size(); i++) {
			Param param = paramBlockListResult.get(i);
			String name = param.getName();
			String comment = commentMap.get(name);
			if (StringUtils.isBlank(comment)) {
				comment = paramNames.get(name);
			}
			if (comment == null) {
				comment = "";
			}
			list.add(convert(param, i, comment));
		}
		return list;
	}

	private static Params convert(Param param, int index, String comment) {
		Params params = new Params();
		params.setCode(param.getName());
		params.setName(comment);
		params.setRemark("");
		params.setNeed("必填");
		params.setIndex(index + 1 + "");
		return params;
	}

	private static Map<String, String> commentMap(List<String> commentLines) {
		int status = 0;
		StringBuilder sb = new StringBuilder();
		Map<String, String> map = new HashMap<String, String>();
		String describe = null;
		for (int i = 0; i < commentLines.size(); i++) {
			String line = commentLines.get(i);
			line = line.trim();
			if (line.startsWith("*")) {
				line = StrUtil.removePrefix(line, "*");
				line = line.trim();
				if (status == 0) {
					if (!line.startsWith("@")) {
						sb.append(line);
						try {
							next(commentLines, i).trim();
						} catch (Exception e) {
							e.printStackTrace();
						}
						String next = next(commentLines, i).trim();
						next = StrUtil.removePrefix(next, "*");
						next = next.trim();
						if (next.startsWith("@param") || next.startsWith("@return") || next.endsWith("*/")
								|| next == null) {
							describe = sb.toString();
							status = 1;
						}
					} else {
						describe = "";
						status = 1;
					}
				} else {
					if (line.startsWith("@")) {
						if (line.startsWith("@return")) {

						} else if (line.startsWith("@param")) {
							line = StrUtil.removePrefix(line, "@param");
							line = line.trim();
							String[] split = line.split("\\s+");
							if (split.length == 1) {
								String name = split[0];
								String next = next(commentLines, i);
								if (StrUtil.isNotBlank(next)) {
									String comment = next.trim();
									comment = StrUtil.removePrefix(comment, "*");
									comment = comment.trim();
									if (!comment.startsWith("@param") && !comment.startsWith("@return")
											&& !comment.endsWith("*/")) {
										map.put(name, comment);
									}
								}
							} else if (split.length == 2) {
								String name = split[0];
								String comment = split[1];
								map.put(name, comment);
							}
						}
					} else {

					}
				}
			}
		}
		map.put("__describe__", describe);
		return map;
	}

	private static List<String> reverse(List<String> list) {
		if (list == null) {
			return new ArrayList<String>();
		}
		List<String> listNew = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			listNew.add(list.get(list.size() - i - 1));
		}
		return listNew;
	}

	private static List<String> getComment(List<String> readLines, int lineNum) {
		String last = getLast(readLines, lineNum);
		List<String> list = new ArrayList<String>();
		if (last != null) {
			last = last.trim();
			if (last.endsWith("*/")) {
				int index = lineNum - 1;
				String line = null;
				while (!(line = readLines.get(index--).trim()).startsWith("/**")) {
					line = line.trim();
					if (line.startsWith("*")) {
						list.add(line);
					} else if (line.startsWith("/*") && line.endsWith("*/")) {
						list.add("*/");
						String mid = StringUtils.removeStart(line, "/*");
						mid = StringUtils.removeEnd(mid, "*/");
						list.add("* @return");
						list.add("* " + mid);
						return list;
					} else {
						throw new RuntimeException("注释不正确:" + line);
					}
				}
				return list;
			} else {
				return null;
			}
		}
		return null;
	}

	private static String getLast(List<String> readLines, int lineNum) {
		if (lineNum - 1 >= 0) {
			return readLines.get(lineNum - 1);
		}
		return null;
	}

	private static List<Param> getListParam(List<String> paramBlockList) {
		List<Param> list = new ArrayList<Param>();
		for (int i = 0; i < paramBlockList.size(); i++) {
			String paramBlock = paramBlockList.get(i);
			String type = null;
			String name = null;
			if (paramBlock.contains("getParameter") && paramBlock.contains("(") && paramBlock.contains(")")) {
				type = "String";
				String substringAfterLast = StringUtils.substringAfterLast(paramBlock, "getParameter");
				substringAfterLast = substringAfterLast.trim();
				substringAfterLast = StrUtil.removeSuffix(substringAfterLast, ";");
				substringAfterLast = substringAfterLast.trim();
				substringAfterLast = StrUtil.removePrefix(substringAfterLast, "(");
				substringAfterLast = StrUtil.removeSuffix(substringAfterLast, ")");
				substringAfterLast = substringAfterLast.trim();
				substringAfterLast = StrUtil.removePrefix(substringAfterLast, "\"");
				substringAfterLast = StrUtil.removeSuffix(substringAfterLast, "\"");
				name = substringAfterLast;
				Param param = new Param(type, name);
				list.add(param);
			}
		}
		return list;
	}

	private static List<Param> getListParam(String paramBlockStr, String[] exceptParamClass) {
		List<Param> list = new ArrayList<Param>();
		if ("".equals(paramBlockStr)) {
			return list;
		}
		String[] split = DocFileUtil.split(paramBlockStr);
		for (int i = 0; i < split.length; i++) {
			String pr = split[i];
			pr = pr.trim();
			String[] split2 = pr.split("\\s+");
			if (split2.length == 2) {
				String type = split2[0];
				String name = split2[1];
				Param param = new Param(type, name);
				if (ArrayUtil.contains(exceptParamClass, type)) {
					continue;
				}
				list.add(param);
			} else {
				throw new ParamException("参数不正确:" + pr);
			}
		}
		return list;
	}

	private static Pair<String, List<String>> getParamStr(List<String> readLines, int lineNum) {
		List<String> list = new ArrayList<String>();
		int stackSmall = 0;
		int stackBlock = 0;
		int index = lineNum;
		StringBuilder sb = new StringBuilder();
		int status = 0;
		String resultType = null;
		String methodName = null;
		String methodBlockStr = null;
		List<String> methodBlockList = new ArrayList<String>();
		while (index < readLines.size()) {
			String line = readLines.get(index++);
			if (status == 0) {
				int indexOf = line.indexOf("(");
				if (indexOf != -1) {
					String methodFull = StringUtils.substringBefore(line, "(");
					methodFull = StrUtil.removePrefix(methodFull.trim(), "public");
					methodFull = methodFull.trim();
					String[] split = methodFull.split("\\s+");
					resultType = split[0];
					methodName = split[1];
				} else {
					throw new RuntimeException("方法未找到");
				}
				status = 1;
				stackSmall += StrUtil.count(line, "(") - StrUtil.count(line, ")");
				stackBlock += StrUtil.count(line, "{") - StrUtil.count(line, "}");
				if (stackSmall == 0) {
					sb.append(line);
					methodBlockStr = sb.toString();
					sb = null;
				} else {
					sb.append(line);
				}
			} else {
				stackSmall += StrUtil.count(line, "(") - StrUtil.count(line, ")");
				stackBlock += StrUtil.count(line, "{") - StrUtil.count(line, "}");
				if (sb != null) {
					if (stackSmall > 0) {
						sb.append(line);
					} else {
						sb.append(line);
						methodBlockStr = sb.toString();
						sb = null;
					}
				}
				if (list != null) {
					if (stackBlock > 0) {
						list.add(line);
					} else {
						methodBlockList = list;
						list = null;
					}
				}
			}

		}
		Pair<String, List<String>> pair = Pair.of(methodBlockStr, methodBlockList);
		return pair;
	}

	private static String next(List<String> readLines, int lineNum) {
		if (lineNum >= 0 && lineNum < readLines.size() - 1) {
			return readLines.get(lineNum + 1);
		}
		return null;
	}

}
