<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="contractData" required="true"
	type="com.sap.hybris.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/desktop/contract"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<ul>
	<li><spring:theme
			code="text.account.contract.contractDetails.contractId"
			arguments="${contractData.contractId}" /></li>
	<li><spring:theme code="text.account.servivecontract.dateCreated" />
		&nbsp <fmt:formatDate value="${contractData.startDate}"
			dateStyle="medium" timeStyle="short" type="date" /></li>
	<li><spring:theme code="text.account.servivecontract.dateEnded" />
		&nbsp <fmt:formatDate value="${contractData.endDate}"
			dateStyle="medium" timeStyle="short" type="date" /></li>
</ul>

