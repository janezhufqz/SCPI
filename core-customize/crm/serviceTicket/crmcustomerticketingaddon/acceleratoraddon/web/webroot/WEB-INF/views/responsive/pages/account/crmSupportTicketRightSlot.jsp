<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<c:if test="${fn:length(ticketData.relatedObjects) > 0}">
	<div class="account-section-right-header">
		<spring:theme code="text.account.supporttickets.referencobject"
			text="Reference Object" />

	</div>
	<div class="row customer-ticketing-body">
		<div class="col-md-8 col-xs-10">


			<c:forEach var="ticketRelatedObjectData"
				items="${ticketData.relatedObjects}" varStatus="loopCounter">

				<label
					for="text.account.supporttickets.createTicket.${ticketRelatedObjectData.objectType}"
					class="control-label"><spring:message
						code="text.account.supporttickets.createTicket.${ticketRelatedObjectData.objectType}"
						text="${ticketRelatedObjectData.objectType}" /> </label>
				<c:choose>
					<c:when test="${not empty ticketRelatedObjectData.objectGuid}">
						<c:url
							value="${ticketRelatedObjectData.objectUrl}${ticketRelatedObjectData.objectGuid}"
							var="relObjecturl" />
						<a href="${relObjecturl}"><p class="form-control-static">${ticketRelatedObjectData.objectGuid}</p></a>
					</c:when>
					<c:otherwise>
						<c:url
							value="${ticketRelatedObjectData.objectUrl}${ticketRelatedObjectData.objectId}"
							var="relObjecturl" />
						<a href="${relObjecturl}"><p class="form-control-static">${ticketRelatedObjectData.objectId}</p></a>
					</c:otherwise>
				</c:choose>
			</c:forEach>

		</div>
	</div>
</c:if>
