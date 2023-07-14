package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class ServletData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();

        String kind = req.getParameter("_kind");
        if (kind == null) {
            pw.println("Error: _kind parameter is mandatory");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String keyName = req.getParameter("_key");
        Key key;
        if (keyName != null) {
            key = KeyFactory.createKey(kind, keyName);
        } else {
            key = KeyFactory.createKey(kind, System.currentTimeMillis());
        }

        Entity entity = new Entity(key);

        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            String field = entry.getKey();
            String value = entry.getValue()[0];

            if (!"_kind".equals(field) && !"_key".equals(field)) {
                entity.setProperty(field, value);
            }
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entity);

        pw.println("Entity of kind " + kind + " saved to datastore. Key: " + entity.getKey());
    }
}
