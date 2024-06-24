/**
 * @author Snehal Khandve
 * Andrew ID: skhandve
 * */

package ds.project4webapp;

import com.mongodb.client.*;
import com.mongodb.client.model.BsonField;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;

/**
 * The class that handles connection to Mongo.
 * */
public class MongoConnection {

    //mongo client
    MongoClient mongoClient = null;
    //database
    MongoDatabase database = null;
    //data collection
    MongoCollection<Document> collection = null;

    /**
     * Constructor to initialize the mongo connection.
     * */
    MongoConnection() {
        try {
            mongoClient = MongoClients.create("mongodb://skhandve:YOuKkslbgwJnXxO9@ac-xlbvwwf-shard-00-00.cyi86m3.mongodb.net:27017,ac-xlbvwwf-shard-00-01.cyi86m3.mongodb.net:27017,ac-xlbvwwf-shard-00-02.cyi86m3.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
            database = mongoClient.getDatabase("sunsetsunrise");
            collection = database.getCollection("logs");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert the data into the DB.
     * */
    public void logToDatabase(ServiceLog logEntry) {
        Document log = new Document("latitude", logEntry.latitude)
                .append("longitude", logEntry.longitude)
                .append("date", logEntry.date)
                .append("apiResponseTime", logEntry.apiResponseTime)
                .append("status", logEntry.statusCode)
                .append("userAgent", logEntry.getUserAgent());

        collection.insertOne(log);
    }

    /**
     * Fetch logs from the DB to display it to the dashboard.
     * */
    public List<ServiceLog> fetchLogs() {
        FindIterable<Document> documents = collection.find();
        List<ServiceLog> logs = new ArrayList<>();

        for (Document document : documents) {
            String latitude = document.getString("latitude");
            String longitude = document.getString("longitude");
            String date = document.getString("date");
            String apiResponseTime = document.getString("apiResponseTime");
            String status = document.getString("status");
            String agent = document.getString("userAgent");

            logs.add(new ServiceLog(latitude, longitude, date, apiResponseTime, status, agent));
        }

        return logs;
    }

    public long getTotalApiRequests() {
        return collection.countDocuments();
    }

    public Map<String, Double> getResponseTimeAnalysis() {
        // Convert apiResponseTime to a double before averaging
        BsonField averageResponseTime = avg("averageResponseTime", new Document("$toDouble", "$apiResponseTime"));
        List<Bson> pipeline = Collections.singletonList(
                group(null, averageResponseTime)
        );

        Map<String, Double> analysis = new HashMap<>();
        collection.aggregate(pipeline).forEach(document -> {
            // Ensure that the averageResponseTime key exists in the document
            if (document.containsKey("averageResponseTime")) {
                analysis.put("averageResponseTime", document.getDouble("averageResponseTime"));
            }
        });

        // Handle the case where there is no data or the average could not be calculated
        if (!analysis.containsKey("averageResponseTime")) {
            analysis.put("averageResponseTime", 0.0); // Set a default value
        }

        return analysis;
    }


    public Map<String, Integer> getStatusCodeDistribution() {
        List<Bson> pipeline = Arrays.asList(
                group("$status", sum("count", 1))
        );

        Map<String, Integer> distribution = new HashMap<>();
        collection.aggregate(pipeline).forEach(document -> {
            distribution.put(document.getString("_id"), document.getInteger("count"));
        });

        return distribution;
    }

    public Map<String, Integer> getGeographicDistribution() {
        List<Bson> pipeline = Arrays.asList(
                group("$latitude", sum("count", 1))
        );

        Map<String, Integer> distribution = new HashMap<>();
        collection.aggregate(pipeline).forEach(document -> {
            distribution.put(document.getString("_id"), document.getInteger("count"));
        });

        return distribution;
    }

}
