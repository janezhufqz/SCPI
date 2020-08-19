<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:url value="/my-account/service-contract/renew/"
	var="contractRenewalUrl" />
<spring:url value="/my-account/service-contract/terminate/"
	var="contractTerminateUrl" />
<div class="headline">
	<spring:theme code="text.account.contractHistory.viewContracts"
		text="View your contracts" />
</div>
<c:if test="${empty backendError}">
	<c:if test="${not empty searchPageData.results}">
		<nav:pagination top="true" supportShowPaged="${isShowPageAllowed}"
			supportShowAll="${isShowAllAllowed}"
			searchPageData="${searchPageData}"
			searchUrl="/my-account/service-contracts?sort=${searchPageData.pagination.sort}"
			msgKey="text.account.contractHistory.page"
			numberPagesShown="${numberPagesShown}" />
		<table class="serviceContractsTable">
			<thead>
				<tr>
					<th id="header2"><spring:theme code="text.account.contract.id"
							text="Contract ID" /></th>
					<th id="header4"><spring:theme
							code="text.account.contract.description" text="Description" /></th>
					<th id="header5"><spring:theme
							code="text.account.contract.startDate" text="Start Date" /></th>
					<th id="header6"><spring:theme
							code="text.account.contract.endDate" text="End Date" /></th>
					<th id="header2"><spring:theme
							code="text.account.contract.netValue" text="Net Value" /></th>
					<th id="header4"><spring:theme
							code="text.account.contract.status" text="Status" /></th>
					<th id="header5" class="right"><spring:theme
							code="text.account.contract.actions" text="Actions" /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${searchPageData.results}" var="contract">
					<c:url value="/my-account/service-contract/${contract.contractId}"
						var="myAccountContractDetailsUrl" />
					<tr>
						<td headers="header2"><c:out value="${contract.contractId}" /></td>
						<td headers="header4"><c:out value="${contract.description}" /></td>
						<td headers="header5"><fmt:formatDate
								value="${contract.startDate}" dateStyle="long" timeStyle="short"
								type="date" /></td>
						<td headers="header6"><fmt:formatDate
								value="${contract.endDate}" dateStyle="long" timeStyle="short"
								type="date" /></td>
						<td headers="header2"><c:out
								value="${contract.currency.symbol} ${contract.netValue}" /></td>
						<td headers="header4"><spring:message
								code="${contract.contractCondensedStatus}" /></td>
						<td headers="header5" class="right"><ycommerce:testId
								code="serviceContract_view_details_link">
								<a href="${myAccountContractDetailsUrl}"> <spring:theme
										code="text.account.servicecontract.viewDetails" text="View" />
								</a>
								<a
									href="${myAccountContractDetailsUrl}${contract.contractGuid/${contract.contractID}">
									<spring:theme code="text.account.servicecontract.renew"
										text="Renew" />
								</a>
								<a
									href="${contractTerminateUrl}${contract.contractGuid/${contract.contractID}> <spring:theme
										code="text.account.servicecontract.terminate" text="Terminate" />
								</a>
							</ycommerce:testId></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		<nav:pagination top="false" msgKey="text.account.contractHistory.page"
			supportShowPaged="${isShowPageAllowed}"
			supportShowAll="${isShowAllAllowed}"
			searchPageData="${searchPageData}"
			searchUrl="/my-account/service-contracts?sort=${searchPageData.pagination.sort}"
			numberPagesShown="${numberPagesShown}" />
	</c:if>
<c:if test="${empty searchPageData.results}">
	<p class="emptyMessage">
		<spring:theme code="text.account.servicecontract.noserviceContract"
			text="You have no Service Contracts" />
	</p>
</c:if>
</c:if>
<c:if test="${not empty backendError}">
	<p class="error">
		<spring:message code="${fn:toUpperCase(backendError)}" />
	</p>
</c:if>
