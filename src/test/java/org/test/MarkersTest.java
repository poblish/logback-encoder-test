package org.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Charsets;
import com.google.common.primitives.Ints;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.test.OurMarkers.append;
import static org.test.utils.TestUtils.markerToString;

public class MarkersTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkersTest.class);

    @Test
    public void testOriginalMarkersImpl() throws Exception {
        long ns1 = System.nanoTime();
        long ns2 = System.nanoTime();

        final LogstashMarker marker = net.logstash.logback.marker.Markers.append("hello", "x-" + ns1).and( net.logstash.logback.marker.Markers.append("c", "d-" + ns2) );
        assertThat( markerToString(marker) ).contains("\"hello\":\"x-" + ns1 + "\",\"c\":\"d-" + ns2 + "\"");
    }

    @Test
    public void testOurMarkersImpl() throws Exception {
        long ns1 = System.nanoTime();
        long ns2 = System.nanoTime();

        final LogstashMarker marker = append("hello", "x-" + ns1).and( append("c", "d-" + ns2) );
        assertThat( markerToString(marker) ).contains("\"hello\":\"x-" + ns1 + "\",\"c\":\"d-" + ns2 + "\"");
    }

    // Should be handled just fine by Jackson's NumberSerializer (!!)
    @Test public void testBigInteger() throws Exception {
        final LogstashMarker marker = append("BigInt", new BigInteger("445402929545445402929545"));
        assertThat( markerToString(marker) ).contains("\"BigInt\":445402929545445402929545");
    }

    // Should be handled just fine by Jackson's NumberSerializer (!!)
    @Test public void testBigDecimal() throws Exception {
        final LogstashMarker marker = append("BigDecimal", new BigDecimal("3.141592653589793"));
        assertThat( markerToString(marker) ).contains("\"BigDecimal\":3.141592653589793");
    }

    @Test public void testByteArrayOldImplFails() throws Exception {
        final byte[] bs = "གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་".getBytes(Charsets.UTF_8);
        final LogstashMarker marker = net.logstash.logback.marker.Markers.append("Bytes", bs);
        assertThat( markerToString(marker) ).contains("\"Bytes\":\"4L2C4L204LyL4L2C4L264LyL4L2a4L264LyL4L2i4L2y4L2E4LyL4L2i4L6S4L6x4L2j4LyL4L2U4L284LyL\"");
    }

    @Test public void testByteArray() throws Exception {
        final LogstashMarker marker = append("Bytes", "གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་".getBytes(Charsets.UTF_8));
        assertThat( markerToString(marker) ).contains("\"Bytes\":\"གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་\"");
    }

    @Test public void testChar() throws Exception {
        final LogstashMarker marker = append("Chars", 'ག');
        assertThat( markerToString(marker) ).contains("\"Chars\":\"ག\"");
    }

    @Test public void testCharArray() throws Exception {
        final LogstashMarker marker = append("Chars", "གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་".toCharArray());
        assertThat( markerToString(marker) ).contains("\"Chars\":\"གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་\"");
    }

    @Test public void testIterator() throws Exception {
        final Iterator<String> itr = new ArrayList<>(Arrays.asList("a", "{b}", "製品に関する重要なお知らせ")).iterator();
        final LogstashMarker marker = append("Iterator", itr);
        assertThat( markerToString(marker) ).contains("\"Iterator\":[\"a\",\"{b}\",\"製品に関する重要なお知らせ\"]");
    }

    @Test public void testIterable() throws Exception {
        final Iterable<Integer> itr = Ints.asList(1979, 1981, 1983);
        final LogstashMarker marker = append("Iterable", itr);
        assertThat( markerToString(marker) ).contains("\"Iterable\":[1979,1981,1983]");
    }

    @Test public void testStreamOldImplFails() throws Exception {
        final Stream<String> stream = new ArrayList<>(Arrays.asList("a", "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"x()\"},{\"value\":\"Close\",\"onclick\":\"d()\"}]}}}", "c")).stream();
        final LogstashMarker marker = net.logstash.logback.marker.Markers.append("Stream", stream);
        assertThat( markerToString(marker) ).contains("\"Stream\":{\"parallel\":false}");
    }

    @Test public void testStream() throws Exception {
        final Stream<String> stream = new ArrayList<>(Arrays.asList("a", "{\"menu\":{\"id\":\"file\",\"value\":\"File\",\"popup\":{\"menuitem\":[{\"value\":\"New\",\"onclick\":\"x()\"},{\"value\":\"Close\",\"onclick\":\"d()\"}]}}}", "c")).stream();
        final LogstashMarker marker = append("Stream", stream);
        assertThat( markerToString(marker) ).contains("\"Stream\":[\"a\",\"{\\\"menu\\\":{\\\"id\\\":\\\"file\\\",\\\"value\\\":\\\"File\\\",\\\"popup\\\":{\\\"menuitem\\\":[{\\\"value\\\":\\\"New\\\",\\\"onclick\\\":\\\"x()\\\"},{\\\"value\\\":\\\"Close\\\",\\\"onclick\\\":\\\"d()\\\"}]}}}\",\"c\"]");
    }

    @Test public void testEnumerationsOldImplFails() throws Exception {
        final long ns1 = System.nanoTime();
        final Enumeration<String> enms = new Vector<>(Arrays.asList("製品に関する重要なお知らせ", "b", "c-" + ns1)).elements();
        final LogstashMarker marker = net.logstash.logback.marker.Markers.append("Enumeration", enms);
        assertThat( markerToString(marker) ).contains("\"Enumeration\":{}");
    }

    @Test public void testEnumerations() throws Exception {
        final long ns1 = System.nanoTime();
        final Enumeration<String> enms = new Vector<>(Arrays.asList("製品に関する重要なお知らせ", "b", "c-" + ns1)).elements();
        final LogstashMarker marker = append("Enumeration", enms);
        assertThat( markerToString(marker) ).contains("\"Enumeration\":[\"製品に関する重要なお知らせ\",\"b\",\"c-" + ns1 + "\"]");
    }

    @Test public void testOptionalPresent() throws Exception {
        assertThat( markerToString( append("Optional", Optional.of("hello")) ) ).contains("\"Optional\":\"hello\"");
    }

    @Test public void testOptionalMissing() throws Exception {
        assertThat( markerToString( append("Optional", Optional.empty()) ) ).contains("\"Optional\":\"<missing>\"");
    }

    @Test public void testStringBuilder() throws Exception {
        final StringBuilder sb = new StringBuilder("Hello-").append(1979);
        assertThat( markerToString( append("SB", sb) ) ).contains("\"SB\":\"Hello-1979\"");
    }

    @Test public void testBean() throws Exception {
        final LogstashMarker marker = append("Bean", new TestBean());
        assertThat( markerToString(marker) ).contains("\"Bean\":{\"name\":\"Hello\",\"val\":345.34234234,\"yrs\":40}");
    }

    private static class TestBean {
        @JsonProperty private String name = "Hello";
        @JsonProperty("yrs") private int age = 40;
        @JsonProperty private BigDecimal val = new BigDecimal("345.34234234");
    }
}
