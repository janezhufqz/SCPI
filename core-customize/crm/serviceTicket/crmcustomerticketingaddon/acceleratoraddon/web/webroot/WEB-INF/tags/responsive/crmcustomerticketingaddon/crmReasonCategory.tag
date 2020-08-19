<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%@ attribute name="requestForm" required="true"
	type="com.sap.hybris.crm.crmcustomerticketingaddon.forms.SapCrmSupportTicketForm"%>
<%@ attribute name="type" required="true" type="java.lang.String"%>
<%@ attribute name="ticketData" required="false"
	type="de.hybris.platform.customerticketingfacades.data.TicketData"%>
<div class="row customer-ticketing-body popupWindowCategory">
	</br>

	<c:if test="${not empty reasonCategories1}">
		<div class="form-group">
			<label class="control-label"
				for="text.account.supporttickets.createTicket.reasonCategory">
				<spring:theme
					code="text.account.supporttickets.createTicket.reasonCategory"
					text="Reason Category" />:
			</label>
			<table>
				<tr>
					<td><label class="control-label"
						for="text.account.supporttickets.createTicket.reasonCategory1">
							<spring:theme
								code="text.account.supporttickets.createTicket.reasonCategory1"
								text="Category 1" />:
					</label> <form:select path="reasonCategory1" cssClass="form-control"
							id="reasonCat_1" onchange="getReasonCategory(1,'${type}');">
							<c:if test="${empty ticketData.reasonCategoryMap['1'] }">
								<form:option selected="true" value="" />
							</c:if>
							<c:forEach var="reasonCategory1" items="${reasonCategories1}">
								<c:if
									test="${ticketData.reasonCategoryMap['1'] ne reasonCategory1.guid}">
									<form:option value="${reasonCategory1.guid}">
										<spring:message code="${reasonCategory1.name}" />
									</form:option>
								</c:if>
								<c:if
									test="${ticketData.reasonCategoryMap['1'] eq reasonCategory1.guid}">
									<form:option selected="selected"
										value="${reasonCategory1.guid}">
										<spring:message code="${reasonCategory1.name}" />
									</form:option>
								</c:if>
							</c:forEach>
						</form:select></td>
					<td><label class="control-label"
						for="text.account.supporttickets.createTicket.reasonCategory2">
							<spring:theme
								code="text.account.supporttickets.createTicket.reasonCategory2"
								text="Category 2" />:
					</label> <form:select path="reasonCategory2" cssClass="form-control"
							id="reasonCat_2" onchange="getReasonCategory(2,'${type}');">
							<c:if test="${empty ticketData.reasonCategoryMap['2'] }">
								<form:option selected="true" value="" />
							</c:if>
							<c:forEach var="reasonCategory2" items="${reasonCategories2}">
								<c:if
									test="${ticketData.reasonCategoryMap['2'] ne reasonCategory2.guid}">
									<form:option value="${reasonCategory2.guid}">
										<spring:message code="${reasonCategory2.name}" />
									</form:option>
								</c:if>
								<c:if
									test="${ticketData.reasonCategoryMap['2'] eq reasonCategory2.guid}">
									<form:option selected="selected"
										value="${reasonCategory2.guid}">
										<spring:message code="${reasonCategory2.name}" />
									</form:option>
								</c:if>
							</c:forEach>
						</form:select></td>
				</tr>
				<tr>
					<td><label class="control-label"
						for="text.account.supporttickets.createTicket.reasonCategory3">
							<spring:theme
								code="text.account.supporttickets.createTicket.reasonCategory3"
								text="Category 3" />:
					</label> <form:select path="reasonCategory3" cssClass="form-control"
							id="reasonCat_3" onchange="getReasonCategory(3,'${type}');">
							<c:if test="${empty ticketData.reasonCategoryMap['3'] }">
								<form:option selected="true" value="" />
							</c:if>
							<c:forEach var="reasonCategory3" items="${reasonCategories3}">
								<c:if
									test="${ticketData.reasonCategoryMap['3'] ne reasonCategory3.guid}">
									<form:option value="${reasonCategory3.guid}">
										<spring:message code="${reasonCategory3.name}" />
									</form:option>
								</c:if>
								<c:if
									test="${ticketData.reasonCategoryMap['3'] eq reasonCategory3.guid}">
									<form:option selected="selected"
										value="${reasonCategory3.guid}">
										<spring:message code="${reasonCategory3.name}" />
									</form:option>
								</c:if>
							</c:forEach>
						</form:select></td>
					<td><label class="control-label"
						for="text.account.supporttickets.createTicket.reasonCategory4">
							<spring:theme
								code="text.account.supporttickets.createTicket.reasonCategory4"
								text="Category 4" />:
					</label> <form:select path="reasonCategory4" cssClass="form-control"
							id="reasonCat_4" onchange="validateHierarchy(4);">
							<c:if test="${empty ticketData.reasonCategoryMap['4'] }">
								<form:option selected="true" value="" />
							</c:if>
							<c:forEach var="reasonCategory4" items="${reasonCategories4}">
								<c:if
									test="${ticketData.reasonCategoryMap['4'] ne reasonCategory4.guid}">
									<form:option value="${reasonCategory4.guid}">
										<spring:message code="${reasonCategory4.name}" />
									</form:option>
								</c:if>
								<c:if
									test="${ticketData.reasonCategoryMap['4'] eq reasonCategory4.guid}">
									<form:option selected="selected"
										value="${reasonCategory4.guid}">
										<spring:message code="${reasonCategory4.name}" />
									</form:option>
								</c:if>
							</c:forEach>
						</form:select></td>
				</tr>
			</table>


			<div id="customer-ticketing-buttons" class="form-actions">
				<a href="support-tickets" class="btn btn-default hideCatPopup"
					onclick="clearValues();"> <spring:theme
						code="text.account.supporttickets.reasonCat.cancel" text="Cancel" />
				</a>

				<ycommerce:testId code="supportTicket_create_button">
					<button class="btn btn-primary hideCatPopup"
						onclick="showSelectedValue();">
						<spring:theme code="text.account.supporttickets.reasonCat.OK"
							text="OK" />
					</button>
				</ycommerce:testId>
			</div>
		</div>
	</c:if>
</div>
