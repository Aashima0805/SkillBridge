<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Admin overview"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / Admin</div>
    <h1>Admin overview</h1>
    <p>Platform health at a glance: users, bookings, payments and workers waiting for verification.</p>
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
        <div class="card stat-card"><div class="ico">&#128101;</div><div><b>${stats.users}</b><span>Total users (${stats.customers} customers)</span></div></div>
        <div class="card stat-card t-teal"><div class="ico">&#128119;</div><div><b>${stats.workers}</b><span>Workers</span></div></div>
        <div class="card stat-card t-amber"><div class="ico">&#9203;</div><div><b>${stats.pending}</b><span>Awaiting verification</span></div></div>
        <div class="card stat-card t-blue"><div class="ico">&#128197;</div><div><b>${stats.bookings}</b><span>Bookings</span></div></div>
      </div>
      <div class="stat-grid mt-2" style="grid-template-columns:repeat(auto-fit,minmax(200px,1fr))">
        <div class="card stat-card t-green"><div class="ico">&#128176;</div><div><b><sb:money value="${stats.processed}"/></b><span>Payments processed</span></div></div>
        <div class="card stat-card t-teal"><div class="ico">&#127974;</div><div><b><sb:money value="${stats.earnings}"/></b><span>Platform earnings (fees)</span></div></div>
        <div class="card stat-card t-red"><div class="ico">&#8617;</div><div><b><sb:money value="${stats.refunded}"/></b><span>Refunded</span></div></div>
      </div>

      <div class="card mt-3">
        <div class="card-head">
          <h3>Workers awaiting verification</h3>
          <a class="btn btn-outline btn-sm" href="${ctx}/admin/workers?status=pending">View all</a>
        </div>
        <c:choose>
          <c:when test="${empty pendingWorkers}">
            <div class="empty"><div class="big">&#127881;</div><p class="muted mb-0">Everything is verified. Nothing waiting for review.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>Worker</th><th>Skill</th><th>City</th><th>Experience</th><th>Rate</th><th></th></tr></thead>
                <tbody>
                  <c:forEach var="w" items="${pendingWorkers}">
                    <tr>
                      <td><div class="who"><sb:avatar initials="${w.initials}" seed="${w.id}" size="avatar-sm"/><div><strong>${fn:escapeXml(w.fullName)}</strong><span>${fn:escapeXml(w.phone)}</span></div></div></td>
                      <td>${w.skillIcon} ${fn:escapeXml(w.skillName)}</td>
                      <td>${fn:escapeXml(w.city)}</td>
                      <td>${w.experienceYears} yrs</td>
                      <td><sb:money value="${w.hourlyRate}" decimals="0"/>/hr</td>
                      <td class="actions">
                        <form method="post" action="${ctx}/admin/workers">
                          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="workerId" value="${w.id}"><input type="hidden" name="action" value="verify"><input type="hidden" name="back" value="/admin">
                          <button type="submit" class="btn btn-success btn-sm">Verify</button>
                        </form>
                      </td>
                    </tr>
                  </c:forEach>
                </tbody>
              </table>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <div class="grid mt-3" style="grid-template-columns:repeat(auto-fit,minmax(300px,1fr))">
        <div class="card">
          <div class="card-head"><h3>Bookings by skill</h3></div>
          <div class="card-body">
            <c:forEach var="row" items="${stats.bySkill}">
              <div class="bar-row">
                <span>${row.icon} ${fn:escapeXml(row.name)}</span>
                <div class="bar" role="img" aria-label="${fn:escapeXml(row.name)}: ${row.count} bookings"><i style="width:${row.percent}%"></i></div>
                <strong>${row.count}</strong>
              </div>
            </c:forEach>
            <c:if test="${empty stats.bySkill}"><p class="muted mb-0">No skills yet.</p></c:if>
          </div>
        </div>
        <div class="card">
          <div class="card-head"><h3>Bookings by status</h3></div>
          <div class="card-body">
            <ul class="mini-list">
              <c:forEach var="e" items="${stats.byStatus}">
                <li><span class="status status-${fn:toLowerCase(e.key)}">${fn:substring(e.key, 0, 1)}${fn:toLowerCase(fn:substring(e.key, 1, fn:length(e.key)))}</span><span style="margin-left:auto"><strong>${e.value}</strong></span></li>
              </c:forEach>
            </ul>
          </div>
        </div>
      </div>

      <div class="card mt-3">
        <div class="card-head">
          <h3>Recent bookings</h3>
          <a class="btn btn-outline btn-sm" href="${ctx}/admin/bookings">Full report</a>
        </div>
        <c:choose>
          <c:when test="${empty recentBookings}">
            <div class="empty"><p class="muted mb-0">No bookings yet.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>#</th><th>Customer</th><th>Worker</th><th>When</th><th>Total</th><th>Status</th></tr></thead>
                <tbody>
                  <c:forEach var="b" items="${recentBookings}">
                    <tr>
                      <td>#${b.id}</td>
                      <td>${fn:escapeXml(b.customerName)}</td>
                      <td><strong>${fn:escapeXml(b.workerName)}</strong><br><span class="muted small">${b.skillIcon} ${fn:escapeXml(b.skillName)}</span></td>
                      <td class="nowrap">${b.dateLabel}<br><span class="muted small">${b.startLabel} &ndash; ${b.endLabel}</span></td>
                      <td class="nowrap"><sb:money value="${b.total}"/></td>
                      <td><span class="status status-${b.statusClass}">${b.statusLabel}</span></td>
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
