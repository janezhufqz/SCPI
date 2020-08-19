<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="item" required="true"
	type="de.hybris.platform.productregistrationfacades.data.ProductRegistrationData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<div class="regProductDetailsHeadline">
	<spring:theme code="text.account.productregistration.warrantyDetails" />
</div>
<div class="clearfix well well-tertiary well-lg">
	<div class="order-detail-overview">
		<c:choose>
			<c:when test="${not empty registrationProdData.warrantyStart}">
				<div class="col-md-10 col-sm-9">
					<div class="col-sm-8">
						<table
							style="padding: 0; border-spacing: none; margin-top: -16px;"
							cellspacing="0" cellpadding="0" class="genDetails">
							<c:if test="${not empty item.warrantyDescription}">
								<tr>
									<td><spring:theme
											code="text.account.productregistration.warrantyDescription" /></td>
									<td>&#58; &nbsp;&nbsp;&nbsp; ${item.warrantyDescription} </span>
									</td>
								</tr>
							</c:if>
							<c:if test="${not empty item.warrantyBasis}">
								<tr>
									<td><spring:theme
											code="text.account.productregistration.warrantyBasis" /></td>
									<td>&#58; &nbsp;&nbsp;&nbsp; ${item.warrantyBasis}</td>
								</tr>
							</c:if>
							<c:if test="${not empty item.warrantyStart}">
								<tr>
									<td><spring:theme
											code="text.account.productregistration.warrantyStart" /></td>
									<td>&#58; &nbsp;&nbsp;&nbsp; <fmt:formatDate
											value="${item.warrantyStart}" dateStyle="long"
											timeStyle="short" type="date" /></td>
								</tr>
							</c:if>
							<c:if test="${not empty item.warrantyEnd}">
								<tr>
									<td><spring:theme
											code="text.account.productregistration.warrantyEnd" /></td>
									<td>&#58; &nbsp;&nbsp;&nbsp; <fmt:formatDate
											value="${item.warrantyEnd}" dateStyle="long"
											timeStyle="short" type="date" /></td>
								</tr>
							</c:if>
						</table>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<spring:theme
					code="text.account.productregistration.warrantyDetails.noWarranty" />
			</c:otherwise>
		</c:choose>
	</div>
</div>
