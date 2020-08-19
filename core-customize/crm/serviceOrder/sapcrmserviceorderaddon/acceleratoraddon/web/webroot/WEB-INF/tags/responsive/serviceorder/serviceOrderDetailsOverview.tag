<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ attribute name="orderData" required="true"
	type="com.sap.hybris.crm.sapserviceorderservices.ServiceOrderData"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/responsive/order"%>

<div class="col-lg-12">
	<div class="col-sm-3 item-wrapper">
		<div class="item-group">
			<c:if test="${orderData.statusDisplay ne null}">
				<ycommerce:testId code="order_status_label">
					<span class="item-label"><spring:theme
							code="text.sapcrmserviceorderaddon.serviceorder.info.status" /></span>
					<span class="item-value">${fn:escapeXml(orderData.statusDisplay)}</span>
				</ycommerce:testId>
			</c:if>
		</div>
	</div>

	<div class="col-sm-3 item-wrapper">
		<div class="item-group">
			<c:if test="${orderData.totalPrice ne null}">
				<ycommerce:testId code="serviceOrder_total_label">
					<span class="item-label"><spring:theme
							code="text.sapcrmserviceorderaddon.serviceorder.ordertotal.gross" /></span>
					<span class="item-value"><format:price
							priceData="${orderData.totalPrice}" /></span>
				</ycommerce:testId>
			</c:if>
		</div>
	</div>
	<div class="col-sm-3 item-wrapper">
		<div class="item-group">
			<c:if test="${orderData.netPrice ne null}">
				<ycommerce:testId code="serviceOrder_subtotal_label">
					<span class="item-label"><spring:theme
							code="text.sapcrmserviceorderaddon.serviceorder.ordertotal.net" /></span>
					<span class="item-value"><format:price
							priceData="${orderData.netPrice}" /></span>
				</ycommerce:testId>
			</c:if>
		</div>
	</div>
	<div class="col-sm-3 item-wrapper" align="right">
		<div class="item-group service-order-related">

			<ycommerce:testId code="serviceOrder_related_label">
				<c:forEach var="serviceOrderRelObjData"
					items="${orderData.relatedObjects}" varStatus="loopCounter">
					<span class="item-label"> <spring:message
							code="text.sapcrmserviceorderaddon.serviceorder.${serviceOrderRelObjData.objectType}"
							text="${serviceOrderRelObjData.objectType}" />
					</span>
					<span class="item-value"> <c:url
							value="${serviceOrderRelObjData.objecturl}${serviceOrderRelObjData.objectid}"
							var="relObjecturl" /> <a href="${relObjecturl}"><p
								class="form-control-static">${serviceOrderRelObjData.objectid}</p></a>
					</span>
				</c:forEach>

			</ycommerce:testId>

		</div>
	</div>

	<ycommerce:testId code="orderDetails_paymentDetails_section">
		<div class="well-content">
			<c:if test="${orderData.billingAddress ne null}">
				<div class="col-sm-6 service-order-billing-address">
					<div class="label-order-serviceorder-address">
						<spring:theme
							code="text.sapcrmserviceorderaddon.serviceorder.billing.head" />
					</div>
					<div class="value-order">
						<order:addressItem address="${orderData.billingAddress}" />
					</div>
				</div>
			</c:if>
			<c:if test="${orderData.deliveryAddress ne null}">
				<div class="col-sm-6 service-order-delivery-address">
					<div class="label-order-serviceorder-address">
						<spring:theme
							code="text.sapcrmserviceorderaddon.serviceorder.shipping.head" />
					</div>
					<div class="value-order">
						<order:addressItem address="${orderData.deliveryAddress}" />
					</div>
				</div>
			</c:if>
		</div>
	</ycommerce:testId>
</div>

