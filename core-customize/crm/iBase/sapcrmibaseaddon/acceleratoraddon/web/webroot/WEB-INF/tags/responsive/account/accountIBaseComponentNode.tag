<%@ attribute name="node" required="true"
	type="com.sap.hybris.crm.sapcrmibasefacades.data.IBaseComponentData"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%@ taglib prefix="accountIBase"
	tagdir="/WEB-INF/tags/addons/sapcrmibaseaddon/responsive/account"%>
<%@ attribute name="level" required="true" type="java.lang.Integer"%>
<%@ attribute name="loopStatus" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<c:choose>
	<c:when
		test="${not empty node.ibase.ibaseComponents or not empty node.ibase.productComponents or not empty node.ibase.objectComponents or not empty node.ibase.textComponents}">
		<c:set var="haveChild" value="true" />
	</c:when>
</c:choose>

<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title"
				style="margin-left: ${haveChild eq 'true' ? level*10 : (level+2)*10 }px;"
				title="<spring:theme
							code="text.installedBase.iBaseComponent.tooltip" />">
				<a data-toggle="collapse"
					href="#collapse_${level}_${loopStatus}_${node.guid}"
					class="treearrow"><c:if test="${haveChild eq 'true'}">
						<span class="glyphicon glyphicon-expand"></span>
					</c:if></a>
				<c:url value="/my-company/installed-bases/view/${node.ibaseId}"
					var="myAccountIBaseDetailsUrl" />
				<a href="${myAccountIBaseDetailsUrl}"><b><fmt:formatNumber
							var="ibasenumber" pattern="#" value="${node.ibaseId}" />
						${fn:escapeXml(ibasenumber)} - ${fn:replace(node.description, fn:substring(node.description, 30, fn:length(node.description)), '...')}</b></a>
			</h4>
		</div>
		<c:if
			test="${not empty node.ibase.ibaseComponents or not empty node.ibase.productComponents or not empty node.ibase.objectComponents or not empty node.ibase.textComponents}">
			<div id="collapse_${level}_${loopStatus}_${node.guid}"
				class="panel-collapse collapse collapseible-div">
				<c:set value="0" var="loopStatus" />
				<c:forEach items="${node.ibase.ibaseComponents}"
					var="iBaseComponent">
					<accountIBase:accountIBaseComponentNode node="${iBaseComponent}"
						level="${level+1}" loopStatus="${loopStatus}" />
					<c:set value="${loopStatus+1}" var="loopStatus" />
				</c:forEach>
				<c:forEach items="${node.ibase.productComponents}"
					var="productComponents">
					<accountIBase:accountProductComponentNode
						node="${productComponents}" level="${level+1}"
						loopStatus="${loopStatus}" />
					<c:set value="${loopStatus+1}" var="loopStatus" />
				</c:forEach>

				<c:forEach items="${node.ibase.objectComponents}"
					var="objectComponents">
					<accountIBase:accountObjectComponentNode node="${objectComponents}"
						level="${level+1}" loopStatus="${loopStatus}" />
					<c:set value="${loopStatus+1}" var="loopStatus" />
				</c:forEach>
				<c:forEach items="${node.ibase.textComponents}" var="textComponents">
					<accountIBase:accountTextComponentNode node="${textComponents}"
						level="${level+1}" loopStatus="${loopStatus}" />
					<c:set value="${loopStatus+1}" var="loopStatus" />
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>
