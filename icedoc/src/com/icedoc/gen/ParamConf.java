package com.icedoc.gen;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.xiaoleilu.hutool.setting.Setting;

public class ParamConf {
	public static Map<String, String> getParamNames(String path) {
		File confFile = new File(path);
		if (!confFile.exists()) {
			return new HashMap<String, String>();
		}
		Setting setting = new Setting(confFile, Charset.forName("UTF-8"), true);
		Map<String, String> map = new HashMap<String, String>();
		Set<Object> keySet = setting.keySet();
		for (Object object : keySet) {
			String key = object + "";
			String value = setting.getStr(key);
			map.put(key, value);
		}
		return map;
	}

	public static Map<String, String> getParamNames() {
		return ParamConf.getParamNames("src/param.txt");
	}
}
