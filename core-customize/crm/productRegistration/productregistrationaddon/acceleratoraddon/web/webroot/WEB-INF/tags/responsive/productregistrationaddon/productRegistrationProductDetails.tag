<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="item" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class=" regProductDetailsHeadline" style="margin-top: 56px;">
	<spring:theme code="text.account.productregistration.productDetails"/></div>
<div class="account-orderdetail clearfix well well-tertiary well-lg">
	<div class="order-detail-overview">
		<div class="col-md-2 col-sm-3 " style="height: 105px;">
			<div class="productDetailsIcon">
				<a href="${item.url }"> <product:productPrimaryImage
						product="${item}" format="thumbnail" />
				</a>
			</div>
		</div>

		<div class="col-md-10 col-sm-9">
			<div class="col-sm-8">
				<div class="details">
					<div class="name">
						<span class="regItem-label"> <spring:theme
								code="text.account.productregistration.product.code" /> &#58;
							&nbsp;&nbsp;&nbsp; ${item.code}
						</span>
					</div>
				</div>
				<div class="price">
					<c:if test="${not empty item.name}">
						<span class="regItem-label"> <spring:theme
								code="text.account.productregistration.product.name" /> &#58;
							&nbsp;&nbsp;&nbsp; ${item.name}
						</span>
					</c:if>
					<br>
					<c:if test="${not empty item.description}">
						<span class="regItem-label"> <spring:theme
								code="text.account.productregistration.product.description" />
							&#58; &nbsp;&nbsp;&nbsp; ${item.description}
						</span>
					</c:if>
					<br>
				</div>
			</div>
		</div>
	</div>
</div>
