<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%@ attribute name="ticketData" required="false"
	type="de.hybris.platform.customerticketingfacades.data.TicketData"%>

<c:choose>
	<c:when test="${not empty ticketData.reasonCategoryMap['4'] }">
		<select id="reasonCatLabel" class="form-control selectedReasonCat">
			<option value="${ticketData.reasonCategoryMap['4']}">${ticketData.selectedReasonCat}</option>
		</select>
	</c:when>
	<c:when test="${not empty ticketData.reasonCategoryMap['3'] }">
		<select id="reasonCatLabel" class="form-control selectedReasonCat">
			<option value="${ticketData.reasonCategoryMap['3']}">${ticketData.selectedReasonCat}</option>
		</select>
	</c:when>
	<c:when test="${not empty ticketData.reasonCategoryMap['2'] }">
		<select id="reasonCatLabel" class="form-control selectedReasonCat">
			<option value="${ticketData.reasonCategoryMap['2']}">${ticketData.selectedReasonCat}</option>
		</select>
	</c:when>
	<c:otherwise>
		<select id="reasonCatLabel" class="form-control selectedReasonCat">
			<option value="${ticketData.reasonCategoryMap['1']}">${ticketData.selectedReasonCat}</option>
		</select>
	</c:otherwise>
</c:choose>
