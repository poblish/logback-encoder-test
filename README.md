# logback-encoder-test

### Goals:

1. Establish (via tests) how the `net.logstash.logback.encoder.LogstashEncoder` behaves with various different types of `Marker` payload, including `byte[]`, `Enumeration`, `Optional`, `BigInteger`, `BigDecimal`, `Stream`, `Iterator`, `Iterable`, etc. For example:

    ```java
    import static org.test.OurMarkers.append;

    LogstashMarker m = append("Bytes", "གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་".getBytes(Charsets.UTF_8));
    assertThat( markerToString(m) ).contains("\"Bytes\":\"གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་\"");

    ...

    Enumeration<String> e = new Vector<>(Arrays.asList("製品に関する重要なお知らせ", "b", "c")).elements();
    LogstashMarker m = append("Enum", e);

    assertThat( markerToString(m) ).contains("\"Enum\":[\"製品に関する重要なお知らせ\",\"b\",\"c\"]");

    ...

    assertThat( markerToString( append("Opt", Optional.of("hello")) ) ).contains("\"Opt\":\"hello\"");
    assertThat( markerToString( append("Opt", Optional.empty()) ) ).contains("\"Opt\":\"<missing>\"");
    ```

1. Demonstrate a (hopefully) more helpful API for joining multiple `Marker`s for a SLF4J log event. For example:

    ```java
    import org.apache.commons.lang3.tuple.Pair;

    import static org.test.OurMarkers.with;

    ...

    logger.info( with(marker1, marker2, marker3), "event");

    logger.info( with( Pair.of("a","b"), Pair.of("c","d")), "event");
    ```