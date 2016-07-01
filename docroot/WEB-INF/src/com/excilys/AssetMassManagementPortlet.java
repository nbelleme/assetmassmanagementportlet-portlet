package com.excilys;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portlet.asset.service.AssetCategoryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetTagLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;

import java.io.IOException;
import java.util.List;

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

	@Override
	public void processAction(ActionRequest actionRequest,
			ActionResponse actionResponse) throws IOException, PortletException {

		try {
			List<JournalArticle> articles = JournalArticleLocalServiceUtil
					.getArticles();
			for (JournalArticle article : articles) {
				System.out.println(article.getTitle());
				System.out.println(article.getId());
			}

			System.out.println("Count Categories : "
					+ AssetCategoryLocalServiceUtil.getAssetCategoriesCount());
			System.out.println("Count Articles " + articles.size());
			System.out.println("Count Tags : "
					+ AssetTagLocalServiceUtil.getAssetTagsCount());
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}
