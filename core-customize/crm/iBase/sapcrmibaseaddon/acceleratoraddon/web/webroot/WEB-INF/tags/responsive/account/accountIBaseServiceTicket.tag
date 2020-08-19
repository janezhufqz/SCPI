<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="ibasenumber" required="true" type="java.lang.String"%>
<%@ attribute name="ibaseguid" required="true" type="java.lang.String"%>
<%@ attribute name="componentnumber" required="false"
	type="java.lang.String"%>
<%@ attribute name="componentguid" required="false"
	type="java.lang.String"%>

<c:choose>
	<c:when test="${not empty componentnumber && not empty componentguid}">
		<c:set var="serviceticketlink"
			value="${serviceticketurl}?${serviceticketibaseid}=${ibasenumber}&${serviceticketibaseguid}=${ibaseguid}&${serviceticketcomponentid}=${componentnumber}&${serviceticketcomponentguid}=${componentguid}" />
	</c:when>
	<c:otherwise>
		<c:set var="serviceticketlink"
			value="${serviceticketurl}?${serviceticketibaseid}=${ibasenumber}&${serviceticketibaseguid}=${ibaseguid}" />
	</c:otherwise>
</c:choose>

<c:url value="${serviceticketlink}" var="serviceTicktUrl" />

<a href="${serviceTicktUrl}"
	class="service-ticket-btn <c:if test="${not showserviceticketbutton}">service-ticket-disable</c:if>"
	<c:if test="${not showserviceticketbutton}">title="<spring:theme code="text.installedBase.serviceTicket.disabledmsg" arguments="${serviceticketextensionname}" />"</c:if>>
	<spring:theme code="text.installedBase.serviceticketbtn" />
</a>

