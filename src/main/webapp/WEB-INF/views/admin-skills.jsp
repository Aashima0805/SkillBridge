<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Manage skills"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/admin">Admin</a> / Skills</div>
    <h1>Manage skills</h1>
    <p>Create, edit and remove the trades customers can search for. Skills in use by workers cannot be deleted.</p>
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
        <div class="card-head"><h3>Add a skill</h3></div>
        <div class="card-body">
          <form method="post" action="${ctx}/admin/skills" data-validate>
            <input type="hidden" name="_csrf" value="${csrf}">
            <input type="hidden" name="action" value="add">
            <div class="form-row" style="grid-template-columns:repeat(auto-fit,minmax(220px,1fr))">
              <div class="form-group">
                <label for="addName">Name <span class="req">*</span></label>
                <input type="text" id="addName" name="name" maxlength="40" placeholder="e.g. Gardener" data-rule="length" data-min="3" data-max="40" data-msg="Name must be 3 to 40 characters.">
              </div>
              <div class="form-group">
                <label for="addIcon">Icon (emoji)</label>
                <input type="text" id="addIcon" name="icon" maxlength="16" placeholder="&#127807;" data-rule="length" data-min="1" data-max="16" data-optional="true">
              </div>
            </div>
            <div class="form-group">
              <label for="addDesc">Description</label>
              <input type="text" id="addDesc" name="description" maxlength="200" placeholder="Short description shown to customers" data-rule="length" data-min="0" data-max="200" data-optional="true">
            </div>
            <button type="submit" class="btn btn-primary">Add skill</button>
          </form>
        </div>
      </div>

      <div class="card mt-3">
        <div class="card-head"><h3>All skills</h3><span class="muted small">${fn:length(skills)} total</span></div>
        <c:choose>
          <c:when test="${empty skills}">
            <div class="empty"><div class="big">&#128736;</div><p class="muted mb-0">No skills yet. Add the first one above.</p></div>
          </c:when>
          <c:otherwise>
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th colspan="4">Skill (icon, name, description &mdash; edit inline and save)</th><th></th></tr></thead>
                <tbody>
                  <c:forEach var="s" items="${skills}">
                    <tr>
                      <td colspan="4">
                        <form method="post" action="${ctx}/admin/skills" id="edit-${s.id}" data-validate class="row" style="gap:.6rem;flex-wrap:nowrap">
                          <input type="hidden" name="_csrf" value="${csrf}">
                          <input type="hidden" name="action" value="update">
                          <input type="hidden" name="id" value="${s.id}">
                          <input type="text" name="icon" value="${fn:escapeXml(s.icon)}" maxlength="16" aria-label="Icon for ${fn:escapeXml(s.name)}" style="width:72px;text-align:center" data-rule="length" data-min="1" data-max="16">
                          <input type="text" name="name" value="${fn:escapeXml(s.name)}" maxlength="40" aria-label="Skill name" style="min-width:150px" data-rule="length" data-min="3" data-max="40" data-msg="Name must be 3 to 40 characters.">
                          <input type="text" name="description" value="${fn:escapeXml(s.description)}" maxlength="200" aria-label="Description" style="min-width:220px" data-rule="length" data-min="0" data-max="200" data-optional="true">
                          <span class="badge badge-neutral nowrap">${s.workerCount} worker${s.workerCount == 1 ? '' : 's'}</span>
                          <button type="submit" class="btn btn-outline btn-sm">Save</button>
                        </form>
                      </td>
                      <td class="actions">
                        <form method="post" action="${ctx}/admin/skills" data-confirm="Delete the skill &quot;${fn:escapeXml(s.name)}&quot;? This cannot be undone.">
                          <input type="hidden" name="_csrf" value="${csrf}">
                          <input type="hidden" name="action" value="delete">
                          <input type="hidden" name="id" value="${s.id}">
                          <button type="submit" class="btn btn-danger btn-sm" ${s.workerCount > 0 ? 'disabled title="In use by workers"' : ''}>Delete</button>
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
