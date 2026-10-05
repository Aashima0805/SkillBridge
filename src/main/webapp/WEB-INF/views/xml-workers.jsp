<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="XML to HTML table"/></jsp:include>
<section class="page-hero"><div class="container"><div class="crumbs"><a href="${ctx}/about">About</a> / Experiment 7</div><h1>XML document &rarr; HTML table</h1><p>The server publishes workers as an XML document (<span class="kbd" style="color:#0f172a">/workers.xml</span>). JavaScript downloads it with XMLHttpRequest, parses <em>responseXML</em> and builds this table in the browser.</p></div></section>
<div class="container pull-up">
  <div class="card">
    <div class="filter-bar" style="grid-template-columns:1fr 1fr 1.4fr auto">
      <div><label for="xmlSkill">Skill</label><select id="xmlSkill"><option value="">All skills</option><c:forEach var="s" items="${skills}"><option value="${s.id}">${s.icon} ${fn:escapeXml(s.name)}</option></c:forEach></select></div>
      <div><label for="xmlCity">City</label><select id="xmlCity"><option value="">All cities</option><c:forEach var="c" items="${cities}"><option value="${fn:escapeXml(c)}">${fn:escapeXml(c)}</option></c:forEach></select></div>
      <div><label for="xmlFilter">Filter table</label><input id="xmlFilter" type="search" placeholder="Type to filter rows"></div>
      <div><button id="xmlToggle" type="button" class="btn btn-outline">Show raw XML</button></div>
    </div>
    <div class="card-body" style="padding-top:0"><p id="xmlInfo" class="small muted mb-0">Loading&hellip;</p></div>
    <div class="table-wrap">
      <table class="table" id="xmlTable">
        <thead><tr><th data-sort="id">ID</th><th data-sort="name">Name</th><th data-sort="skill">Skill</th><th data-sort="city">City</th><th data-sort="experience">Experience</th><th data-sort="rate">Rate / hr</th><th data-sort="rating">Rating</th><th data-sort="hours">Hours</th><th data-sort="available">Status</th></tr></thead>
        <tbody></tbody>
      </table>
    </div>
  </div>
  <div class="card mt-3 hidden"><div class="card-head"><h3>Raw XML from the server</h3></div><pre id="xmlRaw" class="code xml-box" style="border-radius:0 0 16px 16px;margin:0"></pre></div>
</div>
<jsp:include page="fragments/footer.jsp"/>
