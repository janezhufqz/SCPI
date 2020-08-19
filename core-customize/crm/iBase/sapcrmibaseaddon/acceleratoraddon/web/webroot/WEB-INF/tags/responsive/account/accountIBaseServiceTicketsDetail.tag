<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav"%>
<%@ taglib prefix="accountIBase"
	tagdir="/WEB-INF/tags/addons/sapcrmibaseaddon/responsive/account"%>
<c:if test="${isserviceticketloaded}">
	<spring:url value="${serviceticketviewurl}" var="serviceticketviewurl" />
</c:if>
<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title">
				<a data-toggle="collapse" href="#collapse9"><b><spring:theme
							code="text.installedBase.serviceTicket" /></b></a>
			</h4>
		</div>
		<div id="collapse9" class="panel-collapse collapse">
			<c:choose>
				<c:when test="${not empty ticketDataList}">
					<div class="responsive-table">
						<table class="responsive-table">
							<thead>
								<tr class="responsive-table-head hidden-xs">
									<th id="header1"><spring:theme
											code="text.installedBase.serviceTicket.id" /></th>
									<th id="header2"><spring:theme
											code="text.installedBase.serviceTicket.subject" /></th>
									<th id="header3"><spring:theme
											code="text.installedBase.serviceTicket.ticketCategory" /></th>
									<th id="header4"><spring:theme
											code="text.installedBase.serviceTicket.creationDate" /></th>
									<th id="header5"><spring:theme
											code="text.installedBase.serviceTicket.updationDate" /></th>
									<th id="header6"><spring:theme
											code="text.installedBase.serviceTicket.status" /></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="${ticketDataList}" var="ticket">
									<tr class="responsive-table-item">
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.id" /></td>
										<td headers="header1" class="responsive-table-cell"><a
											href="${serviceticketviewurl}${ticket.id}"
											class="responsive-table-link <c:if test="${empty isserviceticketloaded}">service-ticket-disable</c:if>"
											<c:if test="${empty isserviceticketloaded}">title="<spring:theme code="text.installedBase.serviceTicket.disabledmsg" arguments="${serviceticketextensionname}"/>"</c:if>>
												<c:out value="${ticket.id}" />
										</a></td>
										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.subject" /></td>
										<td headers="header2" class="responsive-table-cell"><c:out
												value="${ticket.subject}" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.ticketCategory" /></td>
										<td headers="header3" class="responsive-table-cell"><spring:message
												code="text.installedBase.serviceTicket.${ticket.ticketCategory}"
												text="${ticket.ticketCategory}" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.creationDate" /></td>
										<td headers="header4" class="responsive-table-cell"><fmt:formatDate
												value="${ticket.creationDate}" pattern="dd-MM-yy hh:mm a" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.updationDate" /></td>
										<td headers="header5" class="responsive-table-cell"><fmt:formatDate
												value="${ticket.lastModificationDate}"
												pattern="dd-MM-yy hh:mm a" /></td>

										<td class="hidden-sm hidden-md hidden-lg"><spring:theme
												code="text.installedBase.serviceTicket.status" /></td>
										<td headers="header6" class="responsive-table-cell"><spring:message
												code="text.installedBase.serviceTicket.status.${fn:toUpperCase(ticket.status.id)}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</div>
				</c:when>
				<c:otherwise>
					<p align="center">
						<i><spring:theme
								code="text.installedBase.serviceTicket.notAvailable" /></i>.
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

