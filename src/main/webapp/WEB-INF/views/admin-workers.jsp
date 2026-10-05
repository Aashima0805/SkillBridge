<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Manage workers"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/admin">Admin</a> / Workers</div>
    <h1>Manage workers</h1>
    <p>Only verified workers appear in customer search. Review new profiles and verify or hide them here.</p>
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
      <c:set var="backFilter" value="${filter == 'pending' ? 'pending' : (filter == 'verified' ? 'verified' : '')}"/>
      <div class="tabs">
        <a href="${ctx}/admin/workers" class="${empty backFilter ? 'active' : ''}">All</a>
        <a href="${ctx}/admin/workers?status=pending" class="${backFilter == 'pending' ? 'active' : ''}">Pending</a>
        <a href="${ctx}/admin/workers?status=verified" class="${backFilter == 'verified' ? 'active' : ''}">Verified</a>
      </div>

      <div class="card">
        <div class="card-head"><h3>${fn:length(workers)} worker${fn:length(workers) == 1 ? '' : 's'}</h3></div>
        <c:choose>
          <c:when test="${empty workers}">
            <div class="empty"><div class="big">&#128119;</div><h3>No workers here</h3><p class="muted mb-0">Nothing matches this filter.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>Worker</th><th>Skill</th><th>City</th><th>Exp.</th><th>Rate</th><th>Rating</th><th>Joined</th><th>Status</th><th></th></tr></thead>
                <tbody>
                  <c:forEach var="w" items="${workers}">
                    <tr>
                      <td><div class="who"><sb:avatar initials="${w.initials}" seed="${w.id}" size="avatar-sm"/><div><strong>${fn:escapeXml(w.fullName)}</strong><span>${fn:escapeXml(w.phone)} &middot; ${fn:escapeXml(w.email)}</span></div></div></td>
                      <td class="nowrap">${w.skillIcon} ${fn:escapeXml(w.skillName)}</td>
                      <td>${fn:escapeXml(w.city)}</td>
                      <td class="nowrap">${w.experienceYears} yrs</td>
                      <td class="nowrap"><sb:money value="${w.hourlyRate}" decimals="0"/>/hr</td>
                      <td class="nowrap"><sb:stars rating="${w.avgRating}" count="${w.reviewCount}"/></td>
                      <td class="nowrap">${w.joinedLabel}</td>
                      <td>
                        <c:choose>
                          <c:when test="${w.verified}"><span class="badge badge-success">Verified</span></c:when>
                          <c:otherwise><span class="badge badge-warning">Pending</span></c:otherwise>
                        </c:choose>
                        <c:if test="${!w.userActive}"><span class="badge badge-danger">Deactivated</span></c:if>
                      </td>
                      <td class="actions">
                        <form method="post" action="${ctx}/admin/workers">
                          <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="workerId" value="${w.id}">
                          <input type="hidden" name="back" value="/admin/workers?status=${backFilter}">
                          <c:choose>
                            <c:when test="${w.verified}">
                              <input type="hidden" name="action" value="unverify">
                              <button type="submit" class="btn btn-outline btn-sm">Unverify</button>
                            </c:when>
                            <c:otherwise>
                              <input type="hidden" name="action" value="verify">
                              <button type="submit" class="btn btn-success btn-sm">Verify</button>
                            </c:otherwise>
                          </c:choose>
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
    </div>
  </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
