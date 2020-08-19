<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<div class="label-order">
	<spring:theme code="text.account.contract.billingPlan" />
</div>
<div>
	<ul>
		<li><spring:theme
				code="text.account.contract.detail.settlementPeriod"
				text="Settlement Period :" /> <c:if
				test="${not empty contract.billingPlan.settlementPeriod}"> &nbsp ${contract.billingPlan.settlementPeriod }</c:if></li>
		<li><spring:theme code="text.account.contract.detail.billingDate"
				text="Billing Date :" /> <c:if
				test="${not empty contract.billingPlan.billingDate}"> &nbsp ${contract.billingPlan.billingDate }</c:if></li>
		<li><spring:theme code="text.account.contract.detail.invoiceDate"
				text="Billing Doc. Creation Date :" /> <c:if
				test="${not empty contract.billingPlan.invoiceCreationDate}">&nbsp ${contract.billingPlan.invoiceCreationDate}</c:if></li>
	</ul>
</div>

