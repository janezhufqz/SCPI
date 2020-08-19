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
				<a data-toggle="collapse" href="#collapse7"><b><spring:theme
							code="text.installedBase.iBaseComponent" /></b></a>
			</h4>
		</div>
		<div id="collapse7" class="panel-collapse collapse">
			<c:choose>
				<c:when test="${not empty iBase.ibaseComponents}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.iBaseComponent.id" /></th>
									<th id="header2"><spring:theme
											code="text.installedBase.iBaseComponent.description" /></th>
									<th id="header3"><spring:theme
											code="text.installedBase.iBaseComponent.warrantyStartDate" /></th>
									<th id="header4"><spring:theme
											code="text.installedBase.iBaseComponent.warrantyEndDate" /></th>
									<th id="header5"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${iBase.ibaseComponents}" var="iBaseComponent">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.iBaseComponent.id" /></td>
										<c:url
											value="/my-company/installed-bases/view/${iBaseComponent.ibaseId}"
											var="myAccountIBaseDetailsUrl" />
										<td headers="header1" class="responsive-table-cell">
											<a href="${myAccountIBaseDetailsUrl}"
											class="responsive-table-link"> <fmt:formatNumber
													var="ibasenumber" pattern="#"
													value="${iBaseComponent.ibaseId}" />
												${fn:escapeXml(ibasenumber)}
										</a> 
										</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.iBaseComponent.description" /></td>
										<td headers="header2" class="responsive-table-cell">
											${fn:escapeXml(iBaseComponent.description)}</td>

										<td headers="header3" class="responsive-table-cell">
											<fmt:formatDate value="${iBaseComponent.warrantyStartDate}"
											dateStyle="long" timeStyle="short" type="date" /></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.iBaseComponent.warrantyStartDate" /></td>
										<td headers="header4" class="responsive-table-cell">
											<fmt:formatDate value="${iBaseComponent.warrantyEndDate}"
											dateStyle="long" timeStyle="short" type="date" /></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.iBaseComponent.warrantyEndDate" /></td>

										<td class="hidden-sm hidden-md hidden-lg"></td>
										<td headers="header5" class="responsive-table-cell"><accountIBase:accountIBaseServiceTicket
												ibaseguid="${iBase.guid}" ibasenumber="${iBase.number}"
												componentnumber="${iBaseComponent.instanceNumber}"
												componentguid="${iBaseComponent.guid}" /></td>

									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.iBaseComponent.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
