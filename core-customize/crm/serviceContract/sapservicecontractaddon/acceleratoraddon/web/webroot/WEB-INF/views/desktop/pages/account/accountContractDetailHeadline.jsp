<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="headline">
	<c:if test="${empty backendError}">
		<spring:theme code="text.account.contract.title.details" />
	</c:if>
	<c:if test="${not empty backendError}">
		<p class="error">
			<spring:message code="${fn:toUpperCase(backendError)}" />
		</p>
	</c:if>
</div>
