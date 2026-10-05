<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="${errorTitle}"/></jsp:include>

<section class="error-page container">
  <div class="code-num">${errorCode}</div>
  <h1 style="font-size:clamp(1.6rem,3.5vw,2.2rem)">${fn:escapeXml(errorTitle)}</h1>
  <p class="muted" style="max-width:34rem;margin-inline:auto">${fn:escapeXml(errorText)}</p>
  <div class="row mt-3" style="justify-content:center">
    <a class="btn btn-primary" href="${ctx}/home">Back to home</a>
    <a class="btn btn-outline" href="javascript:history.back()">Go back</a>
  </div>
</section>

<jsp:include page="fragments/footer.jsp"/>
