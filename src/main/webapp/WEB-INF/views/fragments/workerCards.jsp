<c:choose>
  <c:when test="${empty workers}">
    <div class="card empty"><div class="big">&#128269;</div><h3>No workers match your search</h3><p class="muted">Try another skill, city or keyword, or clear the filters.</p></div>
  </c:when>
  <c:otherwise>
    <div class="worker-grid" data-count="${fn:length(workers)}">
      <c:forEach var="w" items="${workers}"><sb:workerCard w="${w}"/></c:forEach>
    </div>
  </c:otherwise>
</c:choose>
