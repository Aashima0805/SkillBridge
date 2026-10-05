package com.skillbridge.servlet;

import com.skillbridge.dao.WorkerDAO;
import com.skillbridge.model.Worker;
import com.skillbridge.util.Validator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Serves the verified workers as an XML document (/workers.xml?skill=1&amp;city=Hyderabad).
 * The browser downloads it with XMLHttpRequest and shows it as an HTML table (xml-workers page).
 */
@WebServlet("/workers.xml")
public class WorkersXmlServlet extends BaseServlet {
    private final WorkerDAO workerDAO = new WorkerDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int skillId = Validator.toInt(req.getParameter("skill"), 0);
        List<Worker> list = workerDAO.search(skillId, param(req, "city"), "", "rating");
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = doc.createElement("workers");
            root.setAttribute("count", String.valueOf(list.size()));
            root.setAttribute("generated", ZonedDateTime.now(com.skillbridge.util.Config.zone()).toString());
            doc.appendChild(root);
            for (Worker w : list) {
                Element e = doc.createElement("worker");
                e.setAttribute("id", String.valueOf(w.getId()));
                add(doc, e, "name", w.getFullName());
                add(doc, e, "skill", w.getSkillName());
                add(doc, e, "city", w.getCity());
                add(doc, e, "experience", String.valueOf(w.getExperienceYears()));
                add(doc, e, "rate", w.getHourlyRate().toPlainString());
                add(doc, e, "rating", String.valueOf(w.getAvgRating()));
                add(doc, e, "reviews", String.valueOf(w.getReviewCount()));
                add(doc, e, "hours", w.getWorkHoursLabel());
                add(doc, e, "available", String.valueOf(w.isAvailable()));
                root.appendChild(e);
            }
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            resp.setContentType("application/xml;charset=UTF-8");
            resp.setHeader("Cache-Control", "no-store");
            t.transform(new DOMSource(doc), new StreamResult(resp.getOutputStream()));
        } catch (ParserConfigurationException | TransformerException e) {
            throw new ServletException("Could not build XML", e);
        }
    }

    private void add(Document doc, Element parent, String name, String text) {
        Element child = doc.createElement(name);
        child.setTextContent(text);
        parent.appendChild(child);
    }
}
