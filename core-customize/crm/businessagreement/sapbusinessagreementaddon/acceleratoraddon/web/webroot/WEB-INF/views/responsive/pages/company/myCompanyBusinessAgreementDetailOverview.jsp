<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="well-lg well well-tertiary">
	<div class="col-sm-12 col-md-12 col-no-padding">
		<div class="row">
			<div class="col-sm-4 item-wrapper">
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.businessAgreementID" /></span> <span
						class="item-value">${businessAgreementDetailsData.businessAgreementID}</span>

				</div>
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.refnumber" /></span> <span
						class="item-value">${businessAgreementDetailsData.refnumber}</span>
					<div class="control"></div>

				</div>
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.collectivebill" /></span> <span
						class="item-value"><c:choose>
							<c:when test="${businessAgreementDetailsData.collectivebill}">
								<input type="checkbox" checked="checked" disabled="disabled" />

							</c:when>
							<c:otherwise>
								<input type="checkbox" disabled="disabled" />

							</c:otherwise>
						</c:choose></span>
					<div class="control"></div>

				</div>

			</div>
			<div class="col-sm-4 item-wrapper">
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.description" /></span> <span
						class="item-value">${businessAgreementDetailsData.description}</span>


				</div>
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.businessAgreementClass" /></span>
					<span class="item-value">${businessAgreementDetailsData.businessAgreementClass}</span>


				</div>
			</div>
			<div class="col-sm-4 item-wrapper">
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.partnerName" /></span> <span
						class="item-value">${businessAgreementDetailsData.partnerName}</span>

				</div>
				<div class="item-group">

					<span class="item-label"><spring:theme
							code="text.account.businessagreement.isDefault" /></span> <span
						class="item-value"> <c:choose>
							<c:when test="${businessAgreementDetailsData.isDefault}">
								<input type="checkbox" checked="checked" disabled="disabled" />
							</c:when>
							<c:otherwise>
								<input type="checkbox" disabled="disabled" />
							</c:otherwise>
						</c:choose></span>
					<div class="control"></div>

				</div>
			</div>
		</div>
	</div>
</div>


