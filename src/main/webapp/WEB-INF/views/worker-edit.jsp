<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Edit profile"/></jsp:include>

<section class="page-hero">
  <div class="container">
    <div class="crumbs"><a href="${ctx}/home">Home</a> / <a href="${ctx}/worker/dashboard">Dashboard</a> / Profile</div>
    <h1>Edit your worker profile</h1>
    <p>Keep your skill, rate and working hours up to date so customers can book you with confidence.</p>
  </div>
</section>

<div class="container-narrow pull-up">
  <div class="card">
    <div class="card-head">
      <div class="row" style="gap:.9rem">
        <sb:avatar initials="${worker.initials}" seed="${worker.id}"/>
        <div><h3>${fn:escapeXml(worker.fullName)}</h3><span class="muted small">${fn:escapeXml(worker.phone)} &middot; ${fn:escapeXml(worker.email)}</span></div>
      </div>
      <c:choose>
        <c:when test="${worker.verified}"><span class="badge badge-teal">Verified</span></c:when>
        <c:otherwise><span class="badge badge-warning">Awaiting verification</span></c:otherwise>
      </c:choose>
    </div>
    <div class="card-body">
      <c:if test="${not empty errors}">
        <div class="alert alert-error"><span>!</span><span>Please fix the highlighted fields and try again.</span></div>
      </c:if>

      <form method="post" action="${ctx}/worker/profile" data-validate novalidate>
        <input type="hidden" name="_csrf" value="${csrf}">

        <div class="form-row">
          <div class="form-group">
            <label for="skillId">Skill <span class="req">*</span></label>
            <select id="skillId" name="skillId" data-rule="required" class="${not empty errors.skillId ? 'is-invalid' : ''}">
              <option value="">Choose your skill</option>
              <c:forEach var="s" items="${skills}">
                <option value="${s.id}" ${form.skillId == s.id ? 'selected' : ''}>${s.icon} ${fn:escapeXml(s.name)}</option>
              </c:forEach>
            </select>
            <c:if test="${not empty errors.skillId}"><div class="field-error" data-for="skillId">${fn:escapeXml(errors.skillId)}</div></c:if>
          </div>
          <div class="form-group">
            <label for="city">City <span class="req">*</span></label>
            <input type="text" id="city" name="city" maxlength="40" value="${fn:escapeXml(form.city)}" data-rule="city" class="${not empty errors.city ? 'is-invalid' : ''}" autocomplete="address-level2">
            <c:if test="${not empty errors.city}"><div class="field-error" data-for="city">${fn:escapeXml(errors.city)}</div></c:if>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="experience">Experience (years) <span class="req">*</span></label>
            <input type="number" id="experience" name="experience" min="0" max="50" step="1" value="${fn:escapeXml(form.experience)}" data-rule="range" data-min="0" data-max="50" data-int="true" data-msg="Enter whole years from 0 to 50." class="${not empty errors.experience ? 'is-invalid' : ''}">
            <c:if test="${not empty errors.experience}"><div class="field-error" data-for="experience">${fn:escapeXml(errors.experience)}</div></c:if>
          </div>
          <div class="form-group">
            <label for="rate">Hourly rate (&#8377;) <span class="req">*</span></label>
            <input type="number" id="rate" name="rate" min="50" max="2000" step="1" value="${fn:escapeXml(form.rate)}" data-rule="range" data-min="50" data-max="2000" data-msg="Rate must be between 50 and 2000." class="${not empty errors.rate ? 'is-invalid' : ''}">
            <div class="field-hint">Between &#8377;50 and &#8377;2000 per hour. A 5% platform fee is added for the customer.</div>
            <c:if test="${not empty errors.rate}"><div class="field-error" data-for="rate">${fn:escapeXml(errors.rate)}</div></c:if>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="workStart">Work starts <span class="req">*</span></label>
            <select id="workStart" name="workStart" data-rule="required" class="${not empty errors.workStart ? 'is-invalid' : ''}">
              <c:forEach var="h" begin="5" end="23">
                <c:set var="hh"><fmt:formatNumber value="${h}" minIntegerDigits="2" groupingUsed="false"/></c:set>
                <c:set var="h12" value="${h % 12 == 0 ? 12 : h % 12}"/>
                <c:set var="ap" value="${h < 12 ? 'AM' : 'PM'}"/>
                <option value="${hh}:00" ${form.workStart == hh.concat(':00') ? 'selected' : ''}>${h12}:00 ${ap}</option>
                <option value="${hh}:30" ${form.workStart == hh.concat(':30') ? 'selected' : ''}>${h12}:30 ${ap}</option>
              </c:forEach>
            </select>
          </div>
          <div class="form-group">
            <label for="workEnd">Work ends <span class="req">*</span></label>
            <select id="workEnd" name="workEnd" data-rule="required" class="${not empty errors.workEnd ? 'is-invalid' : ''}">
              <c:forEach var="h" begin="5" end="23">
                <c:set var="hh"><fmt:formatNumber value="${h}" minIntegerDigits="2" groupingUsed="false"/></c:set>
                <c:set var="h12" value="${h % 12 == 0 ? 12 : h % 12}"/>
                <c:set var="ap" value="${h < 12 ? 'AM' : 'PM'}"/>
                <option value="${hh}:00" ${form.workEnd == hh.concat(':00') ? 'selected' : ''}>${h12}:00 ${ap}</option>
                <option value="${hh}:30" ${form.workEnd == hh.concat(':30') ? 'selected' : ''}>${h12}:30 ${ap}</option>
              </c:forEach>
            </select>
            <c:if test="${not empty errors.workEnd}"><div class="field-error" data-for="workEnd">${fn:escapeXml(errors.workEnd)}</div></c:if>
            <div class="field-hint">Customers can only book slots inside these hours (at least 2 hours).</div>
          </div>
        </div>

        <div class="form-group">
          <label for="bio">About your work <span class="req">*</span></label>
          <textarea id="bio" name="bio" rows="4" maxlength="300" data-rule="length" data-min="10" data-max="300" data-msg="Describe your work in 10 to 300 characters." class="${not empty errors.bio ? 'is-invalid' : ''}">${fn:escapeXml(form.bio)}</textarea>
          <div class="field-hint">10 to 300 characters. Shown on your public profile.</div>
          <c:if test="${not empty errors.bio}"><div class="field-error" data-for="bio">${fn:escapeXml(errors.bio)}</div></c:if>
        </div>

        <div class="row" style="justify-content:flex-end">
          <a class="btn btn-ghost" href="${ctx}/worker/dashboard">Cancel</a>
          <button type="submit" class="btn btn-primary">Save changes</button>
        </div>
      </form>
    </div>
  </div>
</div>

<jsp:include page="fragments/footer.jsp"/>
