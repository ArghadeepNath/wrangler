package io.cdap.directives.aggregates;

import io.cdap.wrangler.api.*;
import io.cdap.wrangler.api.parser.*;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.Executor;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.DirectiveParseException;
import org.bouncycastle.util.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * A directive that aggregates byte size and time duration fields across rows.
 */
public class SizeTimeAggregator implements Executor<List<Row>, ExecutorContext> {
  public static final String NAME = "aggregate-size-duration";

  private String sourceSizeCol;
  private String sourceTimeCol;
  private String targetSizeCol;
  private String targetTimeCol;
  private String outputSizeUnit = "B";
  private String outputTimeUnit = "ns";
  private String aggregationType = "total";

  private transient Store store;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME)
            .define("sourceSize", ColumnName.class)
            .define("sourceTime", ColumnName.class)
            .define("targetSize", ColumnName.class)
            .define("targetTime", ColumnName.class)
            .defineOptional("unitSize", Text.class)
            .defineOptional("unitTime", Text.class)
            .defineOptional("aggregationType", Text.class);
    return builder.build();
  }

  @Override
  public void initialize(Arguments arguments) throws DirectiveParseException {
    this.sourceSizeCol = ((ColumnName) arguments.value("sourceSize")).value();
    this.sourceTimeCol = ((ColumnName) arguments.value("sourceTime")).value();
    this.targetSizeCol = ((ColumnName) arguments.value("targetSize")).value();
    this.targetTimeCol = ((ColumnName) arguments.value("targetTime")).value();

    if (arguments.contains("unitSize")) {
      this.outputSizeUnit = ((Text) arguments.value("unitSize")).value().toUpperCase();
    }

    if (arguments.contains("unitTime")) {
      this.outputTimeUnit = ((Text) arguments.value("unitTime")).value().toLowerCase();
    }

    if (arguments.contains("aggregationType")) {
      this.aggregationType = ((Text) arguments.value("aggregationType")).value().toLowerCase();
    }
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveParseException {
    store = context.getStore(NAME);
    double totalSizeBytes = store.getOrDefault("totalSizeBytes", 0.0);
    double totalTimeNanos = store.getOrDefault("totalTimeNanos", 0.0);
    int count = store.getOrDefault("count", 0);

    for (Row row : rows) {
      Object sizeObj = row.getValue(sourceSizeCol);
      Object timeObj = row.getValue(sourceTimeCol);

      double sizeBytes = sizeObj instanceof io.cdap.wrangler.api.parser.ByteSize
              ? ((io.cdap.wrangler.api.parser.ByteSize) sizeObj).getValue()
              : parseSize(sizeObj.toString());

      double timeNanos = timeObj instanceof io.cdap.wrangler.api.parser.TimeDuration
              ? ((io.cdap.wrangler.api.parser.TimeDuration) timeObj).getValue()
              : parseTime(timeObj.toString());

      totalSizeBytes += sizeBytes;
      totalTimeNanos += timeNanos;
      count++;
    }

    store.set("totalSizeBytes", totalSizeBytes);
    store.set("totalTimeNanos", totalTimeNanos);
    store.set("count", count);

    List<Row> result = new ArrayList<>();
    return result;
  }

  @Override
  public void destroy() {

  }

  @Override
  public List<Row> finalize(ExecutorContext context) throws DirectiveExecutionException {
    store = context.getStore(NAME);

    double totalSizeBytes = store.getOrDefault("totalSizeBytes", 0.0);
    double totalTimeNanos = store.getOrDefault("totalTimeNanos", 0.0);
    int count = store.getOrDefault("count", 0);

    double resultSize = aggregationType.equals("average") ? totalSizeBytes / count : totalSizeBytes;
    double resultTime = aggregationType.equals("average") ? totalTimeNanos / count : totalTimeNanos;

    Row result = new Row();
    result.add(targetSizeCol, convertSize(resultSize));
    result.add(targetTimeCol, convertTime(resultTime));

    return List.of(result);
  }

  private double parseSize(String text) throws DirectiveExecutionException {
    return new io.cdap.wrangler.api.parser.ByteSize(text).getValue();
  }

  private double parseTime(String text) throws DirectiveExecutionException {
    return new io.cdap.wrangler.api.parser.TimeDuration(text).getValue();
  }

  private double convertSize(double bytes) {
    switch (outputSizeUnit) {
      case "KB": return bytes / 1024;
      case "MB": return bytes / (1024 * 1024);
      case "GB": return bytes / (1024 * 1024 * 1024);
      default: return bytes;
    }
  }

  private double convertTime(double nanos) {
    switch (outputTimeUnit) {
      case "ms": return nanos / 1_000_000;
      case "sec":
      case "s": return nanos / 1_000_000_000;
      case "min": return nanos / (60 * 1_000_000_000L);
      default: return nanos;
    }
  }

  @Override
  public String getName() {
    return NAME;
  }
}