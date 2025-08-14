package com.gradlemedium100.dataaccess.interceptor;

import org.hibernate.EmptyInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Hibernate interceptor for monitoring and logging SQL query performance.
 * This interceptor tracks execution time of SQL queries and logs slow queries
 * that exceed the configured threshold.
 */
@Component
public class QueryPerformanceInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;
    
    /**
     * Threshold in milliseconds for logging slow queries.
     * Queries that take longer than this threshold will be logged as slow queries.
     */
    private long slowQueryThresholdMs;
    
    /**
     * Logger for recording performance information.
     */
    private final Logger logger = LoggerFactory.getLogger(QueryPerformanceInterceptor.class);
    
    /**
     * Map to store start times for queries.
     */
    private final ConcurrentHashMap<String, Long> queryStartTimes = new ConcurrentHashMap<>();
    
    /**
     * Counter for unique query identifiers.
     */
    private final AtomicLong queryCounter = new AtomicLong(0);

    /**
     * Constructor that initializes the slow query threshold from application properties.
     * 
     * @param slowQueryThresholdMs Threshold in milliseconds for logging slow queries
     */
    public QueryPerformanceInterceptor(@Value("${hibernate.query.slow_threshold_ms:1000}") long slowQueryThresholdMs) {
        this.slowQueryThresholdMs = slowQueryThresholdMs;
        logger.info("QueryPerformanceInterceptor initialized with slow query threshold of {} ms", slowQueryThresholdMs);
    }

    /**
     * Intercept SQL statements before they are executed.
     * This method starts timing the query execution.
     * 
     * @param sql The SQL statement to be executed
     * @return The original SQL statement
     */
    @Override
    public String onPrepareStatement(String sql) {
        // Generate a unique identifier for this query execution
        String queryId = String.valueOf(queryCounter.incrementAndGet());
        
        // Record the start time
        queryStartTimes.put(queryId, System.currentTimeMillis());
        
        // Add the query ID as a comment to the SQL for correlation
        String annotatedSql = sql + " /* queryId=" + queryId + " */";
        
        if (logger.isDebugEnabled()) {
            logger.debug("Executing SQL [{}]: {}", queryId, sql);
        }
        
        return annotatedSql;
    }

    /**
     * Log performance details of SQL queries.
     * This method is called internally after a query has completed execution.
     * 
     * @param sql The SQL statement that was executed
     * @param executionTime The execution time in milliseconds
     */
    public void logQueryPerformance(String sql, long executionTime) {
        // Extract query ID from the SQL comment if present
        String queryId = extractQueryId(sql);
        
        // Basic logging for all queries
        if (logger.isDebugEnabled()) {
            logger.debug("Query [{}] executed in {} ms: {}", 
                    queryId, executionTime, sql);
        }
        
        // Log slow queries with warning level
        if (executionTime > slowQueryThresholdMs) {
            logger.warn("SLOW QUERY [{}] - execution time: {} ms (exceeds threshold of {} ms): {}", 
                    queryId, executionTime, slowQueryThresholdMs, sql);
        }
    }
    
    /**
     * Override the postFlush method to calculate query execution times.
     * 
     * @param entities The entities affected by the flush operation
     */
    @Override
    public void postFlush(Iterator entities) {
        // Process any remaining tracked queries
        processRemainingQueries();
        super.postFlush(entities);
    }
    
    /**
     * Extract the query ID from the SQL statement comment.
     * 
     * @param sql The SQL statement with embedded query ID
     * @return The extracted query ID or "unknown" if not found
     */
    private String extractQueryId(String sql) {
        // Simple pattern matching to extract the query ID from the comment
        int start = sql.lastIndexOf("queryId=");
        if (start > 0) {
            int end = sql.indexOf(" ", start);
            if (end > start) {
                return sql.substring(start + 8, end - 2); // Remove "queryId=" and "*/"
            } else {
                return sql.substring(start + 8, sql.length() - 2);
            }
        }
        return "unknown";
    }
    
    /**
     * Process any queries that have started but not completed tracking.
     * This is a cleanup mechanism to ensure all queries are properly logged.
     */
    private void processRemainingQueries() {
        long currentTime = System.currentTimeMillis();
        
        queryStartTimes.forEach((queryId, startTime) -> {
            long executionTime = currentTime - startTime;
            logger.debug("Query [{}] processing time: {} ms", queryId, executionTime);
            
            // Remove the processed entry
            queryStartTimes.remove(queryId);
        });
    }

    /**
     * Set the threshold for logging slow queries.
     * This method allows runtime modification of the threshold.
     * 
     * @param slowQueryThresholdMs The new threshold in milliseconds
     */
    public void setSlowQueryThresholdMs(long slowQueryThresholdMs) {
        this.slowQueryThresholdMs = slowQueryThresholdMs;
        logger.info("Slow query threshold updated to {} ms", slowQueryThresholdMs);
    }
    
    /**
     * Get the current threshold for logging slow queries.
     * 
     * @return The current threshold in milliseconds
     */
    public long getSlowQueryThresholdMs() {
        return slowQueryThresholdMs;
    }
}