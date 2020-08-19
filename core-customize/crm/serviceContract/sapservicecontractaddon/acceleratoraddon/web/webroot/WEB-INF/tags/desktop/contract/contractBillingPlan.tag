<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="contractItemList">
	<div class="headline">
		<spring:theme code="text.contract.billing.plan" text="Billing Plan" />
	</div>

	<ul>
		<li><b><spring:theme
					code="text.account.contract.detail.settlementPeriod"
					text="Settlement Period :" /></b> <c:if
				test="${not empty contract.billingPlan.settlementPeriod}"> &nbsp ${contract.billingPlan.settlementPeriod }</c:if></li>
		<li><b><spring:theme
					code="text.account.contract.detail.billingDate"
					text="Billing Date :" /></b> <c:if
				test="${not empty contract.billingPlan.billingDate}"> &nbsp ${contract.billingPlan.billingDate }</c:if></li>
		<li><b><spring:theme
					code="text.account.contract.detail.invoiceDate"
					text="Billing Doc. Creation Date :" /></b> <c:if
				test="${not empty contract.billingPlan.invoiceCreationDate}">&nbsp ${contract.billingPlan.invoiceCreationDate}</c:if></li>
	</ul>

</div>
