package org.test;

import com.google.common.base.Charsets;
import com.google.common.primitives.Ints;
import net.logstash.logback.marker.LogstashMarker;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.test.OurMarkers.append;
import static org.test.OurMarkers.with;
import static org.test.utils.TestUtils.markerToString;

public class ApiTest {

    @Test
    public void testCombinedMarkersSimple() throws IOException {
        assertThat( markerToString( with( append("x", "y"), append("c", "d"), append("e", "f")) ) )
                .contains("\"x\":\"y\",\"c\":\"d\",\"e\":\"f\"");
    }

    @Test
    public void testCombinedMarkersPairs() throws IOException {
        assertThat( markerToString( with( Pair.of("z", "s"), Pair.of("c", "d"), Pair.of("e", "f")) ) )
                .contains("\"z\":\"s\",\"c\":\"d\",\"e\":\"f\"");
    }

    @Test
    public void testCombinedMarkers() throws IOException {
        final Logger x = LoggerFactory.getLogger(ApiTest.class);

        final Iterable<Integer> itr = Ints.asList(1979, 1981, 1983);
        final LogstashMarker marker1 = append("Iterable", itr);

        final LogstashMarker marker2 = append("Bytes", "གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་".getBytes(Charsets.UTF_8));
        final LogstashMarker marker3 = append("BigDecimal", new BigDecimal("3.141592653589793"));

        x.info( with(marker1, marker2, marker3), "xxx");

        assertThat( markerToString( with(marker1, marker2, marker3) ) )
                .contains("\"BigDecimal\":3.141592653589793")
                .contains("\"Iterable\":[1979,1981,1983]")
                .contains("\"Bytes\":\"གུ་གེ་ཚེ་རིང་རྒྱལ་པོ་\"");
    }
}
