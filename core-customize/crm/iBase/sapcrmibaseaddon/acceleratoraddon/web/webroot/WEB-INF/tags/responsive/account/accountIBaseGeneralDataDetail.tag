<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="accountIBase"
	tagdir="/WEB-INF/tags/addons/sapcrmibaseaddon/responsive/account"%>


<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapse1"><b><spring:theme
							code="text.installedBase.general" /></b></a>
			</h4>
		</div>
		<div id="collapse1" class="panel-collapse collapse in">
			<div class="item-group">
				<table>
					<tr>
						<td><b><spring:theme
									code="text.installedBase.number" /></b></td>
						<td><fmt:formatNumber var="ibasenumber" pattern="#"
								value="${iBase.number}" /> <spring:theme code="${ibasenumber}" />
						</td>
						<td><b><spring:theme
									code="text.installedBase.externalId" /></b></td>
						<td><spring:theme code="${iBase.externalId}" /></td>
					</tr>

					<tr>
						<td><b><spring:theme
									code="text.installedBase.category" /></b></td>
						<td><spring:theme code="${iBase.category}" /></td>
						<td><b><spring:theme
									code="text.installedBase.status" /></b></td>
						<td><spring:theme code="${iBase.status}" /></td>
					</tr>

					<tr>
						<td><b><spring:theme
									code="text.installedBase.description" /></b></td>
						<td><spring:theme code="${iBase.description}" /></td>

					</tr>
					<tr>
						<td colspan="4"><accountIBase:accountIBaseServiceTicket
								ibaseguid="${iBase.guid}" ibasenumber="${iBase.number}" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>
