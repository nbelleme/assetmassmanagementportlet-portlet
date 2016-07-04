<%@page import="com.liferay.portlet.journal.model.JournalArticle"%>
<%@page import="com.excilys.AssetMassManagementPortlet"%>
<%@ include file="/html/init.jsp"%>

<script src="https://code.jquery.com/jquery-3.0.0.min.js"
	integrity="sha256-JmvOoLtYsmqlsWxa7mDSLMwa6dZ9rrIdtrrVYRnDRH0="
	crossorigin="anonymous"></script>

<div id="container" class="row-fluid span8">
	<portlet:actionURL var="formUrl" name='formAction'></portlet:actionURL>
	<a href="<portlet:actionURL name="beamMe"></portlet:actionURL>">Beam
	me!</a>
	<aui:form action="<%=formUrl%>" name='form' method="post">
		<div id="assets-column" class="span7">
			<h3>Asset Type</h3>
			<div class="list-assets">
				<c:forEach var="i" begin="0" end="5">
					<div>
						<h2>Asset type ${i}</h2>
						<ul class="hidden">
							<c:forEach var="j" begin="0" end="5">
								<li><aui:input name="type-id" label="asset name"
										type="checkbox" value="${i}" /></li>
							</c:forEach>
						</ul>
					</div>
					<hr />
				</c:forEach>
			</div>
		</div>
		<div id="tags-column" class="span4">
			<div id="tags-box" class="row-fluid span12">
				<h3>Tags</h3>
				<ul class="list-scroll">
					<c:forEach var="i" begin="0" end="7">
						<li><aui:input name="tag-id" label="tag name" type="checkbox"
								value="${i}" /></li>
					</c:forEach>
				</ul>
			</div>
			<hr />
			<div id="categories-box" class="row-fluid span12">
				<h3>Categories</h3>
				<ul class="list-scroll">
					<c:forEach var="k" begin="0" end="8">
						<li><aui:input name="category-id" label="category name"
								type="checkbox" value="${i}" /></li>
					</c:forEach>
				</ul>
			</div>
		</div>
		<aui:button-row>
			<aui:button type="submit" value="save" onClick="submitForm();" />
			<aui:button value="cancel" />
		</aui:button-row>
	</aui:form>
</div>

