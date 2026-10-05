<jsp:include page="fragments/header.jsp"><jsp:param name="title" value="Checkout"/></jsp:include>
<section class="page-hero"><div class="container"><div class="crumbs"><a href="${ctx}/workers">Find workers</a> / <a href="${ctx}/profile?id=${worker.id}">${fn:escapeXml(worker.fullName)}</a> / Checkout</div><h1>Review &amp; pay</h1><p>Check your booking, then complete the (simulated) payment.</p></div></section>
<div class="container pull-up">
  <div class="profile-grid">
    <div class="card card-body">
      <c:if test="${not empty errors.payment}"><div class="alert alert-error" role="alert">${fn:escapeXml(errors.payment)}</div></c:if>
      <form action="${ctx}/checkout" method="post" data-validate data-once>
        <input type="hidden" name="_csrf" value="${csrf}">
        <h3>Payment method</h3>
        <div class="seg pay-tabs">
          <label><input type="radio" name="method" value="UPI" ${form.method != 'CARD' ? 'checked' : ''}><span>UPI</span></label>
          <label><input type="radio" name="method" value="CARD" ${form.method == 'CARD' ? 'checked' : ''}><span>Debit / credit card</span></label>
        </div>
        <div class="pay-panel" data-panel="UPI">
          <div class="form-group"><label for="upi">UPI ID</label><input id="upi" name="upi" type="text" value="${fn:escapeXml(form.upi)}" placeholder="name@okaxis" data-rule="upi" autocomplete="off"><c:if test="${not empty errors.upi}"><div class="field-error" data-for="upi">${fn:escapeXml(errors.upi)}</div></c:if><div class="field-hint">Demo: any valid-looking id works, e.g. <span class="kbd">test@okaxis</span>. Ids ending in <span class="kbd">@fail</span> simulate a declined payment.</div></div>
        </div>
        <div class="pay-panel" data-panel="CARD">
          <div class="card-preview" aria-hidden="true"><div class="chip"></div><div class="num" id="pvNum">#### #### #### ####</div><div class="row"><span id="pvName">YOUR NAME</span><span id="pvExp">MM/YY</span></div></div>
          <div class="form-group"><label for="cardNumber">Card number</label><input id="cardNumber" name="cardNumber" type="text" inputmode="numeric" placeholder="4242 4242 4242 4242" data-rule="card" autocomplete="off"><c:if test="${not empty errors.cardNumber}"><div class="field-error" data-for="cardNumber">${fn:escapeXml(errors.cardNumber)}</div></c:if><div class="field-hint">Demo card: <span class="kbd">4242 4242 4242 4242</span> (any future expiry, any CVV).</div></div>
          <div class="form-group"><label for="cardName">Name on card</label><input id="cardName" name="cardName" type="text" value="${fn:escapeXml(form.cardName)}" data-rule="name" autocomplete="off"><c:if test="${not empty errors.cardName}"><div class="field-error" data-for="cardName">${fn:escapeXml(errors.cardName)}</div></c:if></div>
          <div class="form-row">
            <div class="form-group"><label for="expiry">Expiry (MM/YY)</label><input id="expiry" name="expiry" type="text" inputmode="numeric" placeholder="MM/YY" data-rule="expiry" autocomplete="off"><c:if test="${not empty errors.expiry}"><div class="field-error" data-for="expiry">${fn:escapeXml(errors.expiry)}</div></c:if></div>
            <div class="form-group"><label for="cvv">CVV</label><input id="cvv" name="cvv" type="password" inputmode="numeric" maxlength="3" placeholder="123" data-rule="cvv" autocomplete="off"><c:if test="${not empty errors.cvv}"><div class="field-error" data-for="cvv">${fn:escapeXml(errors.cvv)}</div></c:if></div>
          </div>
        </div>
        <button class="btn btn-primary btn-lg btn-block mt-2" type="submit">Pay <sb:money value="${total}"/> &amp; confirm booking</button>
        <p class="secure-note mt-2">&#128274; This is a simulation. No real payment is made.</p>
      </form>
    </div>

    <aside class="card card-body sticky-card">
      <h3>Booking summary</h3>
      <div class="row" style="flex-wrap:nowrap"><sb:avatar initials="${worker.initials}" seed="${worker.id}"/><div><strong>${fn:escapeXml(worker.fullName)}</strong><div class="small muted">${worker.skillIcon} ${fn:escapeXml(worker.skillName)} &middot; ${fn:escapeXml(worker.city)}</div></div></div>
      <ul class="mini-list mt-2">
        <li>&#128197; <span>${dateLabel}</span></li>
        <li>&#9200; <span>${startLabel} &ndash; ${endLabel} (${draft.hours} hour${draft.hours > 1 ? 's' : ''})</span></li>
        <li>&#128205; <span>${fn:escapeXml(draft.address)}</span></li>
        <c:if test="${not empty draft.notes}"><li>&#128221; <span>${fn:escapeXml(draft.notes)}</span></li></c:if>
      </ul>
      <div class="price-box mt-2">
        <div class="price-line"><span><sb:money value="${worker.hourlyRate}" decimals="0"/> &times; ${draft.hours} hr</span><span><sb:money value="${subtotal}"/></span></div>
        <div class="price-line"><span>Platform fee (5%)</span><span><sb:money value="${fee}"/></span></div>
        <div class="price-line total"><span>Total</span><span><sb:money value="${total}"/></span></div>
      </div>
      <p class="small muted mt-2">Free cancellation with an automatic refund before the job is completed.</p>
    </aside>
  </div>
</div>
<jsp:include page="fragments/footer.jsp"/>
