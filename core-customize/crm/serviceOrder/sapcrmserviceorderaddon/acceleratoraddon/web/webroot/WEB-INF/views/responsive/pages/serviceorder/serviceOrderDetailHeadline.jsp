<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="back-link">
	<spring:url value="/my-account/service-orders"
		var="serviceOrderHistoryUrl" />
	<button type="button" class="serviceOrderTopBackBtn"
		data-back-to-orderlist="${serviceOrderHistoryUrl}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</button>
	<span class="label"><spring:theme
			code="text.sapcrmserviceorderaddon.serviceorder.title.details"
			arguments="${orderData.code}" text="Service Order Details" /> <c:if
			test="${orderData.description ne null}">
			<spring:theme
				code="text.sapcrmserviceorderaddon.serviceorder.description"
				arguments="${fn:escapeXml(orderData.description)}" text="|" />
		</c:if></span>
</div>
