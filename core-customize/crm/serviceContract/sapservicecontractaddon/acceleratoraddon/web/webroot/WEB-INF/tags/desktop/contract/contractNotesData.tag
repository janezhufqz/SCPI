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

<div class="contractBox address">
	<div class="headline">
		<spring:theme code="text.contract.notes" text="Notes" />
	</div>
	<ul class="ellipses">
		<c:if test="${not empty contract.notes}">
			<c:forEach items="${contract.notes }" var="note">
				<li>${note}</li>
			</c:forEach>
		</c:if>
	</ul>

</div>
