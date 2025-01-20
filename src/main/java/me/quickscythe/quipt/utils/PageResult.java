package me.quickscythe.quipt.utils;

import java.util.List;

public class PageResult {
	public List<Object> pages;
	public int page;
	public int maxPages;

	public PageResult(List<Object> pages, int page, int maxPages) {
		this.pages = pages;
		this.page = page;
		this.maxPages = maxPages;
	}
}