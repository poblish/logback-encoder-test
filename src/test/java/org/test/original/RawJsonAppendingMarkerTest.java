package org.test.original;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static net.logstash.logback.marker.Markers.appendRaw;
import static org.assertj.core.api.Assertions.assertThat;

// Based upon org.test.original.RawJsonAppendingMarkerTest
public class RawJsonAppendingMarkerTest {

    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

    @Test
    public void testWriteTo() throws IOException {

        String rawJson = "{\"myField\":\"value\"}";

        StringWriter writer = new StringWriter();
        JsonGenerator generator = FACTORY.createGenerator(writer);

        LogstashMarker marker = appendRaw("rawJson", rawJson);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\"rawJson\":{\"myField\":\"value\"}}");
    }

    @Test
    public void testEquals() {
        String rawJson = "{\"myField\":\"value\"}";

        assertThat(appendRaw("rawJson", rawJson)).isEqualTo(appendRaw("rawJson", rawJson));

        assertThat(appendRaw("rawJson", rawJson)).isNotEqualTo(appendRaw("rawJson", ""));

        assertThat(appendRaw("rawJson", rawJson)).isNotEqualTo(appendRaw("myDifferentObject", rawJson));
    }

    @Test
    public void testHashCode() {
        String rawJson = "{\"myField\":\"value\"}";

        assertThat(appendRaw("rawJson", rawJson).hashCode()).isEqualTo(appendRaw("rawJson", rawJson).hashCode());

        assertThat(appendRaw("rawJson", rawJson).hashCode()).isNotEqualTo(appendRaw("rawJson", "").hashCode());

        assertThat(appendRaw("rawJson", rawJson).hashCode()).isNotEqualTo(appendRaw("myDifferentObject", rawJson)).hashCode();
    }

}
