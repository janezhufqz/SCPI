<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="accountIBase"
	tagdir="/WEB-INF/tags/addons/sapcrmibaseaddon/responsive/account"%>
<c:if test="${isservicecontractloaded}">
	<spring:url value="${servicecontractviewurl}"
		var="servicecontractviewurl" />
</c:if>
<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapse8"><b><spring:theme
							code="text.installedBase.serviceContract" /></b></a>
			</h4>
		</div>
		<div id="collapse8" class="panel-collapse collapse">
			<c:choose>	
				<c:when test="${not empty serviceContracts}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.serviceContract.id" /></th>
									<th id="header2"><spring:theme
											code="text.installedBase.serviceContract.description" /></th>
									<th id="header3"><spring:theme
											code="text.installedBase.serviceContract.startDate" /></th>
									<th id="header4"><spring:theme
											code="text.installedBase.serviceContract.endDate" /></th>
									<th id="header5"><spring:theme
											code="text.installedBase.serviceContract.netValue" /></th>
									<th id="header6"><spring:theme
											code="text.installedBase.serviceContract.status" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${serviceContracts}" var="contract">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.id" /></td>
										<td headers="header1" class="responsive-table-cell"><a
											href="${servicecontractviewurl}${contract.contractId}"
											class="responsive-table-link <c:if test="${empty isservicecontractloaded}">service-contract-disable</c:if>"
											<c:if test="${empty isservicecontractloaded}">title="<spring:theme code="text.installedBase.serviceContract.disabledmsg" arguments="${servicecontractextensionname}"/>"</c:if>>
												<fmt:formatNumber var="contractId" pattern="#"
													value="${contract.contractId}" />
												${fn:escapeXml(contractId)}
										</a></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.description" /></td>
										<td headers="header2" class="responsive-table-cell">
											${fn:escapeXml(contract.description)}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.startDate" /></td>
										<td headers="header3" class="responsive-table-cell"><fmt:formatDate
												value="${contract.startDate}" dateStyle="long"
												timeStyle="short" type="date" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.endDate" /></td>
										<td headers="header4" class="responsive-table-cell"><fmt:formatDate
												value="${contract.endDate}" dateStyle="long"
												timeStyle="short" type="date" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.endDate" /></td>
										<td headers="header5" class="responsive-table-cell">${contract.currency.symbol}
											${contract.netValue}</td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceContract.status" /></td>
										<td headers="header6" class="responsive-table-cell">${contract.contractCondensedStatus}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.serviceContract.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

