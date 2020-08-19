<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common"
	tagdir="/WEB-INF/tags/addons/sapbusinessagreementaddon/responsive/common"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-orderdetail account-consignment">
	<div class="well well-quinary well-xs">
		<div class="well-content col-sm-12 col-md-12">
			<div class="row">

				<div class="col-sm-4 col-md-4">
					<div class="order-ship-to">
						<ycommerce:testId code="orderDetail_deliveryAddress_section">
							<div class="label-order">
								<spring:theme code="text.account.businessagreement.address" />
							</div>
							<div class="value-order">

								<common:agreementaddressItem
									address="${businessAgreementDetailsData.addressData}" />


							</div>
						</ycommerce:testId>
					</div>
				</div>

			</div>
		</div>
	</div>
</div>
