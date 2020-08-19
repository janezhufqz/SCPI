<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/desktop/contract"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="clearfix">
	<c:if test="${empty backendError}">
		<div class="span-7">
			<contract:accountContractDetailsOverview
				contractData="${contractData}" />
		</div>

		<div class="span-6 last order-totals" style="float: right;">
			<contract:contractTotalsItem contract="${contractData}" />
		</div>
	</c:if>
</div>
