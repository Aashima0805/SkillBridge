# SkillBridge – Test Cases (Experiment 10)

Techniques: **black-box functional testing**, **boundary value analysis (BVA)**, **equivalence partitioning**,
automated **unit tests** (JUnit 5, `mvn test`) and a manual **end-to-end** pass.

## A. Boundary value analysis (BV-01 to BV-12 are automated in `ValidatorTest` and `PricingAndSlotTest`; BV-13 to BV-15 are checked manually)
| ID | Input | Boundary | Expected |
|---|---|---|---|
| BV-01 | Username `abc` / `abcd` | 3 / 4 chars | Rejected / accepted |
| BV-02 | Username 20 / 21 chars | max | Accepted / rejected |
| BV-03 | Password 7 / 8 chars (letter+digit) | min | Rejected / accepted |
| BV-04 | Password 64 / 65 chars | max | Accepted / rejected |
| BV-05 | Phone 9 / 10 / 11 digits | length | Rejected / accepted / rejected |
| BV-06 | Phone starting 5 / 6 | first digit | Rejected / accepted |
| BV-07 | Booking hours 0 / 1 / 8 / 9 | 1–8 | Rejected / ok / ok / rejected |
| BV-08 | Start 08:00 (opening) / 07:30 | working hours | Accepted / rejected |
| BV-09 | 16:00 for 2 h (ends at closing) / 16:30 | working hours | Accepted / rejected |
| BV-10 | Date yesterday / today+60 / today+61 | window | Rejected / ok / rejected |
| BV-11 | Same-day start 10:30 / 11:00 at 10:00 | 1 h notice | Rejected / accepted |
| BV-12 | Card expiry current month / last month | expiry | Accepted / rejected |
| BV-13 | Hourly rate 49 / 50 / 2000 / 2001 | 50–2000 | Rejected / ok / ok / rejected |
| BV-14 | Experience -1 / 0 / 50 / 51 | 0–50 | Rejected / ok / ok / rejected |
| BV-15 | Rating 0 / 1 / 5 / 6 | 1–5 | Rejected / ok / ok / rejected |

## B. Black-box functional tests (manual)
| ID | Feature | Steps | Expected result |
|---|---|---|---|
| FT-01 | Register customer | /register → valid data | Redirect to login, success message |
| FT-02 | Duplicate username | Register with `customer1` | Inline "Already registered" (AJAX) and server error on submit |
| FT-03 | Password mismatch | Different confirm password | Field error, no submit |
| FT-04 | Register worker | Choose "Find work", fill profile | Account created; worker not visible in search until verified |
| FT-05 | Login valid / invalid | `customer1` / wrong password | Dashboard redirect / "Invalid username/e-mail or password" |
| FT-06 | Remember me | Tick box, close browser, reopen | Still logged in (7 days) |
| FT-07 | Role protection | Customer opens /admin | 403 page |
| FT-08 | Anonymous protection | Open /my-bookings logged out | Redirect to login, returns after login |
| FT-09 | Search and filter | Skill = Plumber, City = Hyderabad | Only matching verified workers, updates live |
| FT-10 | Book a slot | Profile → pick date/time/hours/address | Green "slot is free", live price, continue |
| FT-11 | Double booking | Book same worker/time again | "Already booked…" message, button disabled |
| FT-12 | Payment UPI success | `test@okaxis` | Booking created (Pending), reference shown |
| FT-13 | Payment declined | UPI `x@fail` or card ending 0002 | Error shown, no booking created |
| FT-14 | Invalid card | Bad Luhn number | Inline error |
| FT-15 | Worker accepts | Worker dashboard → Accept | Status Accepted |
| FT-16 | Worker completes | Mark completed | Status Completed, customer can review |
| FT-17 | Review once | Post 5★ review twice | Second attempt blocked |
| FT-18 | Cancel + refund | Customer cancels Pending booking | Status Cancelled, payment Refunded |
| FT-19 | Admin verify | Verify pending worker | Worker appears in search |
| FT-20 | Admin skills CRUD | Add, edit, delete unused skill | Success; used skill cannot be deleted |
| FT-21 | Deactivate user | Admin deactivates a user | User can no longer log in |
| FT-22 | XML table | Open /xml-workers | Table built from /workers.xml, sortable and filterable |
| FT-23 | Cookies/session | Open /session-info repeatedly | Visit counter increases; session/cookie details shown |
| FT-24 | CSRF | POST without token | 403 |
| FT-25 | XSS | Name `<script>alert(1)</script>` rejected by validation; notes escaped on output | No script runs |
| FT-26 | 404 page | /no-such-page | Friendly error page |

## C. Automated test summary
Run `mvn test` – 41 unit tests covering validation rules, pricing, booking-slot rules, BCrypt hashing,
remember-me token signing/expiry and JSON escaping. All pass.

## D. End-to-end check performed on the delivered build
Deployed on Tomcat 10.1 with MariaDB: all public, customer, worker and admin pages return HTTP 200; full flow
(search → slot check → book → pay → worker accept → complete → review → admin verify) verified.
