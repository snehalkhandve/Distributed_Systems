/**
 * @author Snehal Khandve
 * Andrew ID: skhandve
 * */
package ds.project4webapp;

/**
 * The class that maintains the list of all attributes to the logged to the Mongo DB.
 * */
public class ServiceLog {
    // Latitude from the request
    String latitude;
    // Longitude from the request
    String longitude;
    //date from the request
    String date;
    // Time API took for the response
    String apiResponseTime;
    //status code from the API response
    String statusCode;
    //user browser and app details
    String userAgent;

    //constructor
    ServiceLog(String latitude, String longitude, String date, String apiResponseTime, String code, String userAgent) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.apiResponseTime = apiResponseTime;
        this.statusCode = code;
        this.userAgent = userAgent;
    }

   // below are the getters and setters
    public String getDate() {
        return date;
    }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getApiResponseTime() { return apiResponseTime; }
    public String getStatusCode() { return statusCode; }
    public String getUserAgent() { return userAgent; }
}

