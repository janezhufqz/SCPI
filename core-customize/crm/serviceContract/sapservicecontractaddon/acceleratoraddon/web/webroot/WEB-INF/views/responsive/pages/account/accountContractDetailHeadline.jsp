<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<spring:url value="/my-account/service-contract-detail/renew/"
	var="contractRenewalUrl" />
<spring:url value="/my-account/service-contract-detail/terminate/"
	var="contractTerminateUrl" />
<spring:url value="/my-account/service-contracts"
	var="backlinkurl" />
<c:if test="${empty backendError}">
	<spring:url value="/my-account/service-contracts"
		var="serviceContractUrl" />
	<button onclick="location.href='${backlinkurl}'" type="button"
		class="btn btn-default" id="contractTopBackBtn">
		<span class="glyphicon glyphicon-chevron-left"></span>
		<spring:theme code="text.account.contract.back.btn" text=" Back" />
	</button>
	<div class="account-section-header contract-detail-header">
		<div class="col-sm-10 col-md-11">
			<spring:theme code="text.account.contract.title.details"
				text="Contract Details" />
		</div>
		<div class="col-xs-3 col-sm-2 col-md-1">
			<c:choose>
				<c:when test="${not empty contractData.isRenewable}">
					<c:choose>
						<c:when test="${contractData.isRenewable eq true }">

							<div class="status" style="word-wrap: break-word;">

								<form:form id="renewContractForm" class="contractEditForm" action="${contractRenewalUrl}"
									method="post">
									<input type="hidden" name="contractId"
										value="${contractData.contractId}" />
									<input type="hidden" name="contractGuid"
										value="${contractData.contractGuid}" />
									<button type="submit" class="btn btn-primary contractbtn-block">
										<spring:theme code="text.account.servicecontract.renew"
											text="Renew" />
									</button>
								</form:form>
							</div>

						</c:when>
						<c:otherwise>

							<div class="status" style="word-wrap: break-word;">
								<button type="submit"
									class="btn btn-primary contractbtn-block contractButtonDisabled">
									<spring:theme code="text.account.servicecontract.renew"
										text="Renew" />
								</button>
							</div>

						</c:otherwise>
					</c:choose>
				</c:when>
				<c:otherwise>
					<div class="status" style="word-wrap: break-word;">
						<button type="submit"
							class="btn btn-primary btn-block contractButtonDisabled">
							<spring:theme code="text.account.servicecontract.renew"
								text="Renew" />
						</button>

					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</c:if>
<c:if test="${not empty backendError}">
	<p class="error">
		<spring:message code="${fn:toUpperCase(backendError)}" />
	</p>
</c:if>
