<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Find workers"/></jsp:include>
<section class="page-hero"><div class="container"><div class="crumbs"><a href="${ctx}/home">Home</a> / Find workers</div><h1>Find a trusted worker</h1><p>Search verified local workers and book the right person in minutes.</p></div></section>
<div class="container pull-up">
  <form id="filterForm" class="card filter-bar" action="${ctx}/workers" method="get">
    <div><label for="fq">Search</label><input id="fq" type="search" name="q" value="${fn:escapeXml(q)}" placeholder="Name, skill or keyword"></div>
    <div><label for="fs">Skill</label>
      <select id="fs" name="skill"><option value="">All skills</option>
        <c:forEach var="s" items="${skills}"><option value="${s.id}" ${s.id == skillId ? 'selected' : ''}>${s.icon} ${fn:escapeXml(s.name)}</option></c:forEach>
      </select></div>
    <div><label for="fc">City</label>
      <select id="fc" name="city"><option value="">All cities</option>
        <c:forEach var="c" items="${cities}"><option value="${fn:escapeXml(c)}" ${c == city ? 'selected' : ''}>${fn:escapeXml(c)}</option></c:forEach>
      </select></div>
    <div><label for="fo">Sort by</label>
      <select id="fo" name="sort">
        <option value="" ${empty sort ? 'selected' : ''}>Top rated</option>
        <option value="price_low" ${sort == 'price_low' ? 'selected' : ''}>Price: low to high</option>
        <option value="price_high" ${sort == 'price_high' ? 'selected' : ''}>Price: high to low</option>
        <option value="experience" ${sort == 'experience' ? 'selected' : ''}>Most experienced</option>
      </select></div>
    <div class="row" style="flex-wrap:nowrap"><button class="btn btn-primary" type="submit">Search</button><a id="clearFilters" class="btn btn-ghost" href="${ctx}/workers">Clear</a></div>
  </form>

  <div class="result-meta"><span><strong id="resultCount">${fn:length(workers)}</strong> verified worker${fn:length(workers) == 1 ? '' : 's'} found</span><span class="small">Results update as you type</span></div>
  <div id="results" aria-live="polite"><jsp:include page="fragments/workerCards.jsp"/></div>

  <c:if test="${not empty recent}">
    <h3 class="mt-4">Recently viewed</h3>
    <div class="worker-grid" style="grid-template-columns:repeat(auto-fit,minmax(280px,1fr))"><c:forEach var="w" items="${recent}"><sb:workerCard w="${w}"/></c:forEach></div>
  </c:if>
</div>
<jsp:include page="fragments/footer.jsp"/>
