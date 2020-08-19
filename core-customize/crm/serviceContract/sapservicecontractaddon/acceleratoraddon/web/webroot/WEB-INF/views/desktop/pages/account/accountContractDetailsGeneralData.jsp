<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="contract"
	tagdir="/WEB-INF/tags/addons/sapservicecontractaddon/desktop/contract"%>
<c:if test="${empty backendError}">
	<div class="contractBoxes clearfix">

		<contract:contractGeneralData contract="${contractData}" />
		<contract:contractNotesData contract="${contractData}" />
	</div>
</c:if>
