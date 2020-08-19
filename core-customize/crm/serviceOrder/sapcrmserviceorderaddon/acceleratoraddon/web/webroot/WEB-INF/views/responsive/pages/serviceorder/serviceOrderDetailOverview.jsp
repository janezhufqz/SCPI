<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="orderinfo" tagdir="/WEB-INF/tags/addons/sapcrmserviceorderaddon/responsive/serviceorder" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<div class="well-lg well well-tertiary clearfix">
    <ycommerce:testId code="orderDetail_overview_section">
        <orderinfo:serviceOrderDetailsOverview orderData="${orderData}"/>
    </ycommerce:testId>
</div>
