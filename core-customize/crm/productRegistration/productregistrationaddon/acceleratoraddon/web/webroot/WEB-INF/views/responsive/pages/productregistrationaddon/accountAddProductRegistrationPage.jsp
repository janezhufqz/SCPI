<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="account-section-header">
	<spring:theme
		code="text.account.productregistration.createRegisteration.heading"/>
</div>
<div class="row product-registration-body">

	<div class="col-md-10 col-xs-12 createProdRegistrationDiv ">
		<div
			class="account-section-content	 account-section-content-small ui-front ">
			<form:form method="post" modelAttribute="productRegistrationForm">
				<label class="control-label" for="productDetails"> <spring:theme
						code="text.account.productregistration.product" />
				</label>
				</br>
				<input id="productDetails" class="inputProductDetails"
					name="productDesc">
				<form:hidden path="productID" />
				<div id="product-registration-buttons" class="form-actions addProd">
					<a href="registered-products" class="btn btn-default"> <spring:theme
							code="text.account.productregistration.createProductRegistration.back"
							text="Cancel" />
					</a>
					<ycommerce:testId code="product-registration_button">
						<button class="btn btn-primary" type="submit"
							id="addProductRegistration">
							<spring:theme
								code="text.account.productregistration.createProductRegistration.submit"
								text="Submit" />
						</button>
					</ycommerce:testId>
				</div>
			</form:form>
		</div>
	</div>
</div>


