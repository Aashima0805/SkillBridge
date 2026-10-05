<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Find trusted local workers"/></jsp:include>

<section class="hero">
  <div class="container">
    <div>
      <span class="pill">&#10024; Verified electricians, plumbers, carpenters &amp; more</span>
      <h1>Skilled hands, <em>fair work</em>, one trusted bridge.</h1>
      <p class="lead">Book verified local workers by skill and city, pay online, and rate the job. Workers get steady, direct work at transparent rates &mdash; no middlemen.</p>
      <form class="hero-search" action="${ctx}/workers" method="get">
        <select name="skill" aria-label="Skill">
          <option value="">Any skill</option>
          <c:forEach var="s" items="${skills}"><option value="${s.id}">${s.icon} ${fn:escapeXml(s.name)}</option></c:forEach>
        </select>
        <input type="text" name="q" placeholder="City or keyword" aria-label="City or keyword">
        <button class="btn btn-accent" type="submit">Search</button>
      </form>
      <div class="hero-cta">
        <c:if test="${empty sessionScope.user}"><a class="btn btn-light" href="${ctx}/register?role=WORKER">Join as a worker</a></c:if>
        <a class="btn btn-ghost" style="color:#e0e7ff" href="${ctx}/about">How it works &rarr;</a>
      </div>
    </div>
    <div class="hero-visual" aria-hidden="true">
      <div class="float-card f1"><span class="ico">&#9889;</span><div><strong>Electrician booked</strong><span>Tomorrow, 10:00 AM</span></div></div>
      <div class="float-card f2"><span class="ico">&#11088;</span><div><strong>4.8 average rating</strong><span>From real customers</span></div></div>
      <div class="float-card f3"><span class="ico">&#9989;</span><div><strong>Payment received</strong><span>Worker paid directly</span></div></div>
    </div>
  </div>
</section>

<section class="stats-strip">
  <div class="container">
    <div class="card">
      <div class="stat"><div class="num" data-countup="${stats.workers}">0</div><div class="lbl">Verified workers</div></div>
      <div class="stat"><div class="num" data-countup="${stats.jobs}">0</div><div class="lbl">Jobs completed</div></div>
      <div class="stat"><div class="num" data-countup="${stats.cities}">0</div><div class="lbl">Cities covered</div></div>
      <div class="stat"><div class="num" data-countup="${stats.rating}">0</div><div class="lbl">Average rating</div></div>
    </div>
  </div>
</section>

<section class="section">
  <div class="container">
    <div class="section-head reveal"><span class="eyebrow">Browse by skill</span><h2>Whatever needs fixing, we have a pro</h2><p>Pick a trade to see verified workers near you, with clear hourly rates.</p></div>
    <div class="grid grid-4" style="grid-template-columns:repeat(auto-fit,minmax(190px,1fr))">
      <c:forEach var="s" items="${skills}">
        <a class="card card-hover skill-tile reveal" href="${ctx}/workers?skill=${s.id}">
          <div class="ico">${s.icon}</div><h3>${fn:escapeXml(s.name)}</h3><p>${s.workerCount} worker${s.workerCount == 1 ? '' : 's'}</p>
        </a>
      </c:forEach>
    </div>
  </div>
</section>

<c:if test="${not empty recent}">
<section class="section-sm">
  <div class="container">
    <h3>Recently viewed by you</h3>
    <div class="worker-grid" style="grid-template-columns:repeat(auto-fit,minmax(280px,1fr))"><c:forEach var="w" items="${recent}"><sb:workerCard w="${w}"/></c:forEach></div>
  </div>
</section>
</c:if>

<section class="section section-alt">
  <div class="container">
    <div class="section-head reveal"><span class="eyebrow">Top rated</span><h2>Highly rated workers this month</h2><p>Verified by our admin team and loved by customers.</p></div>
    <div class="worker-grid">
      <c:forEach var="w" items="${topWorkers}"><div class="reveal"><sb:workerCard w="${w}"/></div></c:forEach>
    </div>
    <div class="text-center mt-4"><a class="btn btn-outline btn-lg" href="${ctx}/workers">See all workers</a></div>
  </div>
</section>

<section class="section">
  <div class="container">
    <div class="section-head reveal"><span class="eyebrow">How it works</span><h2>Booked in four simple steps</h2></div>
    <div class="steps">
      <div class="card step reveal"><h3>Search</h3><p>Filter by skill, city and rating to find the right person for the job.</p></div>
      <div class="card step reveal"><h3>Pick a slot</h3><p>Choose a date and time. We check the worker is free before you pay.</p></div>
      <div class="card step reveal"><h3>Pay securely</h3><p>See the full price breakdown and pay with UPI or card (simulated).</p></div>
      <div class="card step reveal"><h3>Rate the work</h3><p>After the job is done, leave a review to help others and reward good work.</p></div>
    </div>
  </div>
</section>

<c:if test="${not empty reviews}">
<section class="section section-alt">
  <div class="container">
    <div class="section-head reveal"><span class="eyebrow">Happy customers</span><h2>What people are saying</h2></div>
    <div class="grid grid-3">
      <c:forEach var="r" items="${reviews}" begin="0" end="2">
        <div class="card testimonial reveal">
          <sb:stars rating="${r.rating + 0.0}"/>
          <p class="quote">&ldquo;${fn:escapeXml(r.comment)}&rdquo;</p>
          <div class="who"><sb:avatar initials="${r.initials}" seed="${r.customerId}" size="avatar-sm"/><div><strong>${fn:escapeXml(r.customerName)}</strong><div class="small muted">${fn:escapeXml(r.skillName)} &middot; ${fn:escapeXml(r.workerName)}</div></div></div>
        </div>
      </c:forEach>
    </div>
  </div>
</section>
</c:if>

<section class="section">
  <div class="container">
    <div class="cta-band reveal">
      <h2>Are you a skilled worker?</h2>
      <p style="max-width:560px;margin:0 auto 1.6rem">Create your profile, set your own rate and working hours, and get booked directly by customers near you.</p>
      <a class="btn btn-accent btn-lg" href="${ctx}/register?role=WORKER">Create worker profile</a>
    </div>
  </div>
</section>

<jsp:include page="fragments/footer.jsp"/>
