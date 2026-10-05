<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ attribute name="initials" required="true" %>
<%@ attribute name="seed" required="false" type="java.lang.Integer" %>
<%@ attribute name="size" required="false" %>
<span class="avatar c${((empty seed ? 0 : seed) % 5) + 1} ${size}">${fn:escapeXml(initials)}</span>
