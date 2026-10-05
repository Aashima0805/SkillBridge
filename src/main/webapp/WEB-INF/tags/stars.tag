<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="rating" required="true" type="java.lang.Double" %>
<%@ attribute name="count" required="false" type="java.lang.Integer" %>
<span class="stars" aria-label="Rated ${rating} out of 5"><c:forEach begin="1" end="5" var="i"><c:choose><c:when test="${rating >= i - 0.25}">&#9733;</c:when><c:otherwise><span class="off">&#9733;</span></c:otherwise></c:choose></c:forEach></span><span class="stars-text"><c:choose><c:when test="${rating > 0}"><fmt:formatNumber value="${rating}" minFractionDigits="1" maxFractionDigits="1"/></c:when><c:otherwise>New</c:otherwise></c:choose><c:if test="${count != null && count > 0}"> <span class="muted" style="font-weight:500">(${count})</span></c:if></span>
