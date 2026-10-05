<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="About"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / About</div>
    <h1>About SkillBridge</h1>
    <p>A marketplace that connects households with verified local electricians, plumbers, carpenters, painters and helpers.</p>
  </div>
</section>

<div class="container pull-up">
  <div class="grid grid-2">
    <div class="card"><div class="card-body">
      <span class="eyebrow">The project</span>
      <h3>What is SkillBridge?</h3>
      <p>SkillBridge is a Java web application built with Servlets, JSP and JDBC on Apache Tomcat 10.1 with a MySQL database. Customers search for verified workers, check live slot availability, book a time, pay through a simulated gateway and review the job afterwards. Workers manage requests from their own dashboard and admins verify profiles, manage skills and monitor payments.</p>
      <p class="mb-0">It was written as a Web Technology lab project: every one of the ten experiments is used in a real feature rather than in an isolated demo page.</p>
    </div></div>
    <div class="card"><div class="card-body">
      <span class="eyebrow">The problem</span>
      <h3>Why does it exist?</h3>
      <ul class="mb-0" style="padding-left:1.2rem">
        <li>Finding a trustworthy tradesperson usually depends on word of mouth.</li>
        <li>Skilled local workers have no easy way to show their experience, rates and reviews.</li>
        <li>Phone-tag over timings leads to double bookings and no-shows.</li>
        <li>Customers worry about paying upfront with no refund path.</li>
      </ul>
      <p class="mt-2 mb-0">SkillBridge fixes this with admin-verified profiles, instant slot checking, transparent pricing and automatic refunds on cancellation.</p>
    </div></div>
  </div>

  <div class="card mt-3">
    <div class="card-head"><h3>How SkillBridge maps to the 10 lab experiments</h3></div>
    <div class="table-wrap">
      <table class="table">
        <thead><tr><th>#</th><th>Experiment</th><th>Where to see it in SkillBridge</th></tr></thead>
        <tbody>
          <tr><td>1</td><td><strong>Client-side scripting (JavaScript)</strong></td><td>Live price estimate on the booking form, password strength meter and form validation in <code>app.js</code> &mdash; try <a href="${ctx}/register">/register</a> or any <a href="${ctx}/workers">/workers</a> profile.</td></tr>
          <tr><td>2</td><td><strong>Simple web application using Servlets</strong></td><td>Controllers such as <a href="${ctx}/workers">/workers</a>, <code>/login</code>, <code>/register</code>, <code>/book</code> and <code>/booking/action</code> handle requests, run business logic and forward to views.</td></tr>
          <tr><td>3</td><td><strong>Simple web application using JSP</strong></td><td>All pages are JSP views using EL, JSTL and custom tag files (<code>sb:money</code>, <code>sb:stars</code>, <code>sb:avatar</code>, <code>sb:workerCard</code>) with shared header and footer includes, e.g. <a href="${ctx}/home">/home</a>.</td></tr>
          <tr><td>4</td><td><strong>Cookies &amp; session tracking</strong></td><td><a href="${ctx}/session-info">/session-info</a> shows your session and cookies; "remember me" on <a href="${ctx}/login">/login</a> and "recently viewed" workers also use cookies.</td></tr>
          <tr><td>5</td><td><strong>Database connectivity using Servlet and JSP</strong></td><td>DAO classes use JDBC with prepared statements and a HikariCP pool; servlets load data that JSP pages display, e.g. <a href="${ctx}/workers">/workers</a> and <a href="${ctx}/profile?id=1">worker profiles</a>.</td></tr>
          <tr><td>6</td><td><strong>AJAX</strong></td><td>Live username/email availability on <a href="${ctx}/register">/register</a> (<code>/ajax/check</code>), live search on <a href="${ctx}/workers">/workers</a> and slot checking on the booking form (<code>/ajax/slot</code>).</td></tr>
          <tr><td>7</td><td><strong>XML</strong></td><td><a href="${ctx}/workers.xml">/workers.xml</a> returns an XML document that <a href="${ctx}/xml-workers">/xml-workers</a> parses and renders as a sortable HTML table.</td></tr>
          <tr><td>8</td><td><strong>Application that makes use of a database</strong></td><td>Full CRUD on users, workers, skills, bookings, payments and reviews: registration, admin skill management, worker verification and reports at <code>/admin</code>.</td></tr>
          <tr><td>9</td><td><strong>Transactional application (e-commerce style)</strong></td><td>Service marketplace flow: pick a worker and slot, see the price breakdown, pay (simulated UPI/card) and view booking history, with refunds on cancellation, from <a href="${ctx}/workers">/workers</a> to <code>/checkout</code> to <code>/my-bookings</code>.</td></tr>
          <tr><td>10</td><td><strong>Testing the web application</strong></td><td>JUnit tests for validation, pricing, slot rules and tokens (<code>src/test</code>), plus the black-box and boundary-value test cases in <code>docs/TEST_CASES.md</code>.</td></tr>
        </tbody>
      </table>
    </div>
  </div>

  <div class="grid grid-2 mt-3">
    <div class="card">
      <div class="card-head"><h3>Tech stack</h3></div>
      <div class="card-body">
        <div class="tag-cloud">
          <span class="badge badge-info">Java 17</span>
          <span class="badge badge-info">Servlet 6 (Jakarta EE 10)</span>
          <span class="badge badge-info">JSP &amp; JSTL 3</span>
          <span class="badge badge-info">Apache Tomcat 10.1</span>
          <span class="badge badge-teal">MySQL + JDBC</span>
          <span class="badge badge-warning">HTML5 / CSS3</span>
          <span class="badge badge-warning">Vanilla JavaScript</span>
          <span class="badge badge-neutral">XMLHttpRequest</span>
          <span class="badge badge-neutral">XML</span>
          <span class="badge badge-neutral">Maven</span>
        </div>
        <p class="small muted mt-2 mb-0">Architecture: MVC &mdash; servlets (controllers), JSP views, DAO + model classes. Payments are simulated; no real money moves.</p>
      </div>
    </div>
    <div class="card">
      <div class="card-head"><h3>Demo logins</h3></div>
      <div class="card-body">
        <div class="table-wrap">
          <table class="table">
            <thead><tr><th>Role</th><th>Username</th><th>Password</th></tr></thead>
            <tbody>
              <tr><td><span class="badge badge-danger">Admin</span></td><td><span class="kbd">admin</span></td><td><span class="kbd">Admin@123</span></td></tr>
              <tr><td><span class="badge badge-info">Customer</span></td><td><span class="kbd">customer1</span></td><td><span class="kbd">Customer@123</span></td></tr>
              <tr><td><span class="badge badge-teal">Worker</span></td><td><span class="kbd">ramesh_k</span></td><td><span class="kbd">Worker@123</span></td></tr>
            </tbody>
          </table>
        </div>
        <a class="btn btn-primary btn-sm mt-2" href="${ctx}/login">Go to login</a>
      </div>
    </div>
  </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
