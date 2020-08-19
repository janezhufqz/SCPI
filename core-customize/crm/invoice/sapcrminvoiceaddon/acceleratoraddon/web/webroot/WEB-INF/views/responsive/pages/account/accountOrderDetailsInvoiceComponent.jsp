<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:if test="${not empty orderData.billingDocNumbers}">
    <div class="account-orderdetail well well-tertiary invoice">
        <div class="well-headline">
           <spring:theme code="text.account.invoice.download.headline"
			text="Invoice Document" />
        </div>
        
        <div class="well-content">
           <div id="errorMessage">
		<spring:theme code="text.account.invoice.download.defaultError" />
	</div>

	<c:forEach items="${orderData.billingDocNumbers}" var="billingdoc">
		<c:if test="${billingdoc.value  eq 'VBRK'}">


			<a href="#" id="invoicePDF" data-index="${billingdoc.key}"
				class="invoiceCRMClass"><spring:theme
					code="text.account.invoice.download.link" text="Download" /> </a>
		</c:if>
	</c:forEach>
        </div>
       
    </div>
</c:if>
