<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="registrations"
	tagdir="/WEB-INF/tags/addons/productregistrationaddon/responsive/productregistrationaddon"%>

<spring:url value="/my-account/registered-products"
	var="registrationsHistoryUrl" />
<div class="back-link">
	<button type="button" class="productRegTopBackBtn"
		data-back-to-registrations="${registrationsHistoryUrl}">
		<span class="glyphicon glyphicon-chevron-left"></span>
	</button>
	<span class="product-reg-header"><spring:theme
		code="text.account.productregistration.detailsRegisteration.heading"/></span>
</div>

<div class="account-registrationDetail-item-section-list">
	<registrations:productRegistrationProductDetails
		item="${registrationProdData.product}" />

	<registrations:productRegistrationGeneralDetails
		item="${registrationProdData}" />

	<registrations:productRegistrationWarrantyDetails
		item="${registrationProdData}" />
</div>

