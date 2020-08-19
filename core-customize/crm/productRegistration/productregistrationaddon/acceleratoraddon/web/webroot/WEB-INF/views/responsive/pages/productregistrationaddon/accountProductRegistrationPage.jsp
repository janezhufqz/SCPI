<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="pagination"
	tagdir="/WEB-INF/tags/responsive/nav/pagination"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="searchUrl"
	value="/my-account/registered-products?sort=${searchPageData.pagination.sort}" />

<div class="account-section-header">
<cms:pageSlot position="ProductRegistration" var="component">
	<cms:component component="${component}" />
</cms:pageSlot>
	<spring:theme code="text.account.productregistration.history"/>
	
</div>



<div class="clearfix visible-md-block visible-lg-block"></div>
<div class="row product-registration-body">
	<c:if test="${not empty registeredProducts}">
		<div class="account-section-content	">
			<div class="account-productRegistrationHistory">
				<%-- <div class="account-productRegistrationHistory-pagination">
					<nav:pagination top="true" msgKey="text.account.productregistration.page"  showCurrentPageInfo="true" hideRefineButton="false"
					supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" 
					 numberPagesShown="${numberPagesShown}"/>
			</div> --%>
				<table class="responsive-table">
					<thead>
						<tr class="responsive-table-head hidden-xs">
							<th><spring:theme
									code="text.account.productregistration.registrationNumber"
									text="Registration No" /></th>
							<th><spring:theme
									code="text.account.productregistration.productCode"
									text="Product" /></th>
							<th><spring:theme
									code="text.account.productregistration.product"
									text="Product Description" /></th>
							<th><spring:theme
									code="text.account.productregistration.registrationDate"
									text="Valid From" /></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${registeredProducts}" var="registeredProduct">
							<tr class="responsive-table-item">
								<c:url
									value="/my-account/registered-product/${registeredProduct.registrationGuid}"
									var="myAccountregisteredProductDetailsUrl" />
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.productregistration.serialNo"
										text="Registration No" /></td>
								<td><a href="${myAccountregisteredProductDetailsUrl}"
									class="responsive-table-link"><c:out
											value="${registeredProduct.registrationNumber}" /></a></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.productregistration.productCode"
										text="Product ID" /></td>
								<td><spring:message
										code="${registeredProduct.product.code}" /></td>
								<input type="hidden" name="regProdCode" id="regProdCode"
									value="${registeredProduct.product.code}" />
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.productregistration.productName"
										text="Product Description" /></td>
								<td><spring:message
										code="${registeredProduct.product.name}" /></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.account.productregistration.registrationDate"
										text="Registration Date" /></td>
								<td><fmt:formatDate value="${registeredProduct.registrationDate}"
										dateStyle="long" timeStyle="short" type="date" /></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
			<%-- <div class="account-productRegistrationHistory-pagination">
			<nav:pagination top="false" msgKey="text.account.productregistration.page"  showCurrentPageInfo="true" hideRefineButton="false"
			 supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}" searchPageData="${searchPageData}" searchUrl="${searchUrl}" 
			 numberPagesShown="${numberPagesShown}"/>
		</div> --%>
		</div>
	</c:if>

	<c:if test="${empty registeredProducts}">
		<p class="emptyMessage">
			<spring:theme
				code="text.account.productregistration.noRegisteredProducts"
				text="You have no registered products" />
		</p>
	</c:if>
</div>

