<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="label-order">
	<spring:theme code="text.contract.notes" text="Notes" />
</div>

<c:if test="${not empty contract.notes}">
	<ul>
		<c:forEach items="${contract.notes }" var="note">
			<li style="text-transform: lowercase;">&nbsp; ${note}</li>
		</c:forEach>
	</ul>
</c:if>
<c:if test="${empty contract.notes }">
&#45;
</c:if>




