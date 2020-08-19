<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%@ taglib prefix="categories"
	tagdir="/WEB-INF/tags/addons/crmcustomerticketingaddon/responsive/crmcustomerticketingaddon"%>

<div class="account-section-header">
	<spring:theme code="text.account.createticket" text="Create Ticket" />
</div>
<div class="row customer-ticketing-body">
	<div class="col-md-8 col-xs-10">
		<div class="account-section-content	 account-section-content-small ">
			<form:form method="post" modelAttribute="sapCrmSupportTicketForm">
				<br>

				<label class="control-label"
					for="text.account.supporttickets.createTicket.reasonCategory">
					<spring:theme
						code="text.account.supporttickets.createTicket.reasonCategory"
						text="Reason Category" />:
				</label>
				<select id="reasonCatLabel" class="form-control selectedReasonCat"></select>
				<div id=reasonCategoryPopup " class="messagepop pop" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel"
					data-position-to="window" aria-hidden="true">
					<categories:crmReasonCategory
						requestForm="${sapCrmSupportTicketForm}"
						ticketData="${ticketData }" type="${type}" />
				</div>
				<c:forEach
					items="${sapCrmSupportTicketForm.ticketRelatedObjectData}"
					varStatus="vs">
					<form:hidden path="ticketRelatedObjectData[${vs.index}].objectType" />
					<form:hidden path="ticketRelatedObjectData[${vs.index}].objectId" />
					<form:hidden path="ticketRelatedObjectData[${vs.index}].objectGuid" />
					<form:hidden path="ticketRelatedObjectData[${vs.index}].objectUrl" />
				</c:forEach>

				<formElement:formInputBox
					idKey="text.account.supporttickets.createTicket.subject"
					labelKey="text.account.supporttickets.createTicket.subject"
					path="subject" inputCSS="text" mandatory="true" />
				<label class="control-label"
					for="text.account.supporttickets.createTicket.ticketCategory">
					<spring:theme
						code="text.account.supporttickets.createTicket.ticketCategory"
						text="Category" />:
				</label>
				<form:hidden path="ticketCategory" value="${category}" />
				<form:select path="ticketCategory" cssClass="form-control"
					disabled="true">
					<form:option value="${category}" selected="true">
						<spring:message
							code="text.account.supporttickets.createTicket.ticketCategory.${category}" />
					</form:option>
				</form:select>
				</br>

				<!-- Ticket Priority -->
				<c:if test="${not empty priorities}">
					<div class="form-group">
						<label class="control-label"><spring:theme
								code="text.account.supporttickets.createTicket.ticketPriority"
								text="Priority" />:</label>
						<form:select path="ticketPriority" cssClass="form-control">
							<form:option selected="true" value=" " />

							<c:forEach var="priorty" items="${priorities}">
								<form:option value="${priorty}">
									<spring:message
										code="text.account.supporttickets.createTicket.ticketPriority.${priorty}" />
								</form:option>
							</c:forEach>
						</form:select>
					</div>
				</c:if>
				<c:if
					test="${fn:length(sapCrmSupportTicketForm.ticketRelatedObjectData) == 0}">
					<c:if test="${not empty associatedObjects}">
						<div class="form-group">
							<label class="control-label"
								for="text.account.supporttickets.createTicket.associatedTo.option1">
								<spring:theme
									code="text.account.supporttickets.createTicket.associatedTo"
									text="Associated To" />:
							</label>

							<form:select path="associatedTo" cssClass="form-control">
								<option><spring:theme
										code="text.account.supporttickets.createTicket.associatedTo.option1"
										text="Please select"></spring:theme></option>
								<c:forEach var="associatedMap" items="${associatedObjects}">
									<c:forEach var="associatedItem" items="${associatedMap.value}">
										<form:option
											value="${associatedMap.key}=${associatedItem.code}">${associatedMap.key}: ${associatedItem.code}; Updated: <fmt:formatDate
												pattern="dd/MM/yy" value="${associatedItem.modifiedtime}" />
										</form:option>
									</c:forEach>
								</c:forEach>
							</form:select>
						</div>
					</c:if>
				</c:if>
				<formElement:formTextArea
					idKey="text.account.supporttickets.createTicket.message"
					labelKey="text.account.supporttickets.createTicket.message"
					path="message" mandatory="true" areaCSS="form-control"
					labelCSS="control-label" />

				<div id="customer-ticketing-buttons" class="form-actions">
					<a href="crm-support-tickets" class="btn btn-default"> <spring:theme
							code="text.account.supporttickets.createTicket.back"
							text="Cancel" />
					</a>

					<ycommerce:testId code="supportTicket_create_button">
						<button class="btn btn-primary" type="submit" id="addCRMTicket">
							<spring:theme
								code="text.account.supporttickets.createTicket.submit"
								text="Submit" />
						</button>
					</ycommerce:testId>
				</div>
			</form:form>
		</div>
	</div>
</div>
