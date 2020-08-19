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
				<a data-toggle="collapse" href="#collapse3"><b><spring:theme
							code="text.installedBase.partners.involved" /></b></a>
			</h4>
		</div>
		<div id="collapse3" class="panel-collapse collapse">
			<c:choose>
				<c:when test="${not empty iBase.partners}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.partners.function" /></th>
									<th id="header2"><spring:theme
											code="text.installedBase.partners.name" /></th>
									<th id="header3"><spring:theme
											code="text.installedBase.partners.address" /></th>
									<th id="header4"><spring:theme
											code="text.installedBase.partners.isMainPartner" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${iBase.partners}" var="partner">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.partners.function" /></td>
										<td headers="header1" class="responsive-table-cell">
											${fn:escapeXml(partner.function)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.partners.name" /></td>
										<td headers="header2" class="responsive-table-cell">
											${fn:escapeXml(partner.name)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.partners.address" /></td>
										<td headers="header3" class="responsive-table-cell">
											${fn:escapeXml(partner.address)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.partners.isMainPartner" /></td>
										<td headers="header4" class="responsive-table-cell">
											${fn:escapeXml(partner.isMainPartner)}</td>

									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.partners.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

</div>
