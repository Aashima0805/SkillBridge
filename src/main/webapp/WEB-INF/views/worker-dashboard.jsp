<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Worker dashboard"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / Dashboard</div>
    <h1>Welcome back, ${fn:escapeXml(fn:substringBefore(worker.fullName, ' '))}</h1>
    <p>Manage booking requests, track your earnings and control when customers can book you.</p>
  </div>
</section>

<div class="container pull-up" style="padding-bottom:1rem">

  <c:if test="${!worker.verified}">
    <div class="alert alert-warning"><span>&#9203;</span><span>Your profile is awaiting admin verification. You will appear in customer search results once it is approved.</span></div>
  </c:if>

  <div class="stat-grid">
    <div class="card stat-card t-amber"><div class="ico">&#9203;</div><div><b>${pendingCount}</b><span>Pending requests</span></div></div>
    <div class="card stat-card t-blue"><div class="ico">&#128197;</div><div><b>${acceptedCount}</b><span>Accepted jobs</span></div></div>
    <div class="card stat-card t-green"><div class="ico">&#9989;</div><div><b>${completedCount}</b><span>Completed jobs</span></div></div>
    <div class="card stat-card t-teal"><div class="ico">&#8377;</div><div><b><sb:money value="${earnings}"/></b><span>Total earnings</span></div></div>
  </div>

  <div class="card mt-3">
    <div class="card-body row row-between">
      <div class="row" style="gap:1rem">
        <sb:avatar initials="${worker.initials}" seed="${worker.id}"/>
        <div>
          <h3 class="mb-0">${fn:escapeXml(worker.fullName)}</h3>
          <div class="muted small">${worker.skillIcon} ${fn:escapeXml(worker.skillName)} &middot; ${fn:escapeXml(worker.city)} &middot; ${worker.workHoursLabel} &middot; <sb:money value="${worker.hourlyRate}" decimals="0"/>/hr</div>
          <div class="mt-1"><sb:stars rating="${worker.avgRating}" count="${worker.reviewCount}"/>
            <c:if test="${worker.verified}"> &nbsp;<span class="badge badge-teal">Verified</span></c:if></div>
        </div>
      </div>
      <div class="row" style="gap:1.2rem">
        <form method="post" action="${ctx}/worker/dashboard" id="availabilityForm">
          <input type="hidden" name="_csrf" value="${csrf}">
          <input type="hidden" name="available" value="${worker.available ? 'false' : 'true'}">
          <label class="switch mb-0" title="Turn off to stop receiving new bookings">
            <input type="checkbox" ${worker.available ? 'checked' : ''} onchange="this.form.submit()" aria-label="Accepting new bookings">
            <i></i>
            <span>${worker.available ? 'Accepting bookings' : 'Not accepting bookings'}</span>
          </label>
          <noscript><button type="submit" class="btn btn-outline btn-sm">Apply</button></noscript>
        </form>
        <a class="btn btn-outline btn-sm" href="${ctx}/worker/profile">Edit profile</a>
      </div>
    </div>
  </div>

  <div class="row row-between mt-4 mb-2">
    <h2 class="mb-0" style="font-size:1.4rem">Booking requests</h2>
    <span class="muted small">${fn:length(bookings)} total</span>
  </div>

  <c:choose>
    <c:when test="${empty bookings}">
      <div class="card empty"><div class="big">&#128236;</div><h3>No bookings yet</h3><p class="muted">When customers book you, their requests will show up here.</p></div>
    </c:when>
    <c:otherwise>
      <c:forEach var="b" items="${bookings}">
        <article class="card booking-card ${b.status == 'PENDING' ? 'is-new' : ''}">
          <div class="ico">${b.skillIcon}</div>
          <div>
            <div class="row" style="gap:.6rem">
              <h3>#${b.id} &middot; ${fn:escapeXml(b.customerName)}</h3>
              <span class="status status-${b.statusClass}">${b.statusLabel}</span>
            </div>
            <div class="booking-meta">
              <span>&#128222; ${fn:escapeXml(b.customerPhone)}</span>
              <span>&#128197; ${b.dateLabel}</span>
              <span>&#128339; ${b.startLabel} &ndash; ${b.endLabel} (${b.hours} hr)</span>
              <span>&#128176; <sb:money value="${b.total}"/></span>
            </div>
            <div class="booking-meta">
              <span>&#128205; ${fn:escapeXml(b.address)}</span>
              <c:if test="${not empty b.notes}"><span>&#128221; ${fn:escapeXml(b.notes)}</span></c:if>
            </div>
          </div>
          <div class="booking-actions">
            <c:if test="${b.status == 'PENDING'}">
              <div class="row" style="gap:.4rem">
                <form method="post" action="${ctx}/booking/action">
                  <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="accept"><input type="hidden" name="back" value="/worker/dashboard">
                  <button type="submit" class="btn btn-success btn-sm">Accept</button>
                </form>
                <form method="post" action="${ctx}/booking/action" data-confirm="Decline this request? The customer will be refunded.">
                  <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="reject"><input type="hidden" name="back" value="/worker/dashboard">
                  <button type="submit" class="btn btn-danger btn-sm">Decline</button>
                </form>
              </div>
            </c:if>
            <c:if test="${b.status == 'ACCEPTED'}">
              <div class="row" style="gap:.4rem">
                <form method="post" action="${ctx}/booking/action" data-confirm="Mark this job as completed?">
                  <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="complete"><input type="hidden" name="back" value="/worker/dashboard">
                  <button type="submit" class="btn btn-primary btn-sm">Mark completed</button>
                </form>
                <form method="post" action="${ctx}/booking/action" data-confirm="Cancel this booking? The customer will be refunded.">
                  <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="cancel"><input type="hidden" name="back" value="/worker/dashboard">
                  <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                </form>
              </div>
            </c:if>
            <c:if test="${b.status == 'COMPLETED'}">
              <c:choose>
                <c:when test="${b.reviewed}"><span class="small muted">Customer rated you ${b.reviewRating} &#9733;</span></c:when>
                <c:otherwise><span class="small muted">Awaiting review</span></c:otherwise>
              </c:choose>
            </c:if>
            <span class="small muted">Booked ${b.createdLabel}</span>
          </div>
        </article>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</div>

<jsp:include page="fragments/footer.jsp"/>
