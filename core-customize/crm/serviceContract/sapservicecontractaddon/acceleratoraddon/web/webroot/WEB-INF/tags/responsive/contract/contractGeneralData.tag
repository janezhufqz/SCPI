<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>

<div class="label-order">
	<spring:theme code="text.account.contract.generalData" />
</div>
<div>
	<ul>
		<li><spring:theme code="text.account.contract.description"
				text="Description" /><span style="text-transform: lowercase;">
				&#58; &nbsp; ${contract.description }</span></li>
		<%-- <li><spring:theme
                code="text.account.contract.externalDescription"
                text="External Reference" /> &nbsp ${contract.description }</li> --%>
		<li><spring:theme code="text.account.contract.status"
				text="Status" />&#58; &nbsp; ${contract.contractStatus }</li>

	</ul>
</div>
