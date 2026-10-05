<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Bookings and payments"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/admin">Admin</a> / Bookings</div>
    <h1>Bookings &amp; payments</h1>
    <p>Every booking with its simulated payment. Cancelling a pending or accepted booking refunds the customer automatically.</p>
  </div>
</section>

<div class="container pull-up">
  <div class="admin-layout">
      <aside class="card side-nav" aria-label="Admin navigation">
        <a href="${ctx}/admin" class="${navPath == '/admin' ? 'active' : ''}">&#128202; Overview</a>
        <a href="${ctx}/admin/workers" class="${navPath == '/admin/workers' ? 'active' : ''}">&#128119; Workers</a>
        <a href="${ctx}/admin/skills" class="${navPath == '/admin/skills' ? 'active' : ''}">&#128736; Skills</a>
        <a href="${ctx}/admin/users" class="${navPath == '/admin/users' ? 'active' : ''}">&#128101; Users</a>
        <a href="${ctx}/admin/bookings" class="${navPath == '/admin/bookings' ? 'active' : ''}">&#128179; Bookings</a>
      </aside>
    <div>
      <div class="stat-grid">
        <div class="card stat-card t-blue"><div class="ico">&#128197;</div><div><b>${stats.bookings}</b><span>Total bookings</span></div></div>
        <div class="card stat-card t-green"><div class="ico">&#128176;</div><div><b><sb:money value="${stats.processed}"/></b><span>Payments processed</span></div></div>
        <div class="card stat-card t-teal"><div class="ico">&#127974;</div><div><b><sb:money value="${stats.earnings}"/></b><span>Platform earnings</span></div></div>
        <div class="card stat-card t-red"><div class="ico">&#8617;</div><div><b><sb:money value="${stats.refunded}"/></b><span>Refunded</span></div></div>
      </div>
      <div class="row mt-2" style="gap:.5rem">
        <c:forEach var="e" items="${stats.byStatus}">
          <span class="status status-${fn:toLowerCase(e.key)}">${fn:substring(e.key, 0, 1)}${fn:toLowerCase(fn:substring(e.key, 1, fn:length(e.key)))}: ${e.value}</span>
        </c:forEach>
      </div>

      <div class="card mt-3">
        <div class="card-head"><h3>Booking &amp; payment report</h3><span class="muted small">Latest ${fn:length(bookings)} bookings</span></div>
        <c:choose>
          <c:when test="${empty bookings}">
            <div class="empty"><div class="big">&#128179;</div><p class="muted mb-0">No bookings yet.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>#</th><th>Customer</th><th>Worker</th><th>Skill</th><th>When</th><th>Total</th><th>Status</th><th>Payment</th><th></th></tr></thead>
                <tbody>
                  <c:forEach var="b" items="${bookings}">
                    <tr>
                      <td>#${b.id}</td>
                      <td>${fn:escapeXml(b.customerName)}</td>
                      <td>${fn:escapeXml(b.workerName)}</td>
                      <td class="nowrap">${b.skillIcon} ${fn:escapeXml(b.skillName)}</td>
                      <td class="nowrap">${b.dateLabel}<br><span class="muted small">${b.startLabel} &ndash; ${b.endLabel}</span></td>
                      <td class="nowrap"><sb:money value="${b.total}"/></td>
                      <td><span class="status status-${b.statusClass}">${b.statusLabel}</span></td>
                      <td>
                        <c:choose>
                          <c:when test="${empty b.paymentMethod}"><span class="muted small">No payment</span></c:when>
                          <c:otherwise>
                            <span class="badge ${b.paymentStatus == 'SUCCESS' ? 'badge-success' : (b.paymentStatus == 'REFUNDED' ? 'badge-warning' : 'badge-neutral')}">${fn:escapeXml(b.paymentMethod)} &middot; ${fn:escapeXml(b.paymentStatus)}</span>
                            <div class="small muted nowrap"><span class="kbd">${fn:escapeXml(b.txnRef)}</span></div>
                          </c:otherwise>
                        </c:choose>
                      </td>
                      <td class="actions">
                        <c:if test="${b.status == 'PENDING' || b.status == 'ACCEPTED'}">
                          <form method="post" action="${ctx}/booking/action" data-confirm="Cancel booking #${b.id} and refund the customer?">
                            <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${b.id}"><input type="hidden" name="action" value="cancel"><input type="hidden" name="back" value="/admin/bookings">
                            <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                          </form>
                        </c:if>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
