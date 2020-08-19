<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="address" required="true" type="de.hybris.platform.commercefacades.user.data.AddressData" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


	<c:if test="${not empty fn:escapeXml(address.title)}">
	    ${fn:escapeXml(address.title)}&nbsp;
	</c:if>
	${fn:escapeXml(address.firstName)}&nbsp;${fn:escapeXml(address.lastName)}
	<br>

${fn:escapeXml(address.line1)}
<c:if test="${not empty fn:escapeXml(address.line2)}">
	<br>
	${fn:escapeXml(address.line2)}
</c:if>
<br>
${fn:escapeXml(address.town)}&nbsp;${fn:escapeXml(address.region.name)}
<br>
${fn:escapeXml(address.country.name)}&nbsp;${fn:escapeXml(address.postalCode)}
