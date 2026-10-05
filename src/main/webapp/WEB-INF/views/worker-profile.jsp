<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="${worker.fullName}"/></jsp:include>
<section class="page-hero"><div class="container"><div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/workers">Find workers</a> / ${fn:escapeXml(worker.fullName)}</div><h1>${fn:escapeXml(worker.fullName)}</h1><p>${worker.skillIcon} ${fn:escapeXml(worker.skillName)} in ${fn:escapeXml(worker.city)}</p></div></section>
<div class="container pull-up">
  <c:if test="${!worker.verified}"><div class="alert alert-warning">This profile is awaiting admin verification and is not visible to customers yet.</div></c:if>
  <div class="profile-grid">
    <div>
      <div class="card card-body">
        <div class="profile-head">
          <sb:avatar initials="${worker.initials}" seed="${worker.id}" size="avatar-lg"/>
          <div>
            <h1>${fn:escapeXml(worker.fullName)}</h1>
            <div class="row" style="gap:.6rem">
              <span class="badge badge-teal">${worker.skillIcon} ${fn:escapeXml(worker.skillName)}</span>
              <c:if test="${worker.verified}"><span class="verified"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l2.4 2.1 3.2-.3 1 3 2.8 1.7-.9 3.1.9 3.1-2.8 1.7-1 3-3.2-.3L12 22l-2.4-2.1-3.2.3-1-3-2.8-1.7.9-3.1-.9-3.1 2.8-1.7 1-3 3.2.3z"/><path d="M8.5 12.2l2.4 2.4 4.6-4.8" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>Verified by SkillBridge</span></c:if>
              <c:if test="${!worker.available}"><span class="badge badge-neutral">Not taking bookings</span></c:if>
            </div>
            <div class="mt-1"><sb:stars rating="${worker.avgRating}" count="${worker.reviewCount}"/></div>
          </div>
        </div>
        <div class="kv">
          <div><b><sb:money value="${worker.hourlyRate}" decimals="0"/></b><span>per hour</span></div>
          <div><b>${worker.experienceYears} yrs</b><span>experience</span></div>
          <div><b>${worker.jobsDone}</b><span>jobs completed</span></div>
          <div><b>${worker.workHoursLabel}</b><span>working hours</span></div>
        </div>
        <h3 class="mt-3">About</h3>
        <p>${fn:escapeXml(worker.bio)}</p>
        <p class="small muted mb-0">&#128205; ${fn:escapeXml(worker.city)} &middot; Member since ${worker.joinedLabel}</p>
      </div>

      <div class="card mt-3">
        <div class="card-head"><h3>Reviews</h3><sb:stars rating="${worker.avgRating}" count="${worker.reviewCount}"/></div>
        <div class="card-body">
          <c:choose>
            <c:when test="${empty reviews}"><p class="muted mb-0">No reviews yet. Be the first to book and review.</p></c:when>
            <c:otherwise>
              <c:forEach var="r" items="${reviews}">
                <div class="review"><sb:avatar initials="${r.initials}" seed="${r.customerId}" size="avatar-sm"/>
                  <div><strong>${fn:escapeXml(r.customerName)}</strong> <span class="small muted">&middot; ${r.createdLabel}</span><div><sb:stars rating="${r.rating + 0.0}"/></div><p>${fn:escapeXml(r.comment)}</p></div></div>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>

    <aside class="card card-body sticky-card">
      <h3>Book ${fn:escapeXml(fn:substringBefore(worker.fullName, ' '))}</h3>
      <c:choose>
        <c:when test="${canBook}">
          <form id="bookingForm" action="${ctx}/book" method="post" data-validate data-rate="${worker.hourlyRate}" data-worker="${worker.id}">
            <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="workerId" value="${worker.id}">
            <div class="form-group"><label for="bdate">Date</label><input id="bdate" type="date" name="date" min="${minDate}" max="${maxDate}" required></div>
            <div class="form-row">
              <div class="form-group"><label for="bstart">Start time</label>
                <select id="bstart" name="start" required><option value="">Select</option>
                  <c:forEach var="t" items="${worker.timeOptions}"><option value="${t}">${t}</option></c:forEach>
                </select></div>
              <div class="form-group"><label for="bhours">Hours</label>
                <select id="bhours" name="hours" required>
                  <c:forEach begin="1" end="8" var="h"><option value="${h}" ${h == 2 ? 'selected' : ''}>${h} hour${h > 1 ? 's' : ''}</option></c:forEach>
                </select></div>
            </div>
            <div class="form-group"><label for="baddr">Work address</label><textarea id="baddr" name="address" rows="2" data-rule="length" data-min="10" data-max="200" data-msg="Enter the full address (10 to 200 characters)." placeholder="House no., street, area, landmark"></textarea></div>
            <div class="form-group"><label for="bnotes">Notes <span class="muted">(optional)</span></label><input id="bnotes" type="text" name="notes" maxlength="200" placeholder="Describe the problem"></div>
            <div id="slotMsg" class="slot-msg wait">Choose a date, start time and duration to check availability.</div>
            <div class="price-box">
              <div class="price-line"><span>Worker charge</span><span id="pbSub">&#8377;0.00</span></div>
              <div class="price-line"><span>Platform fee (5%)</span><span id="pbFee">&#8377;0.00</span></div>
              <div class="price-line total"><span>Total</span><span id="pbTotal">&#8377;0.00</span></div>
            </div>
            <button id="bookBtn" class="btn btn-primary btn-block btn-lg mt-2" type="submit">Continue to payment</button>
          </form>
        </c:when>
        <c:when test="${empty sessionScope.user}">
          <p class="muted">Log in as a customer to check availability and book.</p>
          <a class="btn btn-primary btn-block" href="${ctx}/login?next=${'/profile?id='}${worker.id}">Log in to book</a>
          <a class="btn btn-ghost btn-block mt-1" href="${ctx}/register">Create a free account</a>
        </c:when>
        <c:otherwise><div class="alert alert-info mb-0">Only customer accounts can book workers.</div></c:otherwise>
      </c:choose>
      <p class="secure-note mt-2">&#128274; Payments are simulated and never charge real money.</p>
    </aside>
  </div>
  <c:if test="${not empty recent}">
    <h3 class="mt-4">Recently viewed</h3>
    <div class="worker-grid" style="grid-template-columns:repeat(auto-fit,minmax(280px,1fr))"><c:forEach var="w" items="${recent}"><sb:workerCard w="${w}"/></c:forEach></div>
  </c:if>
</div>
<jsp:include page="fragments/footer.jsp"/>
