<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="contractData" required="true"
	type="com.sap.hybris.sapservicecontract.data.ServiceContractData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="contractItemList">
	<div class="headline">
		<spring:theme code="text.page.title.serviceContractItems" text="Items" />
	</div>
	<table class="contractItemListTable">
		<thead>
			<tr>
				<th id="header2"><span class=""><spring:theme
							code="text.contract.product.detail" text="Details" /></span></th>
				<%-- <th id="header3"><spring:theme
							code="text.account.contract.item.number" text="Item Number" /></span></th> --%>

				<th id="header3"><spring:theme
						code="text.account.contract.product.qty" text="Quantity" /></th>
				<th id="header5"><spring:theme
						code="text.account.contract.product.netVal" text="Net Value" /></th>
				<%-- <th id="header4"><spring:theme code="text.account.contract.product.expectedValue"
						text="Expected Value" /></th>
				<th id="header6"><spring:theme code="text.account.contract.product.releasedQuantity"
						text="Released Qty" /></th>
				<th id="header4"><spring:theme code="text.account.contract.product.releasedValue"
						text="Released Value" /></th>
				<th id="header4"><spring:theme code="text.account.contract.product.targetValue"
						text="Target Value" /></th> --%>
				<th id="header6"><spring:theme
						code="text.account.contract.product.serviceProfile"
						text="Service Profile" /></th>
				<th id="header5"><spring:theme
						code="text.account.contract.product.responseProfile"
						text="Response Profile" /></th>
				<th id="header5"><spring:theme
						code="text.account.contract.product.validity" text="Validity" /></th>
				<th id="header6"><spring:theme
						code="text.account.contract.status" text="Status" /></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${contractData.items}" var="entry">
				<c:url value="${entry.product.url}" var="productUrl" />
				<tr class="item">
					<td headers="header2" class="thumb"><a href="${productUrl}">
							<product:productPrimaryImage product="${entry.product}"
								format="thumbnail" />
					</a> <ycommerce:testId code="contractDetails_productName_link">
							<div class="itemName">
								<a href="${entry.product.purchasable ? productUrl : ''}">${entry.productId}</a>
							</div>
						</ycommerce:testId></td>

					<%-- <td headers="header3" class="quantity"><ycommerce:testId
							code="contractDetails_product_quantity_label">${entry.itemNumber} </ycommerce:testId> --%>

					<td headers="header3" class="quantity"><ycommerce:testId
							code="contractDetails_product_quantity_label">${entry.quantity}<spring:message
								code="${fn:toUpperCase(entry.productUnit)}" />
						</ycommerce:testId></td>
					<%-- <td headers="header2" class="quantity"><ycommerce:testId
							code="contractDetails_product_unit_label">
							<spring:message code="${fn:toUpperCase(entry.unit)}" />
						</ycommerce:testId></td> --%>
					<td headers="header5" class="total"><ycommerce:testId
							code="contractDetails_productTotalPrice_label">
							<c:out value="${entry.currency.symbol} ${entry.netValue}" />
						</ycommerce:testId></td>
					<%-- <td headers="header4" class="total"><ycommerce:testId
							code="contractDetails_productExpectedPrice_label">
							<c:out value="${entry.currency.symbol} ${entry.expectedValue}" />
						</ycommerce:testId></td>
					<td headers="header3" class="quantity">
					<c:if test="${not empty entry.releasedQuantity }"><ycommerce:testId
							code="contractDetails_product_released_quantity_label">${entry.releasedQuantity} &nbsp <spring:message code="${fn:toUpperCase(entry.unit)}" /></ycommerce:testId>
					</c:if></td>	
					<td headers="header4" class="total"><c:if test="${not empty entry.releasedValue }"><ycommerce:testId
							code="contractDetails_product_ReleasedPrice_label">
							<c:out value="${entry.currency.symbol} ${entry.releasedValue}" />
						</ycommerce:testId></c:if></td>
					<td headers="header4" class="total"><c:if test="${not empty entry.targetValue}"><ycommerce:testId
							code="contractDetails_product_TargetPrice_label">
							<c:out value="${entry.currency.symbol} ${entry.targetValue}" />
						</ycommerce:testId></c:if></td> --%>
					<td headers="header6" class="total"><ycommerce:testId
							code="contractDetails_product_service_profile_label">
							<c:out value="${entry.serviceProfile}" />
						</ycommerce:testId></td>
					<td headers="header5" class="total"><ycommerce:testId
							code="contractDetails_product_response_profile_label">
							<c:out value="${entry.responseProfile}" />
						</ycommerce:testId></td>
					<td headers="header5" class="total"><ycommerce:testId
							code="contractDetails_product_valid_from_label">
							<fmt:formatDate value="${entry.validFrom}" dateStyle="MEDIUM"
								timeStyle="short" type="date" />
						</ycommerce:testId> <c:out value=" - " /> <ycommerce:testId
							code="contractDetails_product_valid_to_label">
							<fmt:formatDate value="${entry.validTo}" dateStyle="MEDIUM"
								timeStyle="short" type="date" />
						</ycommerce:testId></td>
					<td headers="header6" class="details"><ycommerce:testId
							code="productDetails_product_status_label">
							<spring:message code="${entry.itemStatus}" />
						</ycommerce:testId></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

</div>

