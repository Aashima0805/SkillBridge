/* SkillBridge client-side behaviour (plain JavaScript, no libraries).
   Experiment 1: client-side scripting  | Experiment 6: AJAX form validation
   Experiment 7: XML document -> HTML table | live search & live price via XMLHttpRequest */
(function () {
  'use strict';
  var body = document.body;
  var CTX = body.getAttribute('data-ctx') || '';
  function $(s, r) { return (r || document).querySelector(s); }
  function $$(s, r) { return Array.prototype.slice.call((r || document).querySelectorAll(s)); }
  function esc(s) { var d = document.createElement('div'); d.textContent = s == null ? '' : s; return d.innerHTML; }
  function debounce(fn, ms) { var t; return function () { var a = arguments, c = this; clearTimeout(t); t = setTimeout(function () { fn.apply(c, a); }, ms); }; }
  function money(n) { return '₹' + Number(n).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 }); }

  /* ---- AJAX helper built on XMLHttpRequest ---- */
  function xhr(url, done, fail) {
    var r = new XMLHttpRequest();
    r.open('GET', url, true);
    r.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    r.onreadystatechange = function () {
      if (r.readyState !== 4) { return; }
      if (r.status >= 200 && r.status < 300) { done(r); } else if (fail) { fail(r); }
    };
    r.send();
    return r;
  }

  /* ---- Validation rules (mirror the Java Validator class) ---- */
  var RULES = {
    name: { test: function (v) { return /^[A-Za-zÀ-ɏऀ-ॿ][A-Za-zÀ-ɏऀ-ॿ .'-]{1,59}$/.test(v); }, msg: 'Use letters only (2 to 60 characters).' },
    city: { test: function (v) { return /^[A-Za-zÀ-ɏ][A-Za-zÀ-ɏ .'-]{1,39}$/.test(v); }, msg: 'Enter a city using letters only.' },
    username: { test: function (v) { return /^[A-Za-z][A-Za-z0-9_]{3,19}$/.test(v); }, msg: '4 to 20 characters, start with a letter; letters, digits, underscore.' },
    email: { test: function (v) { return v.length <= 100 && /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(v); }, msg: 'Enter a valid e-mail address.' },
    phone: { test: function (v) { return /^[6-9][0-9]{9}$/.test(v); }, msg: 'Enter a 10-digit mobile number starting with 6, 7, 8 or 9.' },
    password: { test: function (v) { return v.length >= 8 && v.length <= 64 && /[A-Za-z]/.test(v) && /[0-9]/.test(v); }, msg: '8 to 64 characters with at least one letter and one digit.' },
    upi: { test: function (v) { return /^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,32}$/.test(v); }, msg: 'Enter a valid UPI id such as name@okaxis.' },
    card: { test: function (v) { return luhn(v.replace(/[ -]/g, '')); }, msg: 'Enter a valid 16-digit card number.' },
    expiry: { test: function (v) {
      var m = /^(0[1-9]|1[0-2])\/([0-9]{2})$/.exec(v); if (!m) { return false; }
      var now = new Date(); var y = 2000 + parseInt(m[2], 10), mo = parseInt(m[1], 10);
      return y > now.getFullYear() || (y === now.getFullYear() && mo >= now.getMonth() + 1);
    }, msg: 'Enter a valid expiry (MM/YY) that is not in the past.' },
    cvv: { test: function (v) { return /^[0-9]{3}$/.test(v); }, msg: 'CVV must be 3 digits.' },
    required: { test: function (v) { return v.length > 0; }, msg: 'This field is required.' },
    number: { test: function (v) { return v !== '' && !isNaN(Number(v)); }, msg: 'Enter a number.' }
  };
  function luhn(d) {
    if (!/^[0-9]{16}$/.test(d)) { return false; }
    var sum = 0, dbl = false;
    for (var i = d.length - 1; i >= 0; i--) { var n = d.charCodeAt(i) - 48; if (dbl) { n *= 2; if (n > 9) { n -= 9; } } sum += n; dbl = !dbl; }
    return sum % 10 === 0;
  }
  window.SkillBridgeRules = RULES;

  function errorBox(field) {
    var g = field.closest('.form-group') || field.parentNode;
    var box = $('.field-error[data-for="' + (field.name || field.id) + '"]', g);
    if (!box) {
      box = document.createElement('div'); box.className = 'field-error'; box.setAttribute('data-for', field.name || field.id);
      var anchor = $('.field-status', g) || field;
      anchor.parentNode.insertBefore(box, anchor.nextSibling);
    }
    return box;
  }
  function setState(field, msg) {
    var box = errorBox(field);
    if (msg) { field.classList.add('is-invalid'); field.classList.remove('is-valid'); box.textContent = msg; box.hidden = false; field.setAttribute('aria-invalid', 'true'); }
    else { field.classList.remove('is-invalid'); if (field.value) { field.classList.add('is-valid'); } box.textContent = ''; box.hidden = true; field.removeAttribute('aria-invalid'); }
  }
  /* returns an error message or '' */
  function checkField(field, form) {
    if (field.closest('.hidden') || field.disabled) { return ''; }
    var rule = field.getAttribute('data-rule'); if (!rule) { return ''; }
    var v = field.value.trim();
    if (rule === 'password' || rule === 'confirm') { v = field.value; }
    var optional = field.getAttribute('data-optional') === 'true';
    if (v === '' && optional) { return ''; }
    if (v === '') { return field.getAttribute('data-msg-required') || 'This field is required.'; }
    if (rule === 'confirm') {
      var other = $('[name="' + field.getAttribute('data-match') + '"]', form);
      return other && other.value === v ? '' : 'The two passwords do not match.';
    }
    if (rule === 'range') {
      var n = Number(v), min = Number(field.getAttribute('data-min')), max = Number(field.getAttribute('data-max'));
      return (!isNaN(n) && n >= min && n <= max && (field.getAttribute('data-int') !== 'true' || Math.floor(n) === n)) ? '' : (field.getAttribute('data-msg') || ('Enter a value from ' + min + ' to ' + max + '.'));
    }
    if (rule === 'length') {
      var lo = Number(field.getAttribute('data-min')), hi = Number(field.getAttribute('data-max'));
      return (v.length >= lo && v.length <= hi) ? '' : (field.getAttribute('data-msg') || ('Enter ' + lo + ' to ' + hi + ' characters.'));
    }
    var r = RULES[rule]; if (!r) { return ''; }
    return r.test(v) ? '' : (field.getAttribute('data-msg') || r.msg);
  }

  $$('form[data-validate]').forEach(function (form) {
    form.setAttribute('novalidate', 'novalidate');
    var fields = $$('[data-rule]', form);
    fields.forEach(function (f) {
      f.addEventListener('blur', function () { if (f.value !== '' || f.classList.contains('touched')) { f.classList.add('touched'); setState(f, checkField(f, form)); } });
      f.addEventListener('input', function () { if (f.classList.contains('touched') || f.classList.contains('is-invalid')) { setState(f, checkField(f, form)); } if (f.name === 'password') { var c = $('[name="confirm"]', form); if (c && c.value) { setState(c, checkField(c, form)); } } });
    });
    form.addEventListener('submit', function (e) {
      var first = null;
      fields.forEach(function (f) { f.classList.add('touched'); var m = checkField(f, form); setState(f, m); if (m && !first) { first = f; } });
      $$('[data-ajax-check]', form).forEach(function (f) { if (f.getAttribute('data-taken') === 'true' && !first) { first = f; } });
      if (first) { e.preventDefault(); first.focus(); if (first.scrollIntoView) { first.scrollIntoView({ block: 'center', behavior: 'smooth' }); } return; }
      var msg = form.getAttribute('data-confirm');
      if (msg && !window.confirm(msg)) { e.preventDefault(); return; }
      var btn = $('button[type=submit]:not([data-no-loading])', form);
      if (btn) { setTimeout(function () { btn.classList.add('is-loading'); }, 0); }
    });
  });

  /* ---- Confirm dialogs on buttons/forms ---- */
  $$('form[data-confirm]:not([data-validate])').forEach(function (f) { f.addEventListener('submit', function (e) { if (!window.confirm(f.getAttribute('data-confirm'))) { e.preventDefault(); } }); });

  /* ---- Navigation toggle ---- */
  var nt = $('#navToggle'), np = $('#navPanel');
  if (nt && np) { nt.addEventListener('click', function () { var o = np.classList.toggle('open'); nt.setAttribute('aria-expanded', o ? 'true' : 'false'); }); }

  /* ---- Toasts ---- */
  $$('.toast').forEach(function (t) {
    function close() { t.classList.add('leaving'); setTimeout(function () { if (t.parentNode) { t.parentNode.removeChild(t); } }, 300); }
    var b = $('button', t); if (b) { b.addEventListener('click', close); }
    setTimeout(close, t.classList.contains('error') ? 9000 : 6000);
  });

  /* ---- Reveal on scroll + count-up numbers ---- */
  var revealEls = $$('.reveal');
  if ('IntersectionObserver' in window && revealEls.length) {
    var io = new IntersectionObserver(function (entries) { entries.forEach(function (en) { if (en.isIntersecting) { en.target.classList.add('in'); io.unobserve(en.target); } }); }, { threshold: .12 });
    revealEls.forEach(function (el) { io.observe(el); });
  } else { revealEls.forEach(function (el) { el.classList.add('in'); }); }
  $$('[data-countup]').forEach(function (el) {
    var target = parseFloat(el.getAttribute('data-countup')), dec = (el.getAttribute('data-countup').split('.')[1] || '').length, start = null;
    if (isNaN(target)) { return; }
    function step(ts) { if (start === null) { start = ts; } var p = Math.min((ts - start) / 1100, 1); var e = 1 - Math.pow(1 - p, 3); el.textContent = (target * e).toFixed(dec); if (p < 1) { requestAnimationFrame(step); } else { el.textContent = target.toFixed(dec); } }
    requestAnimationFrame(step);
  });

  /* ---- Password show/hide + strength meter ---- */
  $$('[data-toggle-pass]').forEach(function (b) {
    b.addEventListener('click', function () { var i = $('#' + b.getAttribute('data-toggle-pass')); if (!i) { return; } var show = i.type === 'password'; i.type = show ? 'text' : 'password'; b.textContent = show ? 'Hide' : 'Show'; });
  });
  var pw = $('[data-pw-meter]');
  if (pw) {
    var meter = $(pw.getAttribute('data-pw-meter'));
    pw.addEventListener('input', function () {
      var v = pw.value, s = 0;
      if (v.length >= 8) { s++; } if (/[A-Z]/.test(v) && /[a-z]/.test(v)) { s++; } if (/[0-9]/.test(v)) { s++; } if (/[^A-Za-z0-9]/.test(v) || v.length >= 14) { s++; }
      if (meter) { meter.setAttribute('data-level', v ? s : 0); var l = $('.pw-label', meter.parentNode); if (l) { l.textContent = v ? ['', 'Weak', 'Fair', 'Good', 'Strong'][s] || 'Weak' : ''; } }
    });
  }

  /* ---- Registration: role switch + AJAX availability checks (Experiment 6) ---- */
  var roleInputs = $$('input[name=role][type=radio]');
  function applyRole() {
    var sel = roleInputs.filter(function (r) { return r.checked; })[0]; if (!sel) { return; }
    $$('[data-role-only]').forEach(function (el) { var show = el.getAttribute('data-role-only') === sel.value; el.classList.toggle('hidden', !show); $$('input,select,textarea', el).forEach(function (i) { i.disabled = !show; }); });
  }
  if (roleInputs.length) { roleInputs.forEach(function (r) { r.addEventListener('change', applyRole); }); applyRole(); }

  $$('[data-ajax-check]').forEach(function (inp) {
    var field = inp.getAttribute('data-ajax-check');
    var status = $('.field-status', inp.closest('.form-group'));
    var last = '', pending = null;
    function run() {
      var v = inp.value.trim(); if (field === 'email') { v = v.toLowerCase(); }
      inp.setAttribute('data-taken', 'false');
      if (!v) { if (status) { status.textContent = ''; status.className = 'field-status'; } last = ''; return; }
      var local = checkField(inp, inp.form);
      if (local) { if (status) { status.textContent = ''; status.className = 'field-status'; } return; }
      if (v === last) { return; } last = v;
      if (status) { status.textContent = 'Checking availability…'; status.className = 'field-status wait'; }
      if (pending) { pending.abort(); }
      pending = xhr(CTX + '/ajax/check?field=' + encodeURIComponent(field) + '&value=' + encodeURIComponent(v), function (r) {
        var d; try { d = JSON.parse(r.responseText); } catch (e) { return; }
        if (!status) { return; }
        if (d.valid && d.available) { status.textContent = '✓ ' + (d.message || 'Available'); status.className = 'field-status ok'; inp.classList.add('is-valid'); inp.classList.remove('is-invalid'); }
        else { status.textContent = '✗ ' + d.message; status.className = 'field-status bad'; inp.classList.add('is-invalid'); inp.classList.remove('is-valid'); if (d.valid) { inp.setAttribute('data-taken', 'true'); } }
      }, function () { if (status) { status.textContent = ''; status.className = 'field-status'; } });
    }
    inp.addEventListener('input', debounce(run, 350));
    inp.addEventListener('blur', run);
  });

  /* ---- Live worker search (AJAX) ---- */
  var ff = $('#filterForm'), results = $('#results');
  if (ff && results) {
    var count = $('#resultCount');
    var load = function () {
      var qs = $$('input,select', ff).filter(function (i) { return i.name && i.value !== ''; }).map(function (i) { return encodeURIComponent(i.name) + '=' + encodeURIComponent(i.value); }).join('&');
      results.classList.add('results-loading');
      xhr(CTX + '/workers?ajax=1' + (qs ? '&' + qs : ''), function (r) {
        results.innerHTML = r.responseText; results.classList.remove('results-loading');
        var g = $('.worker-grid', results); if (count) { count.textContent = g ? g.getAttribute('data-count') : '0'; }
        if (window.history && history.replaceState) { history.replaceState(null, '', CTX + '/workers' + (qs ? '?' + qs : '')); }
      }, function () { results.classList.remove('results-loading'); });
    };
    ff.addEventListener('submit', function (e) { e.preventDefault(); load(); });
    $$('select', ff).forEach(function (s) { s.addEventListener('change', load); });
    var q = $('input[name=q]', ff); if (q) { q.addEventListener('input', debounce(load, 300)); }
    var clr = $('#clearFilters'); if (clr) { clr.addEventListener('click', function (e) { e.preventDefault(); $$('input,select', ff).forEach(function (i) { i.value = ''; }); load(); }); }
  }

  /* ---- Booking form: live estimate (JS) + slot availability (AJAX) ---- */
  var bf = $('#bookingForm');
  if (bf) {
    var rate = parseFloat(bf.getAttribute('data-rate')), wid = bf.getAttribute('data-worker');
    var dateI = $('[name=date]', bf), startI = $('[name=start]', bf), hoursI = $('[name=hours]', bf);
    var msg = $('#slotMsg'), sub = $('#pbSub'), fee = $('#pbFee'), tot = $('#pbTotal'), submit = $('#bookBtn');
    function estimate() {
      var h = parseInt(hoursI.value, 10) || 0, s = rate * h, f = Math.round(s * 5) / 100;
      if (sub) { sub.textContent = money(s); } if (fee) { fee.textContent = money(f); } if (tot) { tot.textContent = money(s + f); }
    }
    var req = null;
    var check = function () {
      estimate();
      if (!dateI.value || !startI.value || !hoursI.value) { msg.className = 'slot-msg wait'; msg.textContent = 'Choose a date, start time and duration to check availability.'; if (submit) { submit.disabled = true; } return; }
      msg.className = 'slot-msg wait'; msg.textContent = 'Checking availability…';
      if (req) { req.abort(); }
      req = xhr(CTX + '/ajax/slot?workerId=' + wid + '&date=' + encodeURIComponent(dateI.value) + '&start=' + encodeURIComponent(startI.value) + '&hours=' + encodeURIComponent(hoursI.value), function (r) {
        var d; try { d = JSON.parse(r.responseText); } catch (e) { return; }
        if (d.ok) { msg.className = 'slot-msg ok'; msg.textContent = '✓ ' + d.message; if (sub) { sub.textContent = money(d.subtotal); fee.textContent = money(d.fee); tot.textContent = money(d.total); } if (submit) { submit.disabled = false; } }
        else { msg.className = 'slot-msg bad'; msg.textContent = '✗ ' + d.message; if (submit) { submit.disabled = true; } }
      });
    };
    [dateI, startI, hoursI].forEach(function (i) { i.addEventListener('change', check); });
    estimate();
    if (submit) { submit.disabled = true; }
    if (dateI.value && startI.value) { check(); }
  }

  /* ---- Payment: tabs, card formatting, live preview ---- */
  var payTabs = $$('input[name=method]');
  function showPay() { var sel = payTabs.filter(function (i) { return i.checked; })[0]; if (!sel) { return; } $$('.pay-panel').forEach(function (p) { var on = p.getAttribute('data-panel') === sel.value; p.classList.toggle('active', on); $$('input', p).forEach(function (i) { i.disabled = !on; }); }); }
  if (payTabs.length) { payTabs.forEach(function (i) { i.addEventListener('change', showPay); }); showPay(); }
  var cn = $('#cardNumber'), cnm = $('#cardName'), cex = $('#expiry');
  if (cn) {
    cn.addEventListener('input', function () { var d = cn.value.replace(/\D/g, '').slice(0, 16); cn.value = d.replace(/(.{4})/g, '$1 ').trim(); var p = $('#pvNum'); if (p) { p.textContent = (d + '################').slice(0, 16).replace(/(.{4})/g, '$1 ').trim(); } });
    if (cnm) { cnm.addEventListener('input', function () { var p = $('#pvName'); if (p) { p.textContent = cnm.value.toUpperCase() || 'YOUR NAME'; } }); }
    if (cex) { cex.addEventListener('input', function () { var d = cex.value.replace(/\D/g, '').slice(0, 4); cex.value = d.length > 2 ? d.slice(0, 2) + '/' + d.slice(2) : d; var p = $('#pvExp'); if (p) { p.textContent = cex.value || 'MM/YY'; } }); }
    var cv = $('#cvv'); if (cv) { cv.addEventListener('input', function () { cv.value = cv.value.replace(/\D/g, '').slice(0, 3); }); }
  }

  /* ---- Experiment 7: XML document from the server rendered as an HTML table ---- */
  var xt = $('#xmlTable');
  if (xt) {
    var xs = $('#xmlSkill'), xc = $('#xmlCity'), tbody = $('tbody', xt), raw = $('#xmlRaw'), info = $('#xmlInfo'), rows = [], sortKey = 'rating', sortDir = -1;
    function txt(node, tag) { var n = node.getElementsByTagName(tag)[0]; return n ? n.textContent : ''; }
    function render() {
      var term = ($('#xmlFilter').value || '').toLowerCase();
      var list = rows.filter(function (r) { return !term || (r.name + ' ' + r.skill + ' ' + r.city).toLowerCase().indexOf(term) > -1; });
      list.sort(function (a, b) { var x = a[sortKey], y = b[sortKey]; if (typeof x === 'string') { return sortDir * x.localeCompare(y); } return sortDir * (x - y); });
      tbody.innerHTML = list.length ? list.map(function (r) {
        return '<tr><td>' + r.id + '</td><td><strong>' + esc(r.name) + '</strong></td><td>' + esc(r.skill) + '</td><td>' + esc(r.city) + '</td><td>' + r.experience + ' yrs</td><td>' + money(r.rate) + '</td><td>★ ' + r.rating.toFixed(1) + ' <span class="muted">(' + r.reviews + ')</span></td><td>' + esc(r.hours) + '</td><td><span class="badge ' + (r.available ? 'badge-success' : 'badge-neutral') + '">' + (r.available ? 'Available' : 'Busy') + '</span></td></tr>';
      }).join('') : '<tr><td colspan="9" class="text-center muted" style="padding:2rem">No workers in this XML document.</td></tr>';
      $$('th[data-sort]', xt).forEach(function (th) { th.setAttribute('aria-sort', th.getAttribute('data-sort') === sortKey ? (sortDir > 0 ? 'ascending' : 'descending') : 'none'); });
    }
    function fetchXml() {
      var url = CTX + '/workers.xml?skill=' + encodeURIComponent(xs.value) + '&city=' + encodeURIComponent(xc.value);
      info.textContent = 'Requesting ' + url + ' …';
      xhr(url, function (r) {
        var doc = r.responseXML; if (!doc || !doc.documentElement) { info.textContent = 'The server did not return valid XML.'; return; }
        var ws = doc.getElementsByTagName('worker'); rows = [];
        for (var i = 0; i < ws.length; i++) {
          var w = ws[i];
          rows.push({ id: w.getAttribute('id'), name: txt(w, 'name'), skill: txt(w, 'skill'), city: txt(w, 'city'), experience: parseInt(txt(w, 'experience'), 10) || 0, rate: parseFloat(txt(w, 'rate')) || 0, rating: parseFloat(txt(w, 'rating')) || 0, reviews: parseInt(txt(w, 'reviews'), 10) || 0, hours: txt(w, 'hours'), available: txt(w, 'available') === 'true' });
        }
        info.textContent = 'Parsed ' + ws.length + ' <worker> elements from ' + url;
        raw.textContent = r.responseText; render();
      }, function (r) { info.textContent = 'Could not load XML (HTTP ' + r.status + ').'; });
    }
    [xs, xc].forEach(function (s) { s.addEventListener('change', fetchXml); });
    $('#xmlFilter').addEventListener('input', render);
    $$('th[data-sort]', xt).forEach(function (th) { th.style.cursor = 'pointer'; th.addEventListener('click', function () { var k = th.getAttribute('data-sort'); if (k === sortKey) { sortDir = -sortDir; } else { sortKey = k; sortDir = (k === 'name' || k === 'skill' || k === 'city') ? 1 : -1; } render(); }); });
    var tgl = $('#xmlToggle'); if (tgl) { tgl.addEventListener('click', function () { raw.parentNode.classList.toggle('hidden'); tgl.textContent = raw.parentNode.classList.contains('hidden') ? 'Show raw XML' : 'Hide raw XML'; }); }
    fetchXml();
  }

  /* ---- Prevent double submits on plain forms ---- */
  $$('form[data-once]').forEach(function (f) { f.addEventListener('submit', function () { var b = $('button[type=submit]', f); if (b) { setTimeout(function () { b.disabled = true; }, 0); } }); });
})();
