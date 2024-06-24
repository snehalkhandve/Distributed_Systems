/**
 * @author Snehal Khandve
 * Andrew ID: skhandve
 * */
package ds.project4webapp;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * The servlet for the log dashboard.
 * */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {
    //mongodb connection
    MongoConnection connection;

    //constructor
    public DashboardServlet() {
        connection = new MongoConnection();
    }

    /**
     * Following fucntion will be called to obtain the logs.
     * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //fetch logs from the database and set it for the request
        List<ServiceLog> logs = fetchLogsFromDatabase();
        request.setAttribute("logs", logs);

        request.setAttribute("totalApiRequests", connection.getTotalApiRequests());
        request.setAttribute("responseTimeAnalysis", connection.getResponseTimeAnalysis());
        request.setAttribute("statusCodeDistribution", connection.getStatusCodeDistribution());
        request.setAttribute("geographicDistribution", connection.getGeographicDistribution());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/dashboard.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Fetches the logs from the DB.
     * */
    public List<ServiceLog> fetchLogsFromDatabase() {
        return connection.fetchLogs();
    }

    /**
     * Updates the logs to the DB.
     * */
    public void updateDb(ServiceLog logEntry) {
        connection.logToDatabase(logEntry);
    }
}
