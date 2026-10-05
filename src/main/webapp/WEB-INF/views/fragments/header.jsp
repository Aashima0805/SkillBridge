<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${empty param.title ? '' : fn:escapeXml(param.title).concat(' | ')}SkillBridge</title>
  <meta name="description" content="SkillBridge connects customers with verified local electricians, plumbers, carpenters, painters and helpers.">
  <link rel="icon" href="${ctx}/assets/img/favicon.svg" type="image/svg+xml">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&family=Poppins:wght@500;600;700&display=swap">
  <link rel="stylesheet" href="${ctx}/assets/css/style.css">
</head>
<body data-ctx="${ctx}" data-csrf="${csrf}">
<header class="site-header">
  <div class="container navbar">
    <a class="brand" href="${ctx}/home" aria-label="SkillBridge home">
      <span class="brand-mark"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M2 17h20"/><path d="M4 17c0-5 3.6-9 8-9s8 4 8 9"/><path d="M8 17v-4M12 17V8M16 17v-4"/></svg></span>
      <span class="brand-text">Skill<b>Bridge</b></span>
    </a>
    <button class="nav-toggle" id="navToggle" aria-label="Open menu" aria-expanded="false"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M4 7h16M4 12h16M4 17h16"/></svg></button>
    <nav class="nav-panel" id="navPanel">
      <div class="nav-panel-inner">
        <ul class="nav-links">
          <c:set var="role" value="${sessionScope.user.role}"/>
          <c:if test="${empty role || role == 'CUSTOMER'}">
            <li><a href="${ctx}/home" class="${navPath == '/home' ? 'active' : ''}">Home</a></li>
            <li><a href="${ctx}/workers" class="${navPath == '/workers' ? 'active' : ''}">Find workers</a></li>
          </c:if>
          <c:if test="${role == 'CUSTOMER'}"><li><a href="${ctx}/my-bookings" class="${navPath == '/my-bookings' ? 'active' : ''}">My bookings</a></li></c:if>
          <c:if test="${role == 'WORKER'}">
            <li><a href="${ctx}/worker/dashboard" class="${navPath == '/worker/dashboard' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="${ctx}/worker/profile" class="${navPath == '/worker/profile' ? 'active' : ''}">My profile</a></li>
          </c:if>
          <c:if test="${role == 'ADMIN'}">
            <li><a href="${ctx}/admin" class="${navPath == '/admin' ? 'active' : ''}">Dashboard</a></li>
            <li><a href="${ctx}/admin/workers" class="${fn:startsWith(navPath, '/admin/') ? 'active' : ''}">Manage</a></li>
          </c:if>
          <li><a href="${ctx}/about" class="${navPath == '/about' ? 'active' : ''}">About</a></li>
        </ul>
        <div class="nav-actions">
          <c:choose>
            <c:when test="${not empty sessionScope.user}">
              <span class="user-chip"><sb:avatar initials="${sessionScope.user.initials}" seed="${sessionScope.user.id}" size="avatar-sm"/><span class="name">${fn:escapeXml(fn:substringBefore(sessionScope.user.fullName, ' '))}<small>${sessionScope.user.role}</small></span></span>
              <a class="btn btn-outline btn-sm" href="${ctx}/logout">Log out</a>
            </c:when>
            <c:otherwise>
              <a class="btn btn-ghost btn-sm" href="${ctx}/login">Log in</a>
              <a class="btn btn-primary btn-sm" href="${ctx}/register">Sign up free</a>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </nav>
  </div>
</header>
<c:if test="${not empty sessionScope.flash}">
  <div class="toast-zone" id="toastZone" role="status" aria-live="polite">
    <div class="toast ${sessionScope.flash[0]}"><span class="ti"><c:choose><c:when test="${sessionScope.flash[0] == 'success'}">&#10003;</c:when><c:when test="${sessionScope.flash[0] == 'error'}">!</c:when><c:otherwise>i</c:otherwise></c:choose></span><span class="tx">${fn:escapeXml(sessionScope.flash[1])}</span><button type="button" aria-label="Dismiss">&times;</button></div>
  </div>
  <c:remove var="flash" scope="session"/>
</c:if>
<main>
