<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Manage users"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/admin">Admin</a> / Users</div>
    <h1>Manage users</h1>
    <p>Activate or deactivate customer and worker accounts. Deactivated users cannot log in.</p>
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
      <div class="card">
        <div class="card-head"><h3>All accounts</h3><span class="muted small">${fn:length(users)} total</span></div>
        <c:choose>
          <c:when test="${empty users}">
            <div class="empty"><p class="muted mb-0">No users found.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>User</th><th>Username</th><th>Phone</th><th>City</th><th>Role</th><th>Joined</th><th>Status</th><th></th></tr></thead>
                <tbody>
                  <c:forEach var="u" items="${users}">
                    <tr>
                      <td><div class="who"><sb:avatar initials="${u.initials}" seed="${u.id}" size="avatar-sm"/><div><strong>${fn:escapeXml(u.fullName)}</strong><span>${fn:escapeXml(u.email)}</span></div></div></td>
                      <td>${fn:escapeXml(u.username)}</td>
                      <td class="nowrap">${fn:escapeXml(u.phone)}</td>
                      <td>${fn:escapeXml(u.city)}</td>
                      <td>
                        <c:choose>
                          <c:when test="${u.role == 'ADMIN'}"><span class="badge badge-danger">Admin</span></c:when>
                          <c:when test="${u.role == 'WORKER'}"><span class="badge badge-teal">Worker</span></c:when>
                          <c:otherwise><span class="badge badge-info">Customer</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="nowrap">${u.createdLabel}</td>
                      <td>
                        <c:choose>
                          <c:when test="${u.active}"><span class="badge badge-success">Active</span></c:when>
                          <c:otherwise><span class="badge badge-neutral">Deactivated</span></c:otherwise>
                        </c:choose>
                      </td>
                      <td class="actions">
                        <c:if test="${u.role != 'ADMIN'}">
                          <c:choose>
                            <c:when test="${u.active}">
                              <form method="post" action="${ctx}/admin/users" data-confirm="Deactivate ${fn:escapeXml(u.fullName)}? They will no longer be able to log in.">
                                <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${u.id}"><input type="hidden" name="active" value="false">
                                <button type="submit" class="btn btn-danger btn-sm">Deactivate</button>
                              </form>
                            </c:when>
                            <c:otherwise>
                              <form method="post" action="${ctx}/admin/users">
                                <input type="hidden" name="_csrf" value="${csrf}"><input type="hidden" name="id" value="${u.id}"><input type="hidden" name="active" value="true">
                                <button type="submit" class="btn btn-success btn-sm">Activate</button>
                              </form>
                            </c:otherwise>
                          </c:choose>
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
