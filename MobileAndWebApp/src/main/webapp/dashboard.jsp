<%--
  Created by IntelliJ IDEA.
  User: snehalkhandve
  Date: 3/31/24
  Time: 7:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="ds.project4webapp.ServiceLog" %>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
            color: #333;
        }
        h2 {
            border-bottom: 2px solid #333;
            color: #333;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }
        table, th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        td {
            background-color: #fff;
        }
        .stat {
            margin: 10px 0;
            padding: 10px;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 4px;
        }
        .no-data {
            text-align: center;
        }
    </style>
</head>
<body>
<h2>Service Usage Dashboard</h2>
<table border="1">
    <tr>
        <th>Latitude</th>
        <th>Longitude</th>
        <th>Date</th>
        <th>API Response Time</th>
        <th>User Agent Details</th>
        <th>Status</th>
    </tr>
    <%
        // Retrieve logs from request attribute
        List<ServiceLog> logs = (List<ServiceLog>) request.getAttribute("logs");
        if (logs != null) {
            for (ServiceLog log : logs) {
    %>
    <tr>
        <td><%= log.getLatitude() %></td>
        <td><%= log.getLongitude() %></td>
        <td><%= log.getDate() %></td>
        <td><%= log.getApiResponseTime() %></td>
        <td><%= log.getUserAgent() %></td>
        <td><%= log.getStatusCode() %></td>
    </tr>
    <%
        }
    } else {
    %>
    <tr>
        <td colspan="6">No logs available.</td>
    </tr>
    <%
        }
    %>
</table><br><br>

<h2>Operations analytics</h2>

    <!-- Geographic Distribution -->
    <h3>Geographic Distribution</h3>
    <table border="1" style="width:100%; border-collapse: collapse;">
        <tr>
            <th>Latitude</th>
            <th>Number of Requests</th>
        </tr>
        <%
            Map<String, Integer> geographicDistribution = (Map<String, Integer>) request.getAttribute("geographicDistribution");
            if (geographicDistribution != null && !geographicDistribution.isEmpty()) {
                for (Map.Entry<String, Integer> entry : geographicDistribution.entrySet()) {
        %>
        <tr>
            <td><%= entry.getKey() %></td>
            <td><%= entry.getValue() %></td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="2">No geographic data available.</td>
        </tr>
        <%
            }
        %>
    </table>

    <!-- Total API Requests -->
    <h3>Total API Requests</h3>
    <div id="totalApiRequests" class="analytics-box">
    <p>Total Requests: <%= request.getAttribute("totalApiRequests") %></p>
    </div>

    <!-- Response Time Analysis -->
    <h3>Response Time Analysis</h3>
    <div id="responseTimeAnalysis" class="analytics-box">
    <% Map<String, Double> responseTimeAnalysis = (Map<String, Double>) request.getAttribute("responseTimeAnalysis");
        if (responseTimeAnalysis != null && !responseTimeAnalysis.isEmpty()) {
            for (Map.Entry<String, Double> entry : responseTimeAnalysis.entrySet()) {
    %>
    <p><%= entry.getKey() %>: <%= entry.getValue() %></p>
    <%
        }
    } else {
    %>
    <p>No response time data available.</p>
    <%
        }
    %>
    </div>

    <!-- Status Code Distribution -->
    <h3>Status Code Distribution</h3>
    <div id="statusCodeDistribution" class="analytics-box">
    <% Map<String, Integer> statusCodeDistribution = (Map<String, Integer>) request.getAttribute("statusCodeDistribution");
        if (statusCodeDistribution != null && !statusCodeDistribution.isEmpty()) {
            for (Map.Entry<String, Integer> entry : statusCodeDistribution.entrySet()) {
    %>
    <p>Status Code <%= entry.getKey() %>: <%= entry.getValue() %> times</p>
    <%
        }
    } else {
    %>
    <p>No status code data available.</p>
    <%
        }
    %>
    </div>

    <!-- Error Logs Summary -->
    <h3>Error Logs Summary</h3>
    <div id="errorLogsSummary" class="analytics-box">
    <% Map<String, Integer> errorLogsSummary = (Map<String, Integer>) request.getAttribute("errorLogsSummary");
        if (errorLogsSummary != null && !errorLogsSummary.isEmpty()) {
            for (Map.Entry<String, Integer> entry : errorLogsSummary.entrySet()) {
    %>
    <p>Error Status Code <%= entry.getKey() %>: <%= entry.getValue() %> times</p>
    <%
        }
    } else {
    %>
    <p>No error logs available.</p>
    <%
        }
    %>
    </div>
</body>
</html>
