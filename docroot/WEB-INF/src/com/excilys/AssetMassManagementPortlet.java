package com.excilys;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

/**
 * Portlet implementation class AssetMassManagementPortlet
 */
/*
 * Taggable assets : 
 * - Web Content (JournalArticle) 
 * - Blog entries 
 * - Documents And Media 
 * - Post 
 * - Threads 
 * - Wiki Page
 */
public class AssetMassManagementPortlet extends MVCPortlet {
	//Liferay Default Asset Types
	private static String BLOG_ENTRY_CLASSNAME = "com.liferay.portlet.blogs.model.BlogsEntry";
	private static String BOOKMARKS_ENTRY_CLASSNAME = "com.liferay.portlet.bookmarks.model.BookmarksEntry";
	private static String CALENDAR_EVENT_CLASSNAME = "com.liferay.portlet.calendar.model.CalEvent";
	private static String WEB_CONTENT_ARTICLE_CLASSNAME = "com.liferay.portlet.journal.model.JournalArticle";
	private static String DL_FILE_CLASSNAME = "com.liferay.portlet.documentlibrary.model.DLFileEntry";
	
	public static Map<String, List<AssetEntry>> assetsMap = new HashMap<String, List<AssetEntry>>();

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);

		long groupId = themeDisplay.getScopeGroupId();
		
		AssetEntryQuery query = new AssetEntryQuery();
		long[] groups = {groupId};
		System.err.println("processAction : GET ASSETS BY TYPES");
		//Get Blog entries
		query.setGroupIds(groups);
		query.setClassName(BLOG_ENTRY_CLASSNAME);
		assetsMap.put(BLOG_ENTRY_CLASSNAME, getAssetEntries(query));
		System.out.println(BLOG_ENTRY_CLASSNAME + ".size = " + assetsMap.get(BLOG_ENTRY_CLASSNAME).size());//FIXME
		
		//Get Bookmarks entries
		query.setClassName(BOOKMARKS_ENTRY_CLASSNAME);
		assetsMap.put(BOOKMARKS_ENTRY_CLASSNAME, getAssetEntries(query));
		System.out.println(BOOKMARKS_ENTRY_CLASSNAME + ".size = " + assetsMap.get(BOOKMARKS_ENTRY_CLASSNAME).size());//FIXME
		
		//Get Calendar events
		query.setClassName(CALENDAR_EVENT_CLASSNAME);
		assetsMap.put(CALENDAR_EVENT_CLASSNAME, getAssetEntries(query));
		System.out.println(CALENDAR_EVENT_CLASSNAME + ".size = " + assetsMap.get(CALENDAR_EVENT_CLASSNAME).size());//FIXME
		
		//Get Web content articles 
		query.setClassName(WEB_CONTENT_ARTICLE_CLASSNAME);
		assetsMap.put(WEB_CONTENT_ARTICLE_CLASSNAME, getAssetEntries(query));
		System.out.println(WEB_CONTENT_ARTICLE_CLASSNAME + ".size = " + assetsMap.get(WEB_CONTENT_ARTICLE_CLASSNAME).size());
		
		//Get Others assets //FIXME
		query.setClassName(DL_FILE_CLASSNAME);
		assetsMap.put(DL_FILE_CLASSNAME, getAssetEntries(query));
		System.out.println(DL_FILE_CLASSNAME + ".size = " + assetsMap.get(DL_FILE_CLASSNAME).size());

		actionRequest.setAttribute("assets", assetsMap);
	}



	private List<AssetEntry> getAssetEntries(AssetEntryQuery query){
		List<AssetEntry> entries = null;
		try {
			entries = AssetEntryLocalServiceUtil.getEntries(query);
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //FIXME
		}
		
		return entries;
	}
}
