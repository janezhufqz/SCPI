<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/responsive/contract"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>

<c:if test="${empty backendError}">
	<div class="account-contractdetail">
		<div class="account-contractdetail-overview">
			<ycommerce:testId code="contractDetail_overview_section">
				<contract:accountContractDetailsOverview
					contractData="${contractData}" />
			</ycommerce:testId>
		</div>
		<div class="account-contractdetail-item-section">
			<div class="account-contractdetail-item-section-header">
				<div class="contractDetail_itemHeader">
					<table>
						<thead>
							<th><span class="col-xs-6 text-uppercase"> <spring:theme
										code="text.account.contract.product.header" text="Items" />
							</span></th>
						</thead>
					</table>
				</div>
			</div>
			<c:forEach items="${contractData.items}" var="contractItem">
				<div
					class="account-contractdetail-item-section-list-item account-contractdetail-item-section-body">
					<contract:contractEntryDetails item="${contractItem}"
						contract="${contractData}" />
				</div>
			</c:forEach>
		</div>
	</div>
</c:if>
