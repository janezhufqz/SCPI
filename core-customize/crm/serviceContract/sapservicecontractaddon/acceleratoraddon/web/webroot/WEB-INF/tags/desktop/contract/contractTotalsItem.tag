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

<%@ attribute name="containerCSS" required="false"
	type="java.lang.String"%>

<table id="contractTotals" class="${containerCSS}">
	<thead>
		<tr>
			<td><spring:theme code="text.account.contract.contractTotals"
					text="Contract Total" /></td>
			<td></td>
		</tr>
	</thead>

	<tbody>
		<tr>
			<td><b><spring:theme code="text.account.contract.netValue"
						text="Net Value" /></b></td>
			<td>${contract.currency.symbol}${contract.netValue}</td>
		</tr>

		<tr>
			<td><b><spring:theme code="text.account.contract.grossValue"
						text="Gross Value" /></b></td>
			<td>${contract.currency.symbol}${contract.grossValue}</td>
		</tr>

	</tbody>

</table>
