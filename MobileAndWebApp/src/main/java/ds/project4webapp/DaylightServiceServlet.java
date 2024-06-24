/**
 * @author Snehal Khandve
 * Andrew ID: skhandve
 * */
package ds.project4webapp;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * The servlet for the app to call.
 * */
@WebServlet(name = "DaylightServiceServlet", urlPatterns = {"/daylight"})
public class DaylightServiceServlet extends HttpServlet {

    //instance of the dashboard servlet
    DashboardServlet dashboardServlet =  new DashboardServlet();

    /**
     * This function will be called by the app when it calls this servlet.
     * */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //To log browser and device details of the app making the request
        String userAgent = request.getHeader("User-Agent");

        //startTime for the ASI request
        long startTime = System.currentTimeMillis();

        //get the required parameters from the request
        String latitude = request.getParameter("lat");
        String longitude = request.getParameter("lng");
        String date = request.getParameter("date");

        //validate the parameters
        if (!isValidInput(latitude, longitude)) {
            respondWithError(response, "Invalid input parameters.");
            return;
        }

        try {
            //if the date is not set, set it as an empty string
            date = (date != null ? date : "");


            String apiUrl = "https://api.sunrise-sunset.org/json?lat=" + latitude + "&lng=" + longitude + "&date=" + date;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //reponse from the API
            int responseCode = conn.getResponseCode();

            //end time of API request processing
            long apiResponseTime = System.currentTimeMillis() - startTime;

            //update the logs to the DB.
            ServiceLog logEntry = new ServiceLog(latitude, longitude, date, String.valueOf(apiResponseTime),
                    String.valueOf(responseCode), userAgent);
            dashboardServlet.updateDb(logEntry);

            // Handle third-party API unavailability or errors
            if (responseCode != HttpURLConnection.HTTP_OK) {
                respondWithError(response, "Third-party API unavailable or returned an error.");
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();
            conn.disconnect();

            // Parse the original JSON response
            JSONObject jsonResponse = new JSONObject(content.toString());

            // Handle invalid data from the third-party API
            if (!jsonResponse.getString("status").equals("OK")) {
                respondWithError(response, "Third-party API returned invalid data.");
                return;
            }

            JSONObject results = jsonResponse.getJSONObject("results");

            // Extract required fields
            String sunrise = results.getString("sunrise");
            String sunset = results.getString("sunset");

            // Construct a new JSON object with only required fields
            JSONObject newJsonResponse = new JSONObject();
            newJsonResponse.put("sunrise", sunrise);
            newJsonResponse.put("sunset", sunset);

            // Assuming we simply pass the new JSON response back to the client
            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(newJsonResponse);
            out.flush();
        } catch (IOException e) {
            // Handle network failures
            respondWithError(response, "Network failure: Unable to reach the third-party API.");
        } catch (Exception e) {
            // Handle any other errors
            respondWithError(response, "An error occurred.");
        }
    }

    //validation is present at the app level, but double validating the attributes
    private boolean isValidInput(String latitude, String longitude) {
        if (latitude == null || latitude.isBlank() || longitude == null || longitude.isBlank()) {
            return false;
        }

        double lat = Double.parseDouble(latitude);
        double lon = Double.parseDouble(longitude);

        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            return false;
        }
        return true;
    }

    /**
     * To respond with errors in case of bad request.
     * */
    private void respondWithError(HttpServletResponse response, String errorMessage) throws IOException {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", errorMessage);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.print(errorResponse);
        out.flush();
    }
}
