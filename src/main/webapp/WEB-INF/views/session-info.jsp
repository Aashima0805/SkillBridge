<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Session and cookies"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / Session &amp; cookies</div>
    <h1>Session &amp; cookies demo</h1>
    <p>Experiment 4: see what the server remembers about you in the HTTP session and what your browser sends back as cookies.</p>
  </div>
</section>

<div class="container pull-up">
  <div class="stat-grid" style="grid-template-columns:repeat(auto-fit,minmax(220px,1fr))">
    <div class="card stat-card t-amber"><div class="ico">&#127850;</div><div><b>${visits}</b><span>Visits to this page (counted in cookie <span class="kbd">sb_visits</span>)</span></div></div>
    <div class="card stat-card t-blue"><div class="ico">&#128273;</div><div><b>${fn:length(cookies)}</b><span>Cookies sent by your browser</span></div></div>
    <div class="card stat-card t-teal"><div class="ico">&#128230;</div><div><b>${fn:length(sessionAttrs)}</b><span>Attributes stored in your session</span></div></div>
  </div>

  <div class="grid mt-3" style="grid-template-columns:repeat(auto-fit,minmax(320px,1fr))">
    <div class="card">
      <div class="card-head"><h3>HttpSession details</h3><span class="badge badge-info">Server side</span></div>
      <div class="table-wrap">
        <table class="table">
          <tbody>
            <c:forEach var="e" items="${sessionInfo}">
              <tr><td style="width:40%"><strong>${fn:escapeXml(e.key)}</strong></td><td>${fn:escapeXml(e.value)}</td></tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
      <div class="card-body" style="border-top:1px solid var(--line)">
        <h4>Session attributes</h4>
        <c:choose>
          <c:when test="${empty sessionAttrs}"><p class="muted small mb-0">No attributes yet. Log in to see <span class="kbd">user</span> appear here.</p></c:when>
          <c:otherwise>
            <div class="tag-cloud"><c:forEach var="a" items="${sessionAttrs}"><span class="kbd">${fn:escapeXml(a)}</span></c:forEach></div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>

    <div class="card">
      <div class="card-head"><h3>Cookies from your browser</h3><span class="badge badge-warning">Client side</span></div>
      <c:choose>
        <c:when test="${empty cookies}">
          <div class="empty"><div class="big">&#127850;</div><p class="muted mb-0">No cookies were sent with this request. Refresh the page and they will appear.</p></div>
        </c:when>
        <c:otherwise>
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>Name</th><th>Value</th></tr></thead>
              <tbody>
                <c:forEach var="c" items="${cookies}">
                  <tr><td><span class="kbd">${fn:escapeXml(c[0])}</span></td><td style="word-break:break-all">${fn:escapeXml(c[1])}</td></tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:otherwise>
      </c:choose>
      <div class="card-body" style="border-top:1px solid var(--line)">
        <p class="small muted mb-0">Reload the page to watch <span class="kbd">sb_visits</span> go up. The session cookie <span class="kbd">JSESSIONID</span> is <em>HttpOnly</em>, so JavaScript cannot read it.</p>
      </div>
    </div>
  </div>

  <div class="grid mt-3" style="grid-template-columns:repeat(auto-fit,minmax(320px,1fr))">
    <div class="card"><div class="card-body">
      <span class="eyebrow">Cookie</span>
      <h3>Stored in the browser</h3>
      <ul class="mb-0" style="padding-left:1.2rem">
        <li>Small name/value text kept by the browser and sent with every matching request.</li>
        <li>Can survive restarts using <span class="kbd">Max-Age</span> (e.g. the visit counter lasts one year).</li>
        <li>Visible to and editable by the user, so never trust it for secrets without signing it.</li>
        <li>Used here for the visit counter, recently viewed workers and the signed "remember me" token.</li>
      </ul>
    </div></div>
    <div class="card"><div class="card-body">
      <span class="eyebrow">Session</span>
      <h3>Stored on the server</h3>
      <ul class="mb-0" style="padding-left:1.2rem">
        <li>Data lives in server memory; the browser only holds the session id cookie.</li>
        <li>Expires after 30 minutes of inactivity (configured in <span class="kbd">web.xml</span>).</li>
        <li>Safe place for the logged-in user, CSRF token and one-time flash messages.</li>
        <li>Lost when the session is invalidated, e.g. on <a href="${ctx}/logout">log out</a>.</li>
      </ul>
    </div></div>
  </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
