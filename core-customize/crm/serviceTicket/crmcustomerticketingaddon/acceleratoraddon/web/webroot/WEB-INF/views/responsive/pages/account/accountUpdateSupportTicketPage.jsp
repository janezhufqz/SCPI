<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<%@ taglib prefix="categories"
	tagdir="/WEB-INF/tags/addons/crmcustomerticketingaddon/responsive/crmcustomerticketingaddon"%>
<div class="account-section-header">
	<spring:theme code="text.account.supportTicket.updateTicket.heading"
		text="Request Customer Support" />
</div>
<div class="row customer-ticketing-body">
	<div class="col-md-8 col-xs-10">
		<div class="update-section">
			<form:form method="post" modelAttribute="sapCrmSupportTicketForm">

				<label class="control-label"
					for="text.account.supporttickets.createTicket.reasonCategory">
					<spring:theme
						code="text.account.supporttickets.createTicket.reasonCategory"
						text="Reason Category" />:
				</label>
				<categories:crmUpdateReasonCategory ticketData="${ticketData }" />
				<div id=reasonCategoryPopup " class="messagepop pop" tabindex="-1"
					role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					<categories:crmReasonCategory
						requestForm="${sapCrmSupportTicketForm}"
						ticketData="${ticketData }" type="${type}" />
				</div>

				<input id="currentTicketStatus" type="hidden"
					value="${ticketData.status.id}">
				<div class="form-group">
					<c:set var="sapCrmSupportTicketForm.subject"
						value="${ticketData.subject}" />
					<formElement:formInputBox
						idKey="text.account.supporttickets.createTicket.subject"
						labelKey="text.account.supporttickets.createTicket.subject"
						path="subject" inputCSS="text" mandatory="true" />

				</div>

				<c:if test="${not empty ticketData.associatedTo}">
					<div class="form-group">
						<label class="control-label"><spring:theme
								code="text.account.supporttickets.createTicket.associatedTo"
								text="Associated To" /> </label>
						<p class="form-control-static">${ticketData.associatedTo}</p>
					</div>
				</c:if>

				<label class="control-label"><spring:theme
						code="text.account.supporttickets.createTicket.ticketCategory"
						text="Category" /> </label>
				<form:hidden path="ticketCategory" value="${category}" />
				<form:select path="ticketCategory" cssClass="form-control"
					disabled="true">
					<form:option value="${category}" selected="true">
						<spring:message
							code="text.account.supporttickets.createTicket.ticketCategory.${category}" />
					</form:option>
				</form:select>

				<label class="control-label"><spring:theme
						code="text.account.supporttickets.createTicket.ticketPriority"
						text="Priority" /> </label>
				<form:select path="ticketPriority" cssClass="form-control">
					<c:if test="${not empty ticketData.ticketPriority }">
						<form:option value="${ticketData.ticketPriority}">
							<spring:message
								code="text.account.supporttickets.createTicket.ticketPriority.${fn:toUpperCase(ticketData.ticketPriority)}" />
						</form:option>
					</c:if>
					<c:forEach var="priorty" items="${priorities}">
						<c:if test="${ticketData.ticketPriority ne priorty}">
							<form:option value="${priorty}">
								<spring:message
									code="text.account.supporttickets.createTicket.ticketPriority.${priorty}" />
							</form:option>
						</c:if>
					</c:forEach>
				</form:select>
		</div>

		<div class="account-section-header">
			<spring:theme code="text.account.supporttickets.messages"
				text="Messages" />
		</div>
		<div class="cts-msg-history row">
			<div class="col-md-12 col-md-offset-0 col-sm-9 col-sm-offset-0">
				<c:if test="${ticketData.status.id ne 'CLOSED'}">
					<form:hidden path="id" value="${ticketData.id}" />
					<formElement:formTextArea idKey="message"
						labelKey="text.account.supporttickets.createTicket.message"
						path="message" areaCSS="form-control" labelCSS="control-label" />
				</c:if>
				<div class="form-group">
					<label for="text.account.supportTicket.updateTicket.status"
						class="control-label"><spring:message
							code="text.account.supportTicket.updateTicket.status"
							text="Status" /> </label>
					<form:select path="status" cssClass="form-control">
						<form:option value="${ticketData.status.id}">
							<spring:message
								code="ticketstatus.${fn:toUpperCase(ticketData.status.id)}" /> (<spring:message
								code="text.account.supporttickets.currentStatus"
								text="Current Status" />)</form:option>
						<c:forEach items="${ticketData.availableStatusTransitions}"
							var="status">
							<form:option value="${status.id}">
								<spring:message code="ticketstatus.box.${status.id}" />
							</form:option>
						</c:forEach>
					</form:select>
				</div>


				<label class="control-label"><spring:message
						code="text.account.supporttickets.createTicket.message.history" />
					<div id="customer-history-message" class="form-group"
						style="height: 100%; overflow: scroll;">
						<c:choose>
							<c:when test="${not empty ticketData.messageHistory}">
								<div class="form-group">
									<p class="form-control-static">${ticketData.messageHistory}</p>

								</div>
							</c:when>
							<c:otherwise>
								<c:forEach items="${ticketData.ticketEvents}"
									var="ticketEventData">
									<c:choose>
										<c:when test="${ticketEventData.author eq 'Customer Service'}">
											<c:set var="cssLine1" scope="page"
												value="cts-msg-history-item cts-msg-history-item-agent col-md-7 col-sm-9 ct-msg-visible" />
											<c:set var="cssLine2" scope="page" value="well" />
										</c:when>
										<c:otherwise>
											<c:set var="cssLine1" scope="page"
												value="cts-msg-history-item cts-msg-history-item-customer col-md-7 col-md-offset-5 col-sm-9 col-sm-offset-3 ct-msg-visible" />
											<c:set var="cssLine2" scope="page" value="well well-primary" />
										</c:otherwise>
									</c:choose>
									<div class="${cssLine1}">
										<div class="${cssLine2}">
											<div class="cts-msg-history-item-info">
												<span class="author">${fn:escapeXml(ticketEventData.author)}</span>
												${fn:escapeXml(ticketEventData.startDateTime)}
											</div>
											<div class="cts-mgs-history-item-msg">
												${fn:escapeXml(ticketEventData.text)}</div>
										</div>
									</div>
								</c:forEach>
							</c:otherwise>
						</c:choose>
					</div>
			</div>
		</div>
		<div id="customer-ticketing-buttons" class="form-actions">
			<div class="buttons">
				<a href="${backlinkurl}" class="btn btn-default"> <spring:theme
						code="text.account.supporttickets.createTicket.back" text="Cancel" />
				</a>
					<ycommerce:testId code="supportTicket_update_button">

						<button class="btn btn-primary" id="updateCRMTicket" type="submit">
							<spring:theme
								code="text.account.supporttickets.createTicket.submit"
								text="Submit" />
						</button>
					</ycommerce:testId>
			</div>



		</div>
		</form:form>

	</div>
</div>






