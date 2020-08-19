<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${empty backendError}">
	<spring:url value="/my-account/service-contracts"
		var="serviceContractUrl" />

	<div id="contract-detail-action-btn">
		<div
			class="contract-cancel-panel col-xs-12 col-sm-6 col-sm-offset-7 col-md-5 col-md-offset-7">

			<button onclick="location.href='${serviceContractUrl}';"
				type="button" class="btn btn-default contractbtn-block"
				id="contractBackBtn">

				<spring:theme
					code="text.account.contractDetails.backToContractHistory"
					text="Back To Contract History" />
			</button>
		</div>
	</div>
</c:if>
