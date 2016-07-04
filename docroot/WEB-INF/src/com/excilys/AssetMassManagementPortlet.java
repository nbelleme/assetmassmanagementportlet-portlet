package com.excilys;

import com.liferay.portal.DuplicateLockException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.persistence.AssetEntryQuery;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryType;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalService;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryTypeLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
	private static String CALENDAR_EVENT_CLASSNAME = "com.liferay.portlet.calendar.model.CalendarBooking";
	private static String WEB_CONTENT_ARTICLE_CLASSNAME = "com.liferay.portlet.journal.model.JournalArticle";
	private static String DL_FILE_CLASSNAME = "com.liferay.portlet.documentlibrary.model.DLFileEntry";
	
	//Liferay Assets types names
	private static String BLOG_ENTRY_NAME = "asset-type-blog-entry";
	private static String BOOKMARK_NAME = "asset-type-bookmark";
	private static String CALENDAR_EVENT_NAME = "asset-type-calendar-event";
	private static String WEB_CONTENT_NAME = "asset-type-web-content";
	
	public static Map<String, List<String>> assetsMap = new HashMap<String, List<String>>();
	Locale language;
	long groupID;
	
	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {
		ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest
				.getAttribute(WebKeys.THEME_DISPLAY);
		
		//Get groupId and Locale to filter data
		groupID = themeDisplay.getScopeGroupId();
		long[] groups = {groupID};
		language = themeDisplay.getLocale();
		
		AssetEntryQuery query = new AssetEntryQuery();
		query.setGroupIds(groups);
		
		System.err.println("processAction : GET ASSETS BY TYPES");//FIXME
		
		//Get Blog entries
		assetsMap.put(BLOG_ENTRY_NAME, getAssetEntriesTitles(query,BLOG_ENTRY_CLASSNAME));
		System.out.println(BLOG_ENTRY_NAME + ".size = " + assetsMap.get(BLOG_ENTRY_NAME).size());//FIXME
		
		//Get Bookmarks entries
		assetsMap.put(BOOKMARK_NAME, getAssetEntriesTitles(query,BOOKMARKS_ENTRY_CLASSNAME));
		System.out.println(BOOKMARK_NAME + ".size = " + assetsMap.get(BOOKMARK_NAME).size());//FIXME
		
		//Get Calendar events //FIXME CalendarBooking
		assetsMap.put(CALENDAR_EVENT_NAME, getAssetEntriesTitles(query,CALENDAR_EVENT_CLASSNAME));
		System.out.println(CALENDAR_EVENT_NAME + ".size = " + assetsMap.get(CALENDAR_EVENT_NAME).size());//FIXME
		
		//Get Web content articles 
		assetsMap.put(WEB_CONTENT_NAME, getAssetEntriesTitles(query,WEB_CONTENT_ARTICLE_CLASSNAME));
		System.out.println(WEB_CONTENT_NAME + ".size = " + assetsMap.get(WEB_CONTENT_NAME).size());//FIXME
		
		//Get Others assets //FIXME
		System.out.println(DL_FILE_CLASSNAME + ".size = " + getAssetEntriesTitles(query,DL_FILE_CLASSNAME).size());//FIXME

		//getDLFileEntryType();//FIXME
		setDLFileByType();//FIXME
		
		actionRequest.setAttribute("assets", assetsMap);
	}


	/**
	 * Return an ArrayList filled with the entries'title of type <i>classname</i>
	 * @param query query to pass at AssetEntryService for request
	 * @param className the type of wanted Asset entries
	 * @return an ArrayList of all entries titles instance of <i>className</i>
	 */
	private List<String> getAssetEntriesTitles(AssetEntryQuery query, String className){
		List<AssetEntry> entries = null;
		List<String> entriesTitles = null;
		//Construct query
		query.setClassName(className);
		try {
			entries = AssetEntryLocalServiceUtil.getEntries(query);
			entriesTitles = new ArrayList<>(entries.size());
			//Extract titles from asset entries
			for (AssetEntry entry : entries){
				entriesTitles.add(entry.getTitle(language));
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); //FIXME
		}
		
		return entriesTitles;
	}

	//TODO Javadoc
	private Map<Long, String> getDLFileEntryType(){
		List<DLFileEntryType> entriesTypes = null;
		Map<Long, String> types = null;
		try {
			entriesTypes = DLFileEntryTypeLocalServiceUtil.getDLFileEntryTypes(0, DLFileEntryTypeLocalServiceUtil.getDLFileEntryTypesCount());
			types = new HashMap<>(entriesTypes.size());
			
			for (DLFileEntryType type: entriesTypes){
				types.put(type.getFileEntryTypeId(), type.getName(language));
				System.out.println("TYPE = "+type.getName(language));//FIXME
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return types;
	}
	
	//TODO Javadoc
	private void setDLFileByType(){
		Map<Long, String> filesTypes = getDLFileEntryType();
		List<String> filesNames=null;
		
		try {
			List<DLFileEntry> files = DLFileEntryLocalServiceUtil.getDLFileEntries(0, DLFileEntryLocalServiceUtil.getDLFileEntriesCount());
			//Create and put a list of files name for each type
			for (Map.Entry<Long, String> type : filesTypes.entrySet()){
				filesNames = new ArrayList<>();
				for (DLFileEntry entry : files){
					if((entry.getFileEntryTypeId() == type.getKey()) && (entry.getGroupId() == groupID)){
						filesNames.add(entry.getTitle());
					} 
				}
				assetsMap.put(type.getValue(), filesNames);
				System.err.println("DLFile, filesNames for "+type.getValue()+ " has length of "+filesNames.size());
			}
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	//TODO get TagList
	//TODO get CategoriesList
}
