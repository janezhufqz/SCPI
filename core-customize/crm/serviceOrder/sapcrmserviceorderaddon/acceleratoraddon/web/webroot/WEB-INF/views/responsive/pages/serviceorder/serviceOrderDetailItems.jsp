<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="serviceorder"
	tagdir="/WEB-INF/tags/addons/sapcrmserviceorderaddon/responsive/serviceorder"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<script>
	function UTCtoZonedTimeConverter() {
		var timestamp = document.getElementsByClassName('startEndTimings');
		for (i = 0; i < timestamp.length; i++) { 
			var v = timestamp[i];
			var appendedstartendtime=v.innerHTML;
			var times=appendedstartendtime.split("-");
			var convertedStarttime=convert(times[0]);
			var convertedEndtime=convert(times[1]);
			var str=convertedStarttime.concat(" -",convertedEndtime);
		   v.innerHTML=str;
		}
	}
	function convert(datetime){
		var AM=true;		
		var v2= new Date(datetime+" GMT+0000");
		var month=v2.getMonth().toString();
				   var date=v2.getDate().toString();
				   var year=v2.getFullYear();
				   var hours=v2.getHours();
				   var min=v2.getMinutes();
				   var month_short = Array('Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec');
				   var monthStr=month_short[month];
				   if(hours>12){
					   AM=false;
					   hours=hours-12;
				   }
				   var converteddate=monthStr.concat(" ",date,", ",year," ",hours,":",min," ",AM?'AM':'PM');
				   return converteddate;
	}
	window.onload = UTCtoZonedTimeConverter;
</script>

<div class="account-serviceorderitemdetails-section-content">
	<div class="account-itemdetails">
		<div class="responsive-table">
			<table class="responsive-table">
				<thead>
					<tr class="responsive-table-head hidden-xs">
						<th id="header1"><spring:theme
								code="text.sapcrmserviceorderaddon.serviceorder.item.details.product" /></th>
						<th id="header2"><spring:theme
								code="text.sapcrmserviceorderaddon.serviceorder.item.details.quantity" /></th>
						<th id="header3"><spring:theme
								code="text.sapcrmserviceorderaddon.serviceorder.item.details.netvalue" /></th>
						<th id="header4"><spring:theme
								code="text.sapcrmserviceorderaddon.serviceorder.item.details.currency" /></th>
						<th id="header6"><spring:theme
								code="text.sapcrmserviceorderaddon.serviceorder.item.details.scheduledate" /></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${orderData.entries}" var="entry">
						<tr class="responsive-table-item">
							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.sapcrmserviceorderaddon.serviceorder.item.details.product" /></td>
							<c:choose>
								<c:when test="${entry.product.url ne null}">
									<c:url value="${entry.product.url}" var="productUrl" />
									<td headers="header1" class="responsive-table-cell"><ycommerce:testId
											code="invoiceDetails_product_link">
											<div class="itemName">
												<a href="${entry.product.purchasable ? productUrl : ''}"
													class="responsive-table-link">${entry.product.code}</a>
											</div>
										</ycommerce:testId>
								</c:when>
								<c:otherwise>
									<td headers="header1" class="responsive-table-cell"><ycommerce:testId
											code="invoiceDetails_product_link">
											<div class="itemName">${entry.product.code}</div>
										</ycommerce:testId></td>
								</c:otherwise>
							</c:choose>

							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.sapcrmserviceorderaddon.serviceorder.item.details.quantity" /></td>
							<td headers="header2" class="responsive-table-cell">
								${fn:escapeXml(entry.quantity)} ${fn:escapeXml(" ")}
								${fn:escapeXml(entry.unitOfMeasure)}</td>

							<td class="hidden-sm hidden-md hidden-lg"><spring:theme
									code="text.sapcrmserviceorderaddon.serviceorder.item.details.netvalue" /></td>
							<td headers="header3"
								class="responsive-table-cell responsive-table-cell-bold"><serviceorder:itemPrice
									priceData="${entry.netPrice}" displayFreeForZero="true" /></td>
							<c:if test="${orderData.currency ne null}">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.sapcrmserviceorderaddon.serviceorder.item.details.currency" /></td>
								<td headers="header4" class="responsive-table-cell">
									${fn:escapeXml(orderData.currency.isocode)}</td>
							</c:if>
							<c:if test="${entry.scheduleServiceOrderEntries ne null}">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.sapcrmserviceorderaddon.serviceorder.item.details.scheduledate" /></td>
								<td headers="header5" class="responsive-table-cell"><c:forEach
										items="${entry.scheduleServiceOrderEntries}"
										var="scheduleEntry" >
										<div class="startEndTimings" >
											<fmt:formatDate value="${scheduleEntry.startDate}"	dateStyle="medium" timeStyle="short" type="both" />-<fmt:formatDate value="${scheduleEntry.endDate}"
											dateStyle="medium" timeStyle="short" type="both" /> 
											</div>
									</c:forEach></td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
