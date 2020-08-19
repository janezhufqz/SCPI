<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapse2"><b><spring:theme
							code="text.installedBase.nameandaddress" /></b></a>
			</h4>
		</div>
		<div id="collapse2" class="panel-collapse collapse">
			<div class="item-group">
				<table>
					<tr>
						<td><b><spring:theme
									code="text.installedBase.name" /></b></td>
						<td><spring:theme code="${iBase.name}" /></td>
						<td><b><spring:theme
									code="text.installedBase.additionalName" /></b></td>
						<td><spring:theme code="${iBase.additionalName}" /></td>
					</tr>

					<tr>
						<td><b><spring:theme
									code="text.installedBase.streetnameandnumber" /></b></td>
						<td>${iBase.streetname}<c:if
								test="${not empty iBase.streetnumber}">&nbsp;&#47;&nbsp;${iBase.streetnumber}</c:if>

						</td>
						<td><b><spring:theme
									code="text.installedBase.postalcodeandcity" /></b></td>
						<td>${iBase.postalcode}<c:if test="${not empty iBase.city}">&nbsp;&#47;&nbsp;${iBase.city}</c:if>
						</td>
					</tr>

					<tr>
						<td><b><spring:theme
									code="text.installedBase.country" /></b></td>
						<td><spring:theme code="${iBase.country}" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>
