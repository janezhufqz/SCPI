<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/my-company/business-agreement/"
	var="agreementDetailsUrl" />
<div class="account-section-header">
	<spring:theme code="text.account.businessagreement.history" />
</div>


<c:if test="${ empty searchPageData.results }">
	<div class="row">
		<div class="col-md-6 col-md-push-3">
			<div class="account-section-content content-empty">

				<spring:theme code="text.account.businessagreement.empty.msg" />

			</div>
		</div>
	</div>
</c:if>

<c:if test="${not empty searchPageData.results}">
	<div class="account-section-content">
		<div class="account-orderhistory">
			<div class="account-orderhistory-pagination">
				<nav:pagination top="true"
					msgKey="text.account.agreementHistory.page"
					showCurrentPageInfo="true" hideRefineButton="true"
					supportShowPaged="${isShowPageAllowed}"
					supportShowAll="${isShowAllAllowed}"
					searchPageData="${searchPageData}"
					searchUrl="/my-company/business-agreements?sort=${searchPageData.pagination.sort}"
					numberPagesShown="${numberPagesShown}" />
			</div>
			<div class="responsive-table">
				<table class="responsive-table">
					<thead>
						<tr class="responsive-table-head hidden-xs">
							<th id="header1"><spring:theme
									code="text.account.businessagreement.businessAgreementID" /></th>
							<th id="header2"><spring:theme
									code="text.account.businessagreement.description" /></th>
							<th id="header3"><spring:theme
									code="text.account.businessagreement.businessAgreementClass" /></th>
							<th id="header4"><spring:theme
									code="text.account.businessagreement.isDefault" /></th>

						</tr>
					</thead>
					<tbody>
						<c:forEach items="${searchPageData.results}" var="agreement">
							<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.businessagreement.businessAgreementID" /></td>
								<td headers="header1" class="responsive-table-cell"><a
									href="${agreementDetailsUrl}${agreement.businessAgreementID}"
									class="responsive-table-link"> <c:out
											value="${agreement.businessAgreementID}" />
								</a></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.businessagreement.description" /></td>
								<td headers="header2" class="responsive-table-cell"><spring:message
										code="${agreement.description }" /></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.businessagreement.businessAgreementClass" /></td>
								<td headers="header3" class="responsive-table-cell"><spring:message
										code="${agreement.businessAgreementClass}" /></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.businessagreement.isDefault" /></td>
								<td headers="header4" class="responsive-table-cell"><c:choose>
										<c:when test="${agreement.isDefault}">
											<input type="checkbox" checked="checked" disabled="disabled" />
										</c:when>
										<c:otherwise>
											<input type="checkbox" disabled="disabled" />
										</c:otherwise>
									</c:choose></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="account-orderhistory-pagination">
			<nav:pagination top="false"
				msgKey="text.account.agreementHistory.page"
				showCurrentPageInfo="true" hideRefineButton="true"
				supportShowPaged="${isShowPageAllowed}"
				supportShowAll="${isShowAllAllowed}"
				searchPageData="${searchPageData}"
				searchUrl="/my-company/business-agreements/?sort=${searchPageData.pagination.sort}"
				numberPagesShown="${numberPagesShown}" />
		</div>
	</div>
</c:if>
