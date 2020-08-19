<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="pagination"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/responsive/nav/pagination"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<spring:url value="/my-account/service-contract/"
	var="contractDetailsUrl" />
<spring:url value="/my-account/service-contract/renew/"
	var="contractRenewalUrl" />
<c:if test="${empty searchPageData.results}">
	<div class="account-section-header">
		<spring:theme code="text.account.servicecontract.empty" />
	</div>
	<c:choose>
		<c:when test="${not empty backendError}">
			<p class="error">
				<spring:message code="${fn:toUpperCase(backendError)}" />
			</p>
		</c:when>
		<c:otherwise>
			<div
				class="account-section-content-contract account-section-content-small-contract ">

				<spring:theme code="text.account.servicecontract.empty.msg" />

			</div>
		</c:otherwise>
	</c:choose>
</c:if>
<c:if test="${empty backendError}">

	<c:if test="${not empty searchPageData.results}">
		<div class="account-section-content-contract	">
			<div class="account-contract">
				<div class="account-contract-pagination">

					<nav:pagination top="true"
						msgKey="text.account.contractHistory.page"
						showCurrentPageInfo="true" hideRefineButton="true"
						supportShowPaged="${isShowPageAllowed}"
						supportShowAll="${isShowAllAllowed}"
						searchPageData="${searchPageData}"
						searchUrl="/my-account/service-contracts?sort=${searchPageData.pagination.sort}"
						numberPagesShown="${numberPagesShown}" />
				</div>
				<div class="account-contract-list ">
					<table class="responsive-table">
						<tr
							class="account-contract-list-item account-contract-list-header row contractHeader hidden-xs">
							<!-- <div class="row contractHeader">  -->
							<th class="col-xs-2 order-id" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.id" />
							</th>
							<th class="col-xs-2 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.description" />
							</th>
							<th class="col-xs-2 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.startDate" />
							</th>
							<th class="col-xs-2 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.endDate" />
							</th>
							<th class="col-xs-2 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.netValue" />
							</th>
							<th class="col-xs-1 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.status" />
							</th>
							<th class="col-xs-1 status" style="word-wrap: break-word;">
								<spring:theme code="text.account.contract.actions"
									text="Actions" />
							</th>
						</tr>

						<c:forEach items="${searchPageData.results}" var="contract">
							<tr class="account-contract-list-item row responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.id" /></td>
								<td class="responsive-table-cell"><a
									class="responsive-table-link"
									href="${contractDetailsUrl}${contract.contractId}"> <span
										class="contract-list-id">${contract.contractId }</span>
								</a></td>

								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.description" /></td>

								<td style="text-transform: lowercase;"
									class="responsive-table-cell">${contract.description }</td>


								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.startDate" /></td>
								<td class="responsive-table-cell"><fmt:formatDate
										value="${contract.startDate}" dateStyle="long"
										timeStyle="short" type="date" /></td>


								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.endDate" /></td>
								<td class="responsive-table-cell"><fmt:formatDate
										value="${contract.endDate}" dateStyle="long" timeStyle="short" /></td>

								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.netValue" /></td>
								<td class="responsive-table-cell">${contract.currency.symbol}
									${contract.netValue}</td>

								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.status" /></td>
								<td class="responsive-table-cell">${contract.contractCondensedStatus}</td>


								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.contract.actions" /></td>
								<td class="responsive-table-cell"><c:choose>
										<c:when test="${not empty contract.isRenewable}">
											<c:choose>
												<c:when test="${contract.isRenewable eq true }">
													<form:form id="renewContractForm" class="contractEditForm"
														action="${contractRenewalUrl}" method="post">
														<input type="hidden" name="contractId"
															value="${contract.contractId}" />
														<input type="hidden" name="contractGuid"
															value="${contract.contractGuid}" />
														<button type="submit"
															class="btn btn-primary contractbtn-block contractRenewBtn">
															<spring:theme code="text.account.servicecontract.renew"
																text="Renew" />
														</button>
													</form:form>

												</c:when>
												<c:otherwise>
													<button type="submit"
														class="btn btn-primary contractbtn-block contractRenewBtn contractButtonDisabled">
														<spring:theme code="text.account.servicecontract.renew"
															text="Renew" />
													</button>
												</c:otherwise>
											</c:choose>
											<%-- <c:if test="${contract.isRenewable eq true }">	 --%>
										</c:when>
										<c:otherwise>
											<button type="submit"
												class="btn btn-primary contractbtn-block contractRenewBtn contractButtonDisabled">
												<spring:theme code="text.account.servicecontract.renew"
													text="Renew" />
											</button>
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
					</table>

				</div>
			</div>
			<div class="account-contract-pagination">
				<nav:pagination top="false"
					msgKey="text.account.contractHistory.page"
					showCurrentPageInfo="true" supportShowPaged="${isShowPageAllowed}"
					supportShowAll="${isShowAllAllowed}"
					searchPageData="${searchPageData}"
					searchUrl="/my-account/service-contracts?sort=${searchPageData.pagination.sort}"
					numberPagesShown="${numberPagesShown}" />
			</div>
		</div>
	</c:if>
</c:if>
