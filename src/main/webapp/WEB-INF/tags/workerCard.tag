<%@ tag body-content="empty" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="sb" tagdir="/WEB-INF/tags" %>
<%@ attribute name="w" required="true" type="com.skillbridge.model.Worker" %>
<article class="card card-hover worker-card">
  <div class="top">
    <sb:avatar initials="${w.initials}" seed="${w.id}"/>
    <div>
      <h3><a href="${ctx}/profile?id=${w.id}" style="color:inherit">${fn:escapeXml(w.fullName)}</a></h3>
      <div class="sub">${w.skillIcon} ${fn:escapeXml(w.skillName)} &middot; ${fn:escapeXml(w.city)}</div>
    </div>
  </div>
  <p class="bio">${fn:escapeXml(w.bio)}</p>
  <div class="meta">
    <span class="verified"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l2.4 2.1 3.2-.3 1 3 2.8 1.7-.9 3.1.9 3.1-2.8 1.7-1 3-3.2-.3L12 22l-2.4-2.1-3.2.3-1-3-2.8-1.7.9-3.1-.9-3.1 2.8-1.7 1-3 3.2.3z"/><path d="M8.5 12.2l2.4 2.4 4.6-4.8" stroke="#fff" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/></svg>Verified</span>
    <span class="badge badge-neutral">${w.experienceYears} yrs exp</span>
    <span class="badge badge-neutral">${w.workHoursLabel}</span>
  </div>
  <div class="foot">
    <div><div class="price"><sb:money value="${w.hourlyRate}" decimals="0"/> <small>/ hour</small></div><sb:stars rating="${w.avgRating}" count="${w.reviewCount}"/></div>
    <a class="btn btn-primary btn-sm" href="${ctx}/profile?id=${w.id}">View &amp; book</a>
  </div>
</article>
