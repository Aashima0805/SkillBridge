<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Log in"/></jsp:include>
<div class="auth-shell">
  <aside class="auth-side">
    <span class="eyebrow" style="align-self:flex-start;background:rgba(255,255,255,.12);color:#c7d2fe">Welcome back</span>
    <h2>Good work deserves a good platform.</h2>
    <ul>
      <li><span class="tick">&#10003;</span><span>Book verified electricians, plumbers, carpenters, painters and helpers.</span></li>
      <li><span class="tick">&#10003;</span><span>Transparent hourly rates and a clear price breakdown.</span></li>
      <li><span class="tick">&#10003;</span><span>Workers manage their own jobs, hours and earnings.</span></li>
    </ul>
  </aside>
  <div class="auth-main">
    <div class="auth-card">
      <h1>Log in</h1>
      <p class="muted">New here? <a href="${ctx}/register">Create a free account</a></p>
      <c:if test="${not empty error}"><div class="alert alert-error" role="alert">${fn:escapeXml(error)}</div></c:if>
      <form action="${ctx}/login" method="post" data-validate>
        <input type="hidden" name="_csrf" value="${csrf}">
        <c:if test="${not empty next}"><input type="hidden" name="next" value="${fn:escapeXml(next)}"></c:if>
        <div class="form-group"><label for="login">Username or e-mail</label><input id="login" type="text" name="login" value="${fn:escapeXml(login)}" autocomplete="username" data-rule="required" data-msg-required="Enter your username or e-mail." autofocus></div>
        <div class="form-group"><label for="password">Password</label><input id="password" type="password" name="password" autocomplete="current-password" data-rule="required" data-msg-required="Enter your password."><button type="button" class="toggle-pass" data-toggle-pass="password">Show</button></div>
        <div class="form-group row row-between"><label class="check"><input type="checkbox" name="remember"> Remember me for 7 days</label></div>
        <button class="btn btn-primary btn-block btn-lg" type="submit">Log in</button>
      </form>
      <div class="card card-body mt-3" style="background:#f8fafc;box-shadow:none">
        <strong class="small">Demo accounts</strong>
        <div class="small muted mt-1">Admin: <span class="kbd">admin</span> / <span class="kbd">Admin@123</span><br>Customer: <span class="kbd">customer1</span> / <span class="kbd">Customer@123</span><br>Worker: <span class="kbd">ramesh_k</span> / <span class="kbd">Worker@123</span></div>
      </div>
    </div>
  </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
