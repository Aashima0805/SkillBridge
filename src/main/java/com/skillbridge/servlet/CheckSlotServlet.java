package com.skillbridge.servlet;

import com.skillbridge.service.BookingService;
import com.skillbridge.util.Json;
import com.skillbridge.util.PricingUtil;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/** AJAX endpoint: is the worker free at this date/time? Also returns the live price. */
@WebServlet("/ajax/slot")
public class CheckSlotServlet extends BaseServlet {
    private final BookingService service = new BookingService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int workerId = Validator.toInt(req.getParameter("workerId"), 0);
        int hours = Validator.toInt(req.getParameter("hours"), 0);
        BookingService.Check chk = service.check(workerId, param(req, "date"), param(req, "start"), hours);
        if (!chk.ok()) {
            json(resp, Json.obj("ok", false, "message", chk.message()));
            return;
        }
        BigDecimal sub = PricingUtil.subtotal(chk.worker().getHourlyRate(), hours);
        BigDecimal fee = PricingUtil.platformFee(sub);
        json(resp, Json.obj("ok", true, "message", chk.message(),
                "subtotal", sub.toPlainString(), "fee", fee.toPlainString(), "total", sub.add(fee).toPlainString()));
    }
}
