<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="My bookings"/></jsp:include>
<section class="page-hero"><div class="container"><div class="crumbs"><a href="${ctx}/home">Home</a> / My bookings</div><h1>My bookings</h1><p>Track your requests, payments and reviews.</p></div></section>
<div class="container pull-up">
  <c:choose>
    <c:when test="${empty bookings}">
      <div class="card empty"><div class="big">&#128197;</div><h3>No bookings yet</h3><p class="muted">Find a trusted worker and make your first booking.</p><a class="btn btn-primary" href="${ctx}/workers">Find workers</a></div>
    </c:when>
    <c:otherwise>
      <c:forEach var="b" items="${bookings}">
        <article class="card booking-card ${b.id == newId ? 'is-new' : ''}">
          <div class="ico">${b.skillIcon}</div>
          <div>
            <h3>${fn:escapeXml(b.workerName)} <span class="small muted">&middot; ${fn:escapeXml(b.skillName)}</span></h3>
            <div class="booking-meta">
              <span>&#128197; ${b.dateLabel}</span><span>&#9200; ${b.startLabel} &ndash; ${b.endLabel}</span><span>&#128205; ${fn:escapeXml(b.address)}</span>
              <span>Booking #${b.id}</span>
              <c:if test="${not empty b.txnRef}"><span>${b.paymentMethod} &middot; ${b.txnRef} &middot; ${b.paymentStatus == 'REFUNDED' ? 'Refunded' : 'Paid'}</span></c:if>
              <c:if test="${b.status != 'CANCELLED'}"><span>&#128222; ${b.workerPhone}</span></c:if>
            </div>
            <c:if test="${b.status == 'COMPLETED'}">
              <c:choose>
                <c:when test="${b.reviewed}"><div class="mt-1 small">Your review: <sb:stars rating="${b.reviewRating + 0.0}"/></div></c:when>
                <c:otherwise>
                  <details class="review-box"><summary>&#11088; Rate this job</summary>
                    <form action="${ctx}/review" method="post" class="mt-1">
                      <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="bookingId" value="${b.id}">
                      <div class="star-input" role="radiogroup" aria-label="Rating">
                        <c:forEach begin="1" end="5" var="i"><c:set var="n" value="${6 - i}"/><input type="radio" id="st${b.id}_${n}" name="rating" value="${n}"><label for="st${b.id}_${n}" title="${n} star${n > 1 ? 's' : ''}">&#9733;</label></c:forEach>
                      </div>
                      <div class="form-group mt-1"><textarea name="comment" rows="2" maxlength="300" placeholder="Share your experience (optional)"></textarea></div>
                      <button class="btn btn-primary btn-sm" type="submit">Post review</button>
                    </form>
                  </details>
                </c:otherwise>
              </c:choose>
            </c:if>
          </div>
          <div class="booking-actions">
            <span class="status status-${b.statusClass}">${b.statusLabel}</span>
            <strong class="price" style="font-size:1.1rem"><sb:money value="${b.total}"/></strong>
            <c:if test="${b.status == 'PENDING' || b.status == 'ACCEPTED'}">
              <form action="${ctx}/booking/action" method="post" data-confirm="Cancel this booking? The payment will be refunded.">
                <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="cancel">
                <button class="btn btn-danger btn-sm" type="submit">Cancel &amp; refund</button>
              </form>
            </c:if>
          </div>
        </article>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</div>
<jsp:include page="fragments/footer.jsp"/>
