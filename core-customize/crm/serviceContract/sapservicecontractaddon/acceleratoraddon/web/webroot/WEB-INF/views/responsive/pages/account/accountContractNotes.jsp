<%@ page trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/responsive/contract"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<br>
<c:if test="${empty backendError}">
	<div class="account-contractdetail">
		<div
			class="account-contractdetail-item-section-header  item-box well well-tertiary">
			<div class="col-md-6">
				<contract:contractNotesData contract="${contractData}" />
			</div>
		</div>
	</div>
</c:if>
