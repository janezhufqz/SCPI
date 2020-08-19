<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>


<spring:htmlEscape defaultHtmlEscape="true" />
<script>
	function UTCtoZonedTimeConverter() {
		var timestamp = document.getElementsByClassName('ordercreateDate');
		for (i = 0; i < timestamp.length; i++) { 
			var v = timestamp[i];
			var AM=true;		
		   var v2 = new Date(v.innerHTML+" GMT+0000");
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
		   v.innerHTML=converteddate;
		}
	}
	window.onload = UTCtoZonedTimeConverter;
</script>

<c:set var="searchUrl"
	value="/my-account/service-orders?sort=${searchPageData.pagination.sort}" />

<div class="account-section-header">
	<spring:theme code="text.account.serviceOrderHistory" />
</div>


<c:choose>
	<c:when test="${empty searchPageData.results}">
		<div class="account-section-content	 account-section-content-small ">
			<ycommerce:testId code="orderHistory_noOrders_label">
				<spring:theme code="text.account.orderHistory.noOrders" />
			</ycommerce:testId>
		</div>
	</c:when>
	<c:otherwise>
		<div class="account-section-content	">
			<div class="account-orderhistory">
				<div class="account-orderhistory-pagination">
					<nav:pagination top="true"
						msgKey="text.account.serviceOrderHistory.page.sortTitle"
						showCurrentPageInfo="true" hideRefineButton="true"
						supportShowPaged="${isShowPageAllowed}"
						supportShowAll="${isShowAllAllowed}"
						searchPageData="${searchPageData}" searchUrl="${searchUrl}"
						numberPagesShown="${numberPagesShown}"
						additionalParams="${additionalParams}" />
				</div>
				<div class="account-overview-table">
					<table class="orderhistory-list-table responsive-table">
						<tr
							class="account-orderhistory-table-head responsive-table-head hidden-xs">
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.id.label" /></th>
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.type.label" /></th>
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.status.label" /></th>
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.price.label" /></th>
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.createdate.label" /></th>
							<th><spring:theme
									code="text.myaccount.sapcrmserviceorder.description.label" /></th>
						</tr>
						<c:forEach items="${searchPageData.results}" var="order">
							<spring:url value="/my-account/service-order/"
								var="serviceOrderDetailsURL">
								<spring:param name="orderCode" value="${order.code}" />
							</spring:url>
							<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.id.label" /></td>
								<td class="responsive-table-cell"><a
									href="${serviceOrderDetailsURL}" class="responsive-table-link">
										${order.code} </a></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.type.label" /></td>
								<td class="responsive-table-cell">${order.type}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.status.label" /></td>
								<td class="responsive-table-cell">${order.statusDisplay}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.price.label" /></td>
								<td class="responsive-table-cell responsive-table-cell-bold">${order.total.formattedValue}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.createdate.label" /></td>
								<td class="responsive-table-cell">
								<div class="ordercreateDate">
								<fmt:formatDate
										value="${order.createDate}" dateStyle="medium"
										timeStyle="short" type="both" /></td></div>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.myaccount.sapcrmserviceorder.description.label" /></td>
								<td class="responsive-table-cell">${order.description}</td>
							</tr>
						</c:forEach>
					</table>
				</div>
			</div>
			<div class="account-orderhistory-pagination">
				<nav:pagination top="false"
					msgKey="text.account.serviceOrderHistory.page.sortTitle"
					showCurrentPageInfo="true" hideRefineButton="true"
					supportShowPaged="${isShowPageAllowed}"
					supportShowAll="${isShowAllAllowed}"
					searchPageData="${searchPageData}" searchUrl="${searchUrl}"
					numberPagesShown="${numberPagesShown}"
					additionalParams="${additionalParams}" />
			</div>
		</div>
	</c:otherwise>
</c:choose>
