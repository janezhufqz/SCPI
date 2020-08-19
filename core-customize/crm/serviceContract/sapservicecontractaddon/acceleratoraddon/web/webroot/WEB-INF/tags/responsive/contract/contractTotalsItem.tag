<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<%@ attribute name="containerCSS" required="false"
	type="java.lang.String"%>

<div class="account-contractdetail-item-section-footer">
	<p class="total-headline">
		<spring:theme code="text.account.contract.contractTotals"
			text="Contract Total" />
	</p>
	<div class="contractTotal">

		<div class="subtotal row">
			<div class="col-xs-6">
				<spring:theme code="text.account.contract.netValue" text="Net Value" />
			</div>
			<div class="col-xs-6 text-right">${contract.currency.symbol }
				${contract.netValue}</div>
		</div>
		<div class="shipping row">
			<div class="col-xs-6">
				<spring:theme code="text.account.contract.grossValue"
					text="Gross Value" />
			</div>
			<div class="col-xs-6 text-right">${contract.currency.symbol }
				${contract.grossValue}</div>
		</div>
	</div>
</div>
<div class="col-xs-12 contract-separator"></div>
