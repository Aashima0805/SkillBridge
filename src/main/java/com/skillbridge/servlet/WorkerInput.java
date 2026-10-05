package com.skillbridge.servlet;

import com.skillbridge.dao.SkillDAO;
import com.skillbridge.util.PricingUtil;
import com.skillbridge.util.Validator;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

/** Reads and validates the worker-specific form fields (used by registration and profile edit). */
final class WorkerInput {
    int skillId;
    int experience;
    BigDecimal rate;
    String bio;
    LocalTime start;
    LocalTime end;

    static WorkerInput parse(HttpServletRequest req, Map<String, String> errors, SkillDAO skillDAO) {
        WorkerInput in = new WorkerInput();
        in.skillId = Validator.toInt(req.getParameter("skillId"), 0);
        if (in.skillId <= 0 || skillDAO.findById(in.skillId) == null) {
            errors.put("skillId", "Please choose your skill.");
        }
        in.experience = Validator.toInt(req.getParameter("experience"), -1);
        if (!Validator.inRange(in.experience, 0, 50)) {
            errors.put("experience", "Experience must be a whole number from 0 to 50 years.");
        }
        try {
            in.rate = new BigDecimal(Validator.clean(req.getParameter("rate"))).setScale(2, java.math.RoundingMode.HALF_UP);
            if (in.rate.compareTo(BigDecimal.valueOf(PricingUtil.MIN_RATE)) < 0
                    || in.rate.compareTo(BigDecimal.valueOf(PricingUtil.MAX_RATE)) > 0) {
                errors.put("rate", "Hourly rate must be between " + PricingUtil.MIN_RATE + " and " + PricingUtil.MAX_RATE + ".");
            }
        } catch (NumberFormatException e) {
            errors.put("rate", "Enter your hourly rate as a number.");
        }
        in.bio = Validator.clean(req.getParameter("bio"));
        if (!Validator.lengthBetween(in.bio, 10, 300)) {
            errors.put("bio", "Tell customers about your work in 10 to 300 characters.");
        }
        try {
            in.start = LocalTime.parse(Validator.clean(req.getParameter("workStart")));
            in.end = LocalTime.parse(Validator.clean(req.getParameter("workEnd")));
            if (java.time.Duration.between(in.start, in.end).toMinutes() < 120) {
                errors.put("workEnd", "Working hours must span at least 2 hours (end after start).");
            }
        } catch (DateTimeParseException e) {
            errors.put("workEnd", "Choose valid start and end times.");
        }
        return in;
    }
}
