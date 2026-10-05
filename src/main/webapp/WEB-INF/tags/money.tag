<%@ tag body-content="empty" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="value" required="true" type="java.lang.Object" %>
<%@ attribute name="decimals" required="false" %>
&#8377;<fmt:formatNumber value="${value}" type="number" minFractionDigits="${empty decimals ? 2 : decimals}" maxFractionDigits="${empty decimals ? 2 : decimals}" groupingUsed="true"/>
