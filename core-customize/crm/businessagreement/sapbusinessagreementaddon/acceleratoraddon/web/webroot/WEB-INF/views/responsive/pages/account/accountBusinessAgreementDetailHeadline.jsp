<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/my-company/business-agreements" var="agreementHistoryUrl" htmlEscape="false"/>
<common:headline url="${agreementHistoryUrl}" labelKey="text.account.businessagreement.title.details" />
