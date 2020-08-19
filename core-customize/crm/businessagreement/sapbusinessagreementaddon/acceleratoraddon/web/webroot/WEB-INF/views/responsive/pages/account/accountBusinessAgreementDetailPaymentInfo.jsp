<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common"
	tagdir="/WEB-INF/tags/addons/sapbusinessagreementaddon/responsive/common"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-orderdetail account-consignment">
	<div class="well well-quinary well-xs">
		<div class="well-headline">
			<spring:theme code="text.account.businessagreement.paymentvia" />
		</div>

		<div class="well-content">
			<div class="col-xs-12 col-sm-12 col-md-8 col-lg-7 col-no-padding">

				<div class="row">


					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme code="text.account.businessagreement.paymentmethod" />
						</div>
						<div class="item-value"></div>
					</div>
					<div class="col-xs-7 col-sm-7  col-md-7 col-lg-7">

						<div class="">${businessAgreementDetailsData.paymentVia.paymentMethod}</div>
						<div class="item-value"></div>
					</div>

				</div>
				<div class="row">
					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme
								code="text.account.businessagreement.alternativepayer" />
						</div>
						<div class="item-value"></div>
					</div>
					<div class="col-xs-7 col-sm-7  col-md-7 col-lg-7">

						<div class="">${businessAgreementDetailsData.paymentVia.alternativePayerDesc}</div>
						<div class="item-value"></div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme code="text.account.businessagreement.address" />
						</div>
						<div class="">
							<common:agreementaddressItem
								address="${businessAgreementDetailsData.paymentVia.alternativePayerAddress}" />
						</div>
						<div class="item-value"></div>
					</div>
				</div>

			</div>
		</div>
	</div>
</div>


<div class="account-orderdetail account-consignment">
	<div class="well well-quinary well-xs">
		<div class="well-headline">
			<spring:theme code="text.account.businessagreement.refundvia" />
		</div>

		<div class="well-content">
			<div class="col-xs-12 col-sm-12 col-md-8 col-lg-7 col-no-padding">
				<div class="row">
					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme code="text.account.businessagreement.paymentmethod" />
						</div>
						<div class="item-value"></div>
					</div>
					<div class="col-xs-7 col-sm-7  col-md-7 col-lg-7">

						<div class="">${businessAgreementDetailsData.refundVia.paymentMethod}</div>
						<div class="item-value"></div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme
								code="text.account.businessagreement.paymentrecipient" />
						</div>
						<div class="item-value"></div>
					</div>
					<div class="col-xs-7 col-sm-7  col-md-7 col-lg-7">

						<div class="">${businessAgreementDetailsData.refundVia.alternativePayerDesc}</div>
						<div class="item-value"></div>
					</div>
				</div>
				<div class="row">
					<div class="col-xs-5 col-sm-5 col-md-5 col-lg-5 ">
						<div class="businessAgreementDetailHeader">
							<spring:theme code="text.account.businessagreement.address" />
						</div>
						<div class="">
							<common:agreementaddressItem
								address="${businessAgreementDetailsData.refundVia.alternativePayerAddress}" />
						</div>
						<div class="item-value"></div>
					</div>
				</div>

			</div>
		</div>
	</div>
</div>

