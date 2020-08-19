<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<div class="span-24">
	<div class="span-20 last">
		<div class="accountContentPane clearfix">
			<div class="headline">
				<spring:theme
					code="text.account.productregistration.createRegisteration.heading"
					text="Register Product" />
			</div>
			<div class="required right">
				<spring:theme code="form.required"
					text="Fields marked * are required" />
			</div>


			<form:form method="post" modelAttribute="productRegistrationForm">
				<table>
					<tr>
						<td><formElement:formInputBox
								idKey="text.account.productregistration.createRegisteration.purchaseDate"
								labelKey="text.account.productregistration.createRegisteration.purchaseDate"
								path="purchaseDate" inputCSS="text" mandatory="true" /></td>
					</tr>
					<tr>
						<td><formElement:formInputBox
								idKey="text.account.productregistration.createRegisteration.SerialNo"
								labelKey="text.account.productregistration.createRegisteration.SerialNo"
								path="serialNo" inputCSS="text" mandatory="true" /></td>
					</tr>

					<tr>
						<td><formElement:formInputBox
								idKey="text.account.productregistration.createRegisteration.purchaseLocation"
								labelKey="text.account.productregistration.createRegisteration.purchaseLocation"
								path="purchaseLocation" inputCSS="text" /></td>
					</tr>
					<tr>
						<td><formElement:formInputBox
								idKey="text.account.productregistration.createRegisteration.timeZonePurchasedate"
								labelKey="text.account.productregistration.createRegisteration.timeZonePurchasedate"
								path="timeZonePurchasedate" inputCSS="text" /></td>
					</tr>


				</table>

				<div id="addressform_button_panel" class="form-actions">
					<ycommerce:testId code="registerProduct_create_button">
						<button class="positive right" type="submit">
							<spring:theme
								code="text.account.productregistration.addRegisteration"
								text="Register" />
						</button>
					</ycommerce:testId>

					<a href="registered-products" class="button"> <spring:theme
							code="text.account.productregistration.createRegisteration.Back"
							text="Back" />
					</a>
				</div>
			</form:form>
		</div>
	</div>
</div>
