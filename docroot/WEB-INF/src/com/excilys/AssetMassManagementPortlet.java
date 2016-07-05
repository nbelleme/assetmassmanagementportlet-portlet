package com.excilys;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetCategory;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Portlet implementation class AssetMassManagementPortlet
 */

public class AssetMassManagementPortlet extends MVCPortlet {
	private static final Log LOGGER = LogFactoryUtil.getLog(AssetMassManagementPortlet.class);
	
	//Liferay Default Asset Types
	private static final String BLOG_ENTRY_CLASSNAME = "com.liferay.portlet.blogs.model.BlogsEntry";
	private static final String BOOKMARKS_ENTRY_CLASSNAME = "com.liferay.portlet.bookmarks.model.BookmarksEntry";
	private static final String CALENDAR_EVENT_CLASSNAME = "com.liferay.portlet.calendar.model.CalendarBooking";
	private static final String WEB_CONTENT_ARTICLE_CLASSNAME = "com.liferay.portlet.journal.model.JournalArticle";
	
	//Liferay Assets types names
	private static final String BLOG_ENTRY_NAME = "asset-type-blog-entry";
	private static final String BOOKMARK_NAME = "asset-type-bookmark";
	private static final String CALENDAR_EVENT_NAME = "asset-type-calendar-event";
	private static final String WEB_CONTENT_NAME = "asset-type-web-content";
	
	public static Map<String, Map<Long,String>> assetsMap = new HashMap<String, Map<Long,String>>();
	Locale language;
	long groupID;
	
	@Override
	public void render(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {
		ThemeDisplay themeDisplay = (ThemeDisplay) request
				.getAttribute(WebKeys.THEME_DISPLAY);
		//get groupId and Locale to filter Data
		groupID = themeDisplay.getScopeGroupId();

		long[] groups = {groupID};
		language = themeDisplay.getLocale();
		
		AssetEntryQuery query = new AssetEntryQuery();
		query.setGroupIds(groups);
		
		//fill assetsMap with default assets type
		setAssetEntriesByDefaultTypes(query);
		
		//Get Others assets (custom types)
		setDLFileByType();
		
		request.setAttribute("assets", assetsMap);
		
		//Get Tags and categories
		request.setAttribute("categories", getCategories());
		
		

		super.render(request, response);
	}
	
	/**
	 * Fill <i>assetsMap</i> with Assets Entries sortes by types
	 * @param query
	 */
	private void setAssetEntriesByDefaultTypes(AssetEntryQuery query){
		//Get Blog entries
		assetsMap.put(BLOG_ENTRY_NAME, getAssetEntriesTitles(query,BLOG_ENTRY_CLASSNAME));
		
		//Get Bookmarks entries
		assetsMap.put(BOOKMARK_NAME, getAssetEntriesTitles(query,BOOKMARKS_ENTRY_CLASSNAME));
		
		//Get Calendar events //FIXME CalendarBooking
		assetsMap.put(CALENDAR_EVENT_NAME, getAssetEntriesTitles(query,CALENDAR_EVENT_CLASSNAME));
		
		//Get Web content articles 
		assetsMap.put(WEB_CONTENT_NAME, getAssetEntriesTitles(query,WEB_CONTENT_ARTICLE_CLASSNAME));
	}

	/**
	 * Return an ArrayList filled with the entries'title of type <i>classname</i>
	 * @param query query to pass at AssetEntryService for request
	 * @param className the type of wanted Asset entries
	 * @return an ArrayList of all entries titles instance of <i>className</i>
	 */
	private Map<Long,String> getAssetEntriesTitles(AssetEntryQuery query, String className) {
		List<AssetEntry> entries = null;
		Map<Long,String> entriesTitles = null;
		//Construct query
		query.setClassName(className);
		try {
			entries = AssetEntryLocalServiceUtil.getEntries(query);
			entriesTitles = new HashMap<>(entries.size());
			//Extract titles from asset entries
			for (AssetEntry entry : entries){
				entriesTitles.put(entry.getEntryId(),entry.getTitle(language));
			}
		} catch (SystemException e) {
			LOGGER.error("AssetMassManagementPortlet.getAssetEntriesTitles", e);
			throw new RuntimeException(e);
		}
		return entriesTitles;
	}

	/**
	 * Get all asset custom types (DLFileEntry)
	 * @return A Map in which every entry contains the ID of the type and its name
	 */
	private Map<Long, String> getDLFileEntryType(){
		List<DLFileEntryType> entriesTypes = null;
		Map<Long, String> types = null;
		try {
			entriesTypes = DLFileEntryTypeLocalServiceUtil.getDLFileEntryTypes(0, DLFileEntryTypeLocalServiceUtil.getDLFileEntryTypesCount());
			types = new HashMap<>(entriesTypes.size());
			for (DLFileEntryType type: entriesTypes){
				types.put(type.getFileEntryTypeId(), type.getName(language));
			}
		} catch (SystemException e) {
			LOGGER.error("AssetMassManagementPortlet.getDLFileEntryType", e);
			throw new RuntimeException(e);
		}
		return types;
	}
	
	/**
	 * Fill <i>assetsMap</i> with DLFileEntries sorted by types
	 */
	private void setDLFileByType(){
		Map<Long, String> filesTypes = getDLFileEntryType();
		Map<Long, String> filesNames=null;
		try {
			List<DLFileEntry> files = DLFileEntryLocalServiceUtil.getDLFileEntries(0, DLFileEntryLocalServiceUtil.getDLFileEntriesCount());
			//Create and put a list of files name for each type
			for (Map.Entry<Long, String> type : filesTypes.entrySet()){
				filesNames = new HashMap<>();
				for (DLFileEntry entry : files){
					if((entry.getFileEntryTypeId() == type.getKey()) && (entry.getGroupId() == groupID)){
						filesNames.put(entry.getFileEntryId(),entry.getTitle());
					} 
				}
				assetsMap.put(type.getValue(), filesNames);
			}
		} catch (SystemException e) {
			LOGGER.error("AssetMassManagementPortlet.setDLFileByType", e);
			throw new RuntimeException(e);
		}	
	}
	
	private Map<Long, String> getCategories(){
		List<AssetCategory> assetsCategories = null;
		Map<Long, String> categories = null;
		
		try {
			assetsCategories = AssetCategoryLocalServiceUtil.getCategories();
			categories = new HashMap<>(assetsCategories.size());
			for (AssetCategory cat : assetsCategories){
				categories.put(cat.getCategoryId(),cat.getTitle(language));
			}
		} catch (SystemException e) {
			LOGGER.error("AssetMassManagementPortlet.getCategories", e);
			throw new RuntimeException(e);
		}
		
		return categories;
	}
	
	//TODO get TagList
}
