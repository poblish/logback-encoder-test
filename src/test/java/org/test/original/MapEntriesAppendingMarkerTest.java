package org.test.original;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;
import static org.assertj.core.api.Assertions.assertThat;

// Based upon org.test.original.MapEntriesAppendingMarkerTest
public class MapEntriesAppendingMarkerTest
{
    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

    @Test
    public void testWriteTo() throws IOException {

        Map<String, String> map = new HashMap<>();
        map.put("myField", "value");

        StringWriter writer = new StringWriter();
        JsonGenerator generator = FACTORY.createGenerator(writer);

        LogstashMarker marker = appendEntries(map);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\"myField\":\"value\"}");
    }

    @Test
    public void testEquals() {
        Map<String, String> map = new HashMap<>();

        assertThat(appendEntries(map)).isEqualTo(appendEntries(map));

        Map<String, String> map2 = new HashMap<>();
        map2.put("foo", "bar");
        assertThat(appendEntries(map)).isNotEqualTo(appendEntries(map2));
    }

    @Test
    public void testHashCode() {
        Map<String, String> map = new HashMap<>();

        assertThat(appendEntries(map).hashCode()).isEqualTo(appendEntries(map).hashCode());

        Map<String, String> map2 = new HashMap<>();
        map2.put("foo", "bar");
        assertThat(appendEntries(map).hashCode()).isNotEqualTo(appendEntries(map2).hashCode());
    }
}
