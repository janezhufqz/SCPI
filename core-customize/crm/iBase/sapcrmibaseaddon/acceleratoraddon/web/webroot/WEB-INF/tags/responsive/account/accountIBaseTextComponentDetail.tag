<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapse5"><b><spring:theme
							code="text.installedBase.textComponent" /></b></a>
			</h4>
		</div>
		<div id="collapse5" class="panel-collapse collapse">
			<c:choose>
				<c:when test="${not empty iBase.textComponents}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.textComponent.description" /></th>
									<th id="header2"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${iBase.textComponents}" var="textComponent">
									<c:if test="${not empty textComponent.description}">
										<tr class="responsive-table-item">
											<td class="hidden-sm hidden-md hidden-lg"><spring:theme
													code="text.installedBase.textComponent.description" /></td>
											<td headers="header1" class="responsive-table-cell">
												${fn:escapeXml(textComponent.description)}</td>
											<td class="hidden-sm hidden-md hidden-lg"></td>
											<td headers="header2" class="responsive-table-cell"></td>
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.textComponent.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
