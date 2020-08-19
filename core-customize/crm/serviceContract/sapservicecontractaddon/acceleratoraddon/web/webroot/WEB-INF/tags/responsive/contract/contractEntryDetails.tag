<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="item" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceProductData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ attribute name="contract" required="true"
	type="com.sap.hybris.crm.sapservicecontract.data.ServiceContractData"%>
<spring:url value="/my-account/service-contract-detail/renewItem/"
	var="contractItemRenewalUrl" />
<spring:url value="/my-account/service-contract-detail/terminate/"
	var="contractTerminateUrl" />
<ul>
	<li class="product-item">
		<div class="col-md-2 col-sm-3 " style="height: 140px;">
			<div class="thumb">
				<div class="contractItemIcon">
					<a href="${item.product.url }"> <product:productPrimaryImage
							product="${item.product}" format="thumbnail" />
					</a>

				</div>
			</div>
		</div>

		<div class="col-md-10 col-sm-9">
			<div class="col-sm-8">
				<div class="details">
					<div class="name">
						<a href="#">${item.productId}</a>
					</div>
				</div>
				<div class="price">
					<spring:theme code="text.account.contract.product.qty" />
					&#58; &nbsp;
					<c:if test="${not empty item.quantity}">
                    ${item.quantity} &nbsp ${item.productUnit }
                    </c:if>
					<br>
					<spring:theme code="text.account.contract.product.netVal" />
					&#58; &nbsp;
					<c:if test="${not empty item.netValue}">
                    ${item.currency.symbol } ${item.netValue } </c:if>
					<br>
					<spring:theme code="text.account.contract.product.expectedValue" />
					&#58; &nbsp;
					<c:if test="${not empty item.expectedValue}">${item.currency.symbol } ${item.expectedValue }</c:if>
					<br>
					<spring:theme code="text.account.contract.product.releasedQuantity" />
					&#58; &nbsp;
					<c:if test="${not empty item.releasedQuantity}">${item.releasedQuantity} &nbsp ${item.productUnit }</c:if>
					<br>
					<spring:theme code="text.account.contract.product.releasedValue" />
					&#58; &nbsp;
					<c:if test="${not empty item.releasedValue}">${item.currency.symbol } ${item.releasedValue}</c:if>
					<br>
					<spring:theme code="text.account.contract.product.targetValue" />
					&#58; &nbsp;
					<c:if test="${not empty item.targetValue}">${item.currency.symbol } ${item.targetValue }</c:if>
					<br> <span> <spring:theme
							code="text.account.contract.product.responseProfile" /> &#58;
						&nbsp; ${item.responseProfile}<br> <spring:theme
							code="text.account.contract.product.serviceProfile" /> &#58;
						&nbsp; ${item.serviceProfile}
					</span>
				</div>

			</div>
			<div class="col-sm-4">
				<div class="item-status">
					<spring:theme code="text.account.contract.product.status" />
					&#58; ${item.itemStatus }
					<div>
						<spring:theme code="text.account.contract.product.validity" />
						&#58;
						<fmt:formatDate value="${item.validFrom}" dateStyle="medium"
							timeStyle="short" type="date" />
						&#45;
						<fmt:formatDate value="${item.validTo}" dateStyle="medium"
							timeStyle="short" type="date" />
					</div>
				</div>


				<div class="contractItemBtn">
					<div class="col-xs-4 col-sm-12 col-md-12 contractItemRenewBtn">
						<c:choose>
							<c:when test="${not empty item.isRenewable}">
								<c:choose>
									<c:when test="${item.isRenewable eq true }">
										<form:form id="renewContractItemForm" class="contractEditForm"
											action="${contractItemRenewalUrl}" method="post">
											<input type="hidden" name="contractId"
												value="${contract.contractId}" />
											<input type="hidden" name="contractGuid"
												value="${contract.contractGuid}" />
											<input type="hidden" name="productId"
												value="${item.productId}" />
											<input type="hidden" name="itemGuid" value="${item.itemGuid}" />
											<button type="submit"
												class="btn btn-primary contractbtn-block contractItemBtn">
												<spring:theme code="text.account.servicecontract.renew"
													text="Renew" />
											</button>
										</form:form>


									</c:when>
									<c:otherwise>
										<button type="submit"
											class="btn btn-primary contractbtn-block contractButtonDisabled contractItemBtn">
											<spring:theme code="text.account.servicecontract.renew"
												text="Renew" />
										</button>

									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<button type="submit"
									class="btn btn-primary contractbtn-block contractButtonDisabled contractItemBtn">
									<spring:theme code="text.account.servicecontract.renew"
										text="Renew" />
								</button>

							</c:otherwise>
						</c:choose>
					</div>

					<div class="col-xs-4 col-sm-12 col-md-12">
						<c:choose>
							<c:when test="${not empty item.isTerminable}">
								<c:choose>
									<c:when test="${item.isTerminable eq true}">
										<form:form id="terminateContractItemForm" class="contractEditForm"
											action="${contractTerminateUrl}" method="post">
											<input type="hidden" name="contractId"
												value="${contract.contractId}" />
											<input type="hidden" name="contractGuid"
												value="${contract.contractGuid}" />
											<input type="hidden" name="productId"
												value="${item.productId}" />
											<input type="hidden" name="itemGuid" value="${item.itemGuid}" />
											<button type="submit"
												class="btn btn-primary contractbtn-block contractItemBtn">
												<spring:theme code="text.account.servicecontract.terminate"
													text="Terminate" />
											</button>
										</form:form>
									</c:when>
									<c:otherwise>
										<div class="status">
											<button type="submit"
												class="btn btn-primary contractbtn-block contractButtonDisabled contractItemBtn">
												<spring:theme code="text.account.servicecontract.terminate"
													text="Terminate" />
											</button>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
							<c:otherwise>
								<div class="status">
									<button type="submit"
										class="btn btn-primary contractbtn-block contractButtonDisabled contractItemBtn">
										<spring:theme code="text.account.servicecontract.terminate"
											text="Terminate" />
									</button>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</li>
</ul>
