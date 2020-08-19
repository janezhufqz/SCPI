<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="contractData" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="account-contract-header_data">
	<div class="col-md-3">
		<ycommerce:testId code="contractDetail_overviewOrderID_label">
			<spring:theme code="text.account.contract.contractDetails.contractId"
				arguments="${contractData.contractId}" />
		</ycommerce:testId>
	</div>
	<div class="col-md-9 text-right-md">

		<ycommerce:testId code="contractDetail_overviewOrderTotal_label">
			<span class="uppercase-sm"> <spring:theme
					code="text.account.contract.netValue" />&nbsp; &#58; &nbsp;
			</span>		
			${contractData.currency.symbol}
			${contractData.netValue}
			</ycommerce:testId>

	</div>
	<div class="col-md-12 text-right-md">
		<c:if test="${not empty contractData.status}">
			<ycommerce:testId code="contractDetail_overviewOrderStatus_label">
				<span class="uppercase-sm"> <spring:theme
						code="text.account.contract.status" />&nbsp; &#58;
				</span>&nbsp;
				<span class="uppercase-sm"> <spring:theme
						code="text.account.contract.status.display.${contractData.contractStatus}"
						text="${contractData.contractStatus}" />
				</span>
			</ycommerce:testId>
		</c:if>
	</div>

	<div class="col-md-12 text-right-md">
		<ycommerce:testId code="contractDetail_overviewStatusDate_label">
			<span class="uppercase-sm"> <spring:theme
					code="text.account.contract.startDate" />&nbsp;&#58;&nbsp;
			</span>
			<fmt:formatDate value="${contractData.startDate}" dateStyle="medium"
				timeStyle="short" type="date" />
		</ycommerce:testId>
	</div>
	<!-- 			&nbsp;&#x7C;&nbsp; -->
	<div class="col-md-12 text-right-md">
		<ycommerce:testId code="contractDetail_overviewStatusDate_label">
			<span class="uppercase-sm"> <spring:theme
					code="text.account.contract.endDate" />&nbsp;&#58;&nbsp;
			</span>
			<fmt:formatDate value="${contractData.endDate}" dateStyle="medium"
				timeStyle="short" type="date" />
		</ycommerce:testId>
	</div>
</div>

