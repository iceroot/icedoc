package com.icedoc.doc;

import java.util.List;

public class Doc {
	private String index;
	private String name;
	private String url;
	private String type;
	private List<Params> params;
	private List<Return> returns;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Params> getParams() {
		return params;
	}

	public void setParams(List<Params> params) {
		this.params = params;
	}

	public List<Return> getReturns() {
		return returns;
	}

	public void setReturns(List<Return> returns) {
		this.returns = returns;
	}

}
