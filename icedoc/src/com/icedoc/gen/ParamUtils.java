package com.icedoc.gen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.icedoc.doc.Doc;
import com.icedoc.doc.Docu;
import com.icedoc.doc.Params;
import com.icedoc.doc.Return;

public class ParamUtils {

	public static Map<String, Object> map(Docu docu) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", docu.getTitle());
		map.put("version", docu.getVersion());
		map.put("author", docu.getAuthor());
		map.put("date", docu.getDate());
		map.put("time", getTime());
		map.put("docList", doc2map(docu.getDocList()));
		return map;
	}

	private static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(new Date());
	}

	private static List<Map<String, Object>> doc2map(List<Doc> docList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (docList == null) {
			return new ArrayList<Map<String, Object>>();
		}
		for (int i = 0; i < docList.size(); i++) {
			Doc doc = docList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("index", doc.getIndex());
			map.put("name", doc.getName());
			map.put("type", doc.getType());
			map.put("url", doc.getUrl());
			map.put("params", doc2mapParams(doc.getParams()));
			map.put("returns", doc2mapReturns(doc.getReturns()));
			list.add(map);
		}
		return list;
	}

	private static List<Map<String, Object>> doc2mapParams(List<Params> params) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (params == null || params.size() == 0) {
			return defaultParams();
		}
		for (int i = 0; i < params.size(); i++) {
			Params param = params.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("index", param.getIndex());
			map.put("name", param.getName());
			map.put("code", param.getCode());
			map.put("remark", param.getRemark());
			map.put("need", param.getNeed());
			list.add(map);
		}
		return list;
	}

	private static List<Map<String, Object>> doc2mapReturns(List<Return> returns) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (returns == null || returns.size() == 0) {
			return defaultReturns();
		}
		for (int i = 0; i < returns.size(); i++) {
			Return ret = returns.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("index", ret.getIndex());
			map.put("name", ret.getName());
			map.put("type", ret.getType());
			map.put("code", ret.getCode());
			list.add(map);
		}
		return list;
	}

	private static List<Map<String, Object>> defaultParams() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("index", "1");
		map.put("name", "");
		map.put("code", "无");
		map.put("need", "");
		map.put("remark", "");
		list.add(map);
		return list;
	}

	private static List<Map<String, Object>> defaultReturns() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("index", "1");
		map.put("name", "");
		map.put("type", "");
		map.put("code", "无");
		list.add(map);
		return list;
	}

	public static List<Return> str2ReturnList(String str) {
		if (StringUtils.isBlank(str)) {
			return returnListNull();
		} else {
			String basePath = DocContext.getBasePath();
			String packagePath = str.replace(".", "/");
			String classFullPath = basePath + "/" + packagePath + ".java";
			ReturnPojo resolve = PojoResolve.resolve(classFullPath);
			return returnPojoCreate(resolve);
		}
	}

	private static List<Return> returnPojoCreate(ReturnPojo resolve) {
		List<Return> list = new ArrayList<Return>();
		List<Param> fields = resolve.getFields();
		if (fields == null) {
			return returnListNull();
		} else {
			for (int i = 0; i < fields.size(); i++) {
				Param param = fields.get(i);
				String name = param.getName();
				String type = param.getType();
				type = typeConvrt(type);
				String comment = param.getComment();
				Return returnx = new Return();
				returnx.setCode(name);
				returnx.setIndex(i + 1 + "");
				returnx.setName(comment);
				returnx.setType(type);
				list.add(returnx);
			}
		}
		return list;
	}

	private static String typeConvrt(String type) {
		if (type.contains("<")) {
			type = StringUtils.substringBefore(type, "<");
			type = type.trim();
		} else if (type.contains("[") && type.contains("]")) {
			return "array";
		}
		return type;
	}

	public static List<Return> returnListNull() {
		List<Return> list = new ArrayList<Return>();
		Return returnx = new Return();
		returnx.setCode("");
		returnx.setIndex("1");
		returnx.setName("");
		returnx.setType("");
		list.add(returnx);
		return list;
	}

}
