<%@ attribute name="node" required="true"
	type="com.sap.hybris.crm.sapcrmibasefacades.data.TextComponentData"%>
<%@ attribute name="level" required="true" type="java.lang.Integer"%>
<%@ attribute name="loopStatus" required="true" type="java.lang.Integer"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="panel-group">
	<div class="panel panel-default">
		<div class="panel-heading">
			<h4 class="panel-title" style="margin-left: ${(level+2)*10}px;"
				title="<spring:theme
							code="text.installedBase.textComponent.tooltip" />">
				<b>${fn:escapeXml(node.description)}</b>
			</h4>
		</div>
	</div>
</div>
