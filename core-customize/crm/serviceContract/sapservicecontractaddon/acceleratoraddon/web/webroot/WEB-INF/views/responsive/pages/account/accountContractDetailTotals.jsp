<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/responsive/contract"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty backendError}">
	<div class="account-contractdetail">
		<div class="account-contractdetail-item-section-header">
			<div id="contractDetailTotal">
				<div class="col-sm-6 col-sm-offset-6 col-md-5 col-md-offset-7">
					<contract:contractTotalsItem contract="${contractData}" />
				</div>
			</div>
		</div>
	</div>
</c:if>
