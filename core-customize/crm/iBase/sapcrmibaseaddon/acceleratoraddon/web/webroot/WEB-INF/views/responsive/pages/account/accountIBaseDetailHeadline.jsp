<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-section">
	<div class="account-section-content">
		<div class="back-link">
			<spring:url value="/my-company/installed-bases"
				var="installedBasesUrl" htmlEscape="false" />
			<a href="${installedBasesUrl}"> <span
				class="glyphicon glyphicon-chevron-left"></span>
			</a> <span class="label"> <spring:theme
					code="text.installedBase.title" />
			</span>
		</div>
<!-- Enclosing braces of  ibase details headline are in accountIBaseDetailItems.jsp -->
