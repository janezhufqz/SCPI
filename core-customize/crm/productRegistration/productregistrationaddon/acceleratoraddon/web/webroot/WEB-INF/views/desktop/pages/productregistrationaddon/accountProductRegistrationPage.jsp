<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/desktop/nav/breadcrumb"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="headline">
	<spring:theme text="Product Registration Details" />
</div>
<a href="register-product" class="right button positive"> <spring:theme
		text="Register a Product" />
</a>


<div class="description">
	<spring:theme text="View Registered Products" />
</div>

<table class="productRegistrationTable">
	<thead>
		<tr>
			<th><spring:theme text="Product" /></th>
			<th><spring:theme text="Registration Date" /></th>
			<th class="productRegistTableWarranty"><spring:theme
					text="Warranty Description" /></th>
			<th><spring:theme text="Warranty Status" /></th>
			<th><spring:theme text="Time To Renew" /></th>
			<th><spring:theme text="Action" /></th>

			<th></th>
		</tr>
	</thead>
	<tbody>
	</tbody>
</table>
