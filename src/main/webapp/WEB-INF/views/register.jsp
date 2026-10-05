<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Create account"/></jsp:include>
<div class="auth-shell">
  <aside class="auth-side">
    <span class="eyebrow" style="align-self:flex-start;background:rgba(255,255,255,.12);color:#c7d2fe">Join SkillBridge</span>
    <h2>Find work. Find workers. Build trust.</h2>
    <ul>
      <li><span class="tick">&#10003;</span><span><strong>Customers</strong> book verified workers and pay securely.</span></li>
      <li><span class="tick">&#10003;</span><span><strong>Workers</strong> set their own rate and hours, and get steady direct jobs.</span></li>
      <li><span class="tick">&#10003;</span><span>Every worker profile is checked by an admin before it goes live.</span></li>
    </ul>
  </aside>
  <div class="auth-main">
    <div class="auth-card" style="width:min(560px,100%)">
      <h1>Create your account</h1>
      <p class="muted">Already registered? <a href="${ctx}/login">Log in</a></p>
      <form action="${ctx}/register" method="post" data-validate data-once>
        <input type="hidden" name="_csrf" value="${csrf}">
        <div class="form-group"><span class="label">I want to</span>
          <div class="seg">
            <label><input type="radio" name="role" value="CUSTOMER" ${form.role != 'WORKER' ? 'checked' : ''}><span>Hire workers</span></label>
            <label><input type="radio" name="role" value="WORKER" ${form.role == 'WORKER' ? 'checked' : ''}><span>Find work</span></label>
          </div></div>

        <div class="form-group"><label for="fullName">Full name</label><input id="fullName" name="fullName" type="text" value="${fn:escapeXml(form.fullName)}" data-rule="name" autocomplete="name"><c:if test="${not empty errors.fullName}"><div class="field-error" data-for="fullName">${fn:escapeXml(errors.fullName)}</div></c:if></div>
        <div class="form-row">
          <div class="form-group"><label for="username">Username</label><input id="username" name="username" type="text" value="${fn:escapeXml(form.username)}" data-rule="username" data-ajax-check="username" autocomplete="username"><div class="field-status"></div><c:if test="${not empty errors.username}"><div class="field-error" data-for="username">${fn:escapeXml(errors.username)}</div></c:if></div>
          <div class="form-group"><label for="phone">Mobile number</label><input id="phone" name="phone" type="tel" inputmode="numeric" maxlength="10" value="${fn:escapeXml(form.phone)}" data-rule="phone" data-ajax-check="phone" autocomplete="tel"><div class="field-status"></div><c:if test="${not empty errors.phone}"><div class="field-error" data-for="phone">${fn:escapeXml(errors.phone)}</div></c:if></div>
        </div>
        <div class="form-row">
          <div class="form-group"><label for="email">E-mail</label><input id="email" name="email" type="email" value="${fn:escapeXml(form.email)}" data-rule="email" data-ajax-check="email" autocomplete="email"><div class="field-status"></div><c:if test="${not empty errors.email}"><div class="field-error" data-for="email">${fn:escapeXml(errors.email)}</div></c:if></div>
          <div class="form-group"><label for="city">City</label><input id="city" name="city" type="text" value="${fn:escapeXml(form.city)}" data-rule="city" list="cityList" autocomplete="address-level2"><datalist id="cityList"><option>Hyderabad</option><option>Bengaluru</option><option>Chennai</option><option>Delhi</option><option>Pune</option><option>Kolkata</option><option>Mumbai</option></datalist><c:if test="${not empty errors.city}"><div class="field-error" data-for="city">${fn:escapeXml(errors.city)}</div></c:if></div>
        </div>
        <div class="form-row">
          <div class="form-group"><label for="password">Password</label><input id="password" name="password" type="password" data-rule="password" data-pw-meter="#pwMeter" autocomplete="new-password"><button type="button" class="toggle-pass" data-toggle-pass="password">Show</button><div class="pw-meter" id="pwMeter" data-level="0"><i></i></div><div class="field-hint">8+ characters with a letter and a digit. <strong class="pw-label"></strong></div><c:if test="${not empty errors.password}"><div class="field-error" data-for="password">${fn:escapeXml(errors.password)}</div></c:if></div>
          <div class="form-group"><label for="confirm">Confirm password</label><input id="confirm" name="confirm" type="password" data-rule="confirm" data-match="password" autocomplete="new-password"><c:if test="${not empty errors.confirm}"><div class="field-error" data-for="confirm">${fn:escapeXml(errors.confirm)}</div></c:if></div>
        </div>

        <div data-role-only="WORKER" class="${form.role == 'WORKER' ? '' : 'hidden'}">
          <hr><h3>Your work profile</h3>
          <div class="form-row">
            <div class="form-group"><label for="skillId">Skill</label><select id="skillId" name="skillId" data-rule="required" data-msg-required="Please choose your skill."><option value="">Select skill</option><c:forEach var="s" items="${skills}"><option value="${s.id}" ${form.skillId == s.id ? 'selected' : ''}>${s.icon} ${fn:escapeXml(s.name)}</option></c:forEach></select><c:if test="${not empty errors.skillId}"><div class="field-error" data-for="skillId">${fn:escapeXml(errors.skillId)}</div></c:if></div>
            <div class="form-group"><label for="experience">Experience (years)</label><input id="experience" name="experience" type="number" min="0" max="50" value="${fn:escapeXml(form.experience)}" data-rule="range" data-min="0" data-max="50" data-int="true" data-msg="Enter whole years from 0 to 50."><c:if test="${not empty errors.experience}"><div class="field-error" data-for="experience">${fn:escapeXml(errors.experience)}</div></c:if></div>
          </div>
          <div class="form-row form-row-3">
            <div class="form-group"><label for="rate">Hourly rate (&#8377;)</label><input id="rate" name="rate" type="number" min="50" max="2000" step="10" value="${fn:escapeXml(form.rate)}" data-rule="range" data-min="50" data-max="2000" data-msg="Rate must be between 50 and 2000."><c:if test="${not empty errors.rate}"><div class="field-error" data-for="rate">${fn:escapeXml(errors.rate)}</div></c:if></div>
            <div class="form-group"><label for="workStart">Work from</label><input id="workStart" name="workStart" type="time" step="1800" value="${empty form.workStart ? '08:00' : fn:escapeXml(form.workStart)}"></div>
            <div class="form-group"><label for="workEnd">Work until</label><input id="workEnd" name="workEnd" type="time" step="1800" value="${empty form.workEnd ? '18:00' : fn:escapeXml(form.workEnd)}"><c:if test="${not empty errors.workEnd}"><div class="field-error" data-for="workEnd">${fn:escapeXml(errors.workEnd)}</div></c:if></div>
          </div>
          <div class="form-group"><label for="bio">About your work</label><textarea id="bio" name="bio" rows="3" maxlength="300" data-rule="length" data-min="10" data-max="300" data-msg="Describe your work in 10 to 300 characters." placeholder="Years of experience, what you do best, tools you bring...">${fn:escapeXml(form.bio)}</textarea><c:if test="${not empty errors.bio}"><div class="field-error" data-for="bio">${fn:escapeXml(errors.bio)}</div></c:if></div>
          <div class="alert alert-info">An admin will verify your profile before customers can see it.</div>
        </div>
        <button class="btn btn-primary btn-block btn-lg" type="submit">Create account</button>
      </form>
    </div>
  </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
