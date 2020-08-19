<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="accountIBase"
	tagdir="/WEB-INF/tags/addons/sapcrmibaseaddon/responsive/account"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set value="0" var="level" />
<c:set value="0" var="loopStatus" />
<div class="account-ibase-tree">
	<div class="panel-group">
		<div class="panel panel-default">
			<div class="panel-heading">
				<h4 class="panel-title" style="margin-left: ${level*10}px;"
					title="<spring:theme
							code="text.installedBase.tooltip" />">
					<a data-toggle="collapse"
						href="#collapse_${level}_${loopStatus}_${iBase.guid}"
						class="treearrow"><c:if
							test="${not empty iBase.ibaseComponents or not empty iBase.productComponents or not empty iBase.objectComponents or not empty iBase.textComponents}">
							<span class="glyphicon glyphicon-expand"></span>
						</c:if></a>
					<c:url value="/my-company/installed-bases/view/${iBase.number}"
						var="myAccountIBaseDetailsUrl" />
					<a href="${myAccountIBaseDetailsUrl}"><b> <fmt:formatNumber
								var="ibasenumber" pattern="#" value="${iBase.number}" />
							${fn:escapeXml(ibasenumber)} - ${fn:replace(iBase.description, fn:substring(iBase.description, 30, fn:length(iBase.description)), '...')}
					</b></a>
				</h4>
			</div>
			<c:if
				test="${not empty iBase.ibaseComponents or not empty iBase.productComponents or not empty iBase.objectComponents or not empty iBase.textComponents}">

				<div id="collapse_${level}_${loopStatus}_${iBase.guid}"
					class="panel-collapse collapse collapseible-div">
					<c:forEach items="${iBase.ibaseComponents}" var="iBaseComponent">
						<accountIBase:accountIBaseComponentNode node="${iBaseComponent}"
							level="${level+1}" loopStatus="${loopStatus}" />
						<c:set value="${loopStatus+1}" var="loopStatus" />
					</c:forEach>

					<c:forEach items="${iBase.productComponents}"
						var="productComponents">
						<accountIBase:accountProductComponentNode
							node="${productComponents}" level="${level+1}"
							loopStatus="${loopStatus}" />
						<c:set value="${loopStatus+1}" var="loopStatus" />
					</c:forEach>

					<c:forEach items="${iBase.objectComponents}" var="objectComponents">
						<accountIBase:accountObjectComponentNode
							node="${objectComponents}" level="${level+1}"
							loopStatus="${loopStatus}" />
						<c:set value="${loopStatus+1}" var="loopStatus" />
					</c:forEach>

					<c:forEach items="${iBase.textComponents}" var="textComponents">
						<accountIBase:accountTextComponentNode node="${textComponents}"
							level="${level+1}" loopStatus="${loopStatus}" />
						<c:set value="${loopStatus+1}" var="loopStatus" />
					</c:forEach>
				</div>
			</c:if>
		</div>
	</div>
</div>
