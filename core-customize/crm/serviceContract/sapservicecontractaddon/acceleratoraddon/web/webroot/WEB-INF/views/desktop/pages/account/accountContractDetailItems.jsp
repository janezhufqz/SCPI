<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/desktop/contract"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<spring:url value="/my-account/service-contract/" var="ordersUrl" />

<c:if test="${empty backendError}">
	<div>
		<contract:contractItemDetails contractData="${contractData}" />
	</div>
</c:if>
