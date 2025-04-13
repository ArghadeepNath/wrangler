package io.cdap.directives.aggregates;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests {@link SizeTimeAggregator}
 */
public class SizeTimeAggregatorTest {

  @Test
  public void testSizeTimeAggregatorWithNumericValues() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    
    // Row 1: 100 bytes, 200 ms
    Row row1 = new Row();
    row1.add("data_transfer_size", 100);
    row1.add("response_time", 200);
    rows.add(row1);
    
    // Row 2: 200 bytes, 300 ms
    Row row2 = new Row();
    row2.add("data_transfer_size", 200);
    row2.add("response_time", 300);
    rows.add(row2);
    
    // Row 3: 300 bytes, 400 ms
    Row row3 = new Row();
    row3.add("data_transfer_size", 300);
    row3.add("response_time", 400);
    rows.add(row3);
    
    // Define recipe
    String[] recipe = new String[] {
      "size-time-aggregator :data_transfer_size :response_time :total_size_kb :total_time_sec KB s total"
    };
    
    // Execute recipe
    List<Row> results = TestingRig.execute(recipe, rows);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 600 bytes = 0.5859375 KB, 900 ms = 0.9 seconds
    double expectedSizeKB = 600.0 / 1024.0;
    double expectedTimeSec = 900.0 / 1000.0;
    
    Assert.assertEquals(expectedSizeKB, (double) results.get(0).getValue("total_size_kb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (double) results.get(0).getValue("total_time_sec"), 0.001);
  }
  
  @Test
  public void testSizeTimeAggregatorWithStringValues() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    
    // Row 1: 1KB, 200ms
    Row row1 = new Row();
    row1.add("data_transfer_size", "1KB");
    row1.add("response_time", "200ms");
    rows.add(row1);
    
    // Row 2: 2MB, 1.5s
    Row row2 = new Row();
    row2.add("data_transfer_size", "2MB");
    row2.add("response_time", "1.5s");
    rows.add(row2);
    
    // Define recipe
    String[] recipe = new String[] {
      "size-time-aggregator :data_transfer_size :response_time :total_size_mb :total_time_sec MB s total"
    };
    
    // Execute recipe
    List<Row> results = TestingRig.execute(recipe, rows);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 1KB + 2MB = 2.000976562 MB, 200ms + 1.5s = 1.7 seconds
    double expectedSizeMB = (1 * 1024 + 2 * 1024 * 1024) / (1024.0 * 1024.0);
    double expectedTimeSec = (200 * 1_000_000 + 1.5 * 1_000_000_000) / 1_000_000_000.0;
    
    Assert.assertEquals(expectedSizeMB, (double) results.get(0).getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (double) results.get(0).getValue("total_time_sec"), 0.001);
  }
  
  @Test
  public void testSizeTimeAggregatorWithAverageAggregation() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    
    // Row 1: 100 bytes, 200 ms
    Row row1 = new Row();
    row1.add("data_transfer_size", 100);
    row1.add("response_time", 200);
    rows.add(row1);
    
    // Row 2: 200 bytes, 300 ms
    Row row2 = new Row();
    row2.add("data_transfer_size", 200);
    row2.add("response_time", 300);
    rows.add(row2);
    
    // Row 3: 300 bytes, 400 ms
    Row row3 = new Row();
    row3.add("data_transfer_size", 300);
    row3.add("response_time", 400);
    rows.add(row3);
    
    // Define recipe
    String[] recipe = new String[] {
      "size-time-aggregator :data_transfer_size :response_time :avg_size_bytes :avg_time_ms B ms average"
    };
    
    // Execute recipe
    List<Row> results = TestingRig.execute(recipe, rows);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: Average of 600/3 = 200 bytes, Average of 900/3 = 300 ms
    double expectedAvgSizeBytes = 600.0 / 3.0;
    double expectedAvgTimeMs = 900.0 / 3.0;
    
    Assert.assertEquals(expectedAvgSizeBytes, (double) results.get(0).getValue("avg_size_bytes"), 0.001);
    Assert.assertEquals(expectedAvgTimeMs, (double) results.get(0).getValue("avg_time_ms"), 0.001);
  }
  
  @Test
  public void testSizeTimeAggregatorWithMixedUnits() throws Exception {
    // Create test data
    List<Row> rows = new ArrayList<>();
    
    // Row 1: 1KB, 200ms
    Row row1 = new Row();
    row1.add("data_transfer_size", "1KB");
    row1.add("response_time", "200ms");
    rows.add(row1);
    
    // Row 2: 500 bytes, 0.5s
    Row row2 = new Row();
    row2.add("data_transfer_size", 500);
    row2.add("response_time", "0.5s");
    rows.add(row2);
    
    // Row 3: 0.5MB, 100
    Row row3 = new Row();
    row3.add("data_transfer_size", "0.5MB");
    row3.add("response_time", 100); // Assumed to be in ms
    rows.add(row3);
    
    // Define recipe
    String[] recipe = new String[] {
      "size-time-aggregator :data_transfer_size :response_time :total_size_kb :total_time_ms KB ms total"
    };
    
    // Execute recipe
    List<Row> results = TestingRig.execute(recipe, rows);
    
    // Verify results
    Assert.assertEquals(1, results.size());
    
    // Expected: 1KB + 500B + 0.5MB = 1024 + 500 + 524288 = 525812 bytes = 513.49KB
    // Expected: 200ms + 0.5s + 100ms = 200 + 500 + 100 = 800ms
    double expectedSizeKB = (1024 + 500 + 0.5 * 1024 * 1024) / 1024.0;
    double expectedTimeMs = 200 + 500 + 100;
    
    Assert.assertEquals(expectedSizeKB, (double) results.get(0).getValue("total_size_kb"), 0.001);
    Assert.assertEquals(expectedTimeMs, (double) results.get(0).getValue("total_time_ms"), 0.001);
  }
}