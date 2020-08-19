<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${empty ibases}">
	<div class="row">
		<div class="col-md-6 col-md-push-3">
			<div class="account-section-content content-empty">
				<ycommerce:testId code="installedBase_noIBases_label">
					<spring:theme code="text.installedBase.noIBase" />
				</ycommerce:testId>
			</div>
		</div>
	</div>
</c:if>


<c:if test="${not empty ibases}">
	<div class="account-section-content">
		<div class="account-orderhistory">
			<div class="responsive-table">
				<table class="responsive-table">
					<thead>
						<tr class="responsive-table-head hidden-xs">
							<th id="header1"><spring:theme
									code="text.installedBase.number" /></th>
							<th id="header2"><spring:theme
									code="text.installedBase.description" /></th>
							<th id="header3"><spring:theme
									code="text.installedBase.externalId" /></th>
							<th id="header4"><spring:theme
									code="text.installedBase.category" /></th>
							<th id="header5"><spring:theme
									code="text.installedBase.status" /></th>

						</tr>
					</thead>
					<tbody>
						<c:forEach items="${ibases}" var="iBase">
							<tr class="responsive-table-item">
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.installedBase.number" /></td>
								<c:url value="/my-company/installed-bases/view/${iBase.number}"
									var="myAccountIBaseDetailsUrl" />
								<td headers="header1" class="responsive-table-cell"><a
									href="${myAccountIBaseDetailsUrl}"
									class="responsive-table-link"> <fmt:formatNumber
											var="ibasenumber" pattern="#" value="${iBase.number}" />${fn:escapeXml(ibasenumber)}
								</a></td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.installedBase.description" /></td>
								<td headers="header2" class="responsive-table-cell">
									${fn:escapeXml(iBase.description)}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.installedBase.externalId" /></td>
								<td headers="header3" class="responsive-table-cell">
									${fn:escapeXml(iBase.externalId)}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.installedBase.category" /></td>
								<td headers="header4" class="responsive-table-cell">
									${fn:escapeXml(iBase.category)}</td>
								<td class="hidden-sm hidden-md hidden-lg"><spring:theme
										code="text.installedBase.status" /></td>
								<td headers="header5" class="responsive-table-cell">
									${fn:escapeXml(iBase.status)}</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</c:if>
