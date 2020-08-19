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
	<spring:theme code="text.account.productregistration.genDetails" />
</div>
<div class="clearfix well well-tertiary well-lg">
	<div class="order-detail-overview">

		<div class="col-md-10 col-sm-9">
			<div class="col-sm-8">
				<table style="padding: 0; border-spacing: none; margin-top: -16px;"
					cellspacing="0" cellpadding="0" class="genDetails">
					<c:if test="${not empty item.registrationNumber}">
						<tr>
							<td><spring:theme
									code="text.account.productregistration.registrationNumber" /></td>
							<td>&#58; &nbsp;&nbsp;&nbsp; ${item.registrationNumber} </span>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty item.registrationDate}">
						<tr>
							<td><spring:theme
									code="text.account.productregistration.registrationDate" /></td>
							<td>&#58; &nbsp;&nbsp;&nbsp; <fmt:formatDate
									value="${item.registrationDate}" dateStyle="long"
									timeStyle="short" type="date" /> </span>
							</td>
						</tr>
					</c:if>
					<c:if test="${not empty item.validTo}">
						<tr>
							<td><spring:theme
									code="text.account.productregistration.validTo" /></td>
							<td>&#58; &nbsp;&nbsp;&nbsp; <fmt:formatDate
									value="${item.validTo}" dateStyle="long" timeStyle="short"
									type="date" /> </span>
							</td>
						</tr>
					</c:if>
				</table>
			</div>
		</div>
	</div>
</div>
