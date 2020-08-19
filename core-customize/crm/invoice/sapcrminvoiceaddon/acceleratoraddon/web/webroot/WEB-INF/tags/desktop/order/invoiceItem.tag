<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="order" required="true"
	type="de.hybris.platform.commercefacades.order.data.AbstractOrderData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>


<div class="orderBox delivery">
	<div class="headline"><spring:theme code="text.account.invoice.download.headline"
				text="Invoice Document" /></div>

				<div id="errorMessage"></div>

	<c:forEach items="${orderData.billingDocNumbers}" var="billingdoc">
		<c:if test="${billingdoc.value  eq 'VBRK'}">
			<ul>
				<p>
					<a href="#" id="invoicePDF" data-index="${billingdoc.key}"
						class="invoiceCRMClass"><spring:theme code="text.account.invoice.download.link"
							text="Download" />
				</p>
			</ul>
		</c:if>
	</c:forEach>
</div>
