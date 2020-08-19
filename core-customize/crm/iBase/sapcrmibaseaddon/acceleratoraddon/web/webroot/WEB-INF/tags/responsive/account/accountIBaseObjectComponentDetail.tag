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
				<a data-toggle="collapse" href="#collapse6"><b><spring:theme
							code="text.installedBase.objectComponent" /></b></a>
			</h4>
		</div>
		<div id="collapse6" class="panel-collapse collapse">
			<c:choose>
				<c:when test="${not empty iBase.objectComponents}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.objectComponent.objectid" /></th>
									<th id="header2"><spring:theme
											code="text.installedBase.objectComponent.productid" /></th>
									<th id="header3"><spring:theme
											code="text.installedBase.objectComponent.productname" /></th>
									<th id="header4"><spring:theme
											code="text.installedBase.objectComponent.quantity" /></th>
									<th id="header5"><spring:theme
											code="text.installedBase.objectComponent.unit" /></th>
									<th id="header6"><spring:theme
											code="text.installedBase.objectComponent.warrantyStartDate" /></th>
									<th id="header7"><spring:theme
											code="text.installedBase.objectComponent.warrantyEndDate" /></th>
									<th id="header8"></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${iBase.objectComponents}"
									var="objectComponent">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.objectid" /></td>
										<td headers="header1" class="responsive-table-cell">
											${fn:escapeXml(objectComponent.objectId)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.id" /></td>
										<c:url value="${objectComponent.productUrl}" var="productUrl" />
										<td headers="header2" class="responsive-table-cell"><a
											href="${productUrl}" class="responsive-table-link">
												${fn:escapeXml(objectComponent.productId)} </a></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.name" /></td>
										<td headers="header3" class="responsive-table-cell">
											${fn:escapeXml(objectComponent.productName)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.quantity" /></td>
										<td headers="header4" class="responsive-table-cell">
											${fn:escapeXml(objectComponent.quantity)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.unit" /></td>
										<td headers="header5" class="responsive-table-cell">
											${fn:escapeXml(objectComponent.unit)}</td>

										<td headers="header6" class="responsive-table-cell">
											<fmt:formatDate value="${objectComponent.warrantyStartDate}"
											dateStyle="long" timeStyle="short" type="date" /></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.warrantyStartDate" /></td>
										<td headers="header7" class="responsive-table-cell">
											<fmt:formatDate value="${objectComponent.warrantyEndDate}"
											dateStyle="long" timeStyle="short" type="date" /></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.objectComponent.warrantyEndDate" /></td>

										<td class="hidden-sm hidden-md hidden-lg"></td>
										<td headers="header8" class="responsive-table-cell"><accountIBase:accountIBaseServiceTicket
												ibaseguid="${iBase.guid}" ibasenumber="${iBase.number}"
												componentnumber="${objectComponent.instanceNumber}"
												componentguid="${objectComponent.guid}" /></td>

									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.objectComponent.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
