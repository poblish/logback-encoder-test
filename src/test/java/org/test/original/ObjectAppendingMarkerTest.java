package org.test.original;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static net.logstash.logback.marker.Markers.append;
// import static org.test.OurMarkers.append;
import static org.assertj.core.api.Assertions.assertThat;

// Based upon org.test.original.ObjectAppendingMarkerTest
public class ObjectAppendingMarkerTest {

    private static final JsonFactory FACTORY = new MappingJsonFactory().enable(JsonGenerator.Feature.ESCAPE_NON_ASCII);

    public static class MyClass {
        private String myField;

        public MyClass(String myField) {
            this.myField = myField;
        }

        public String getMyField() {
            return myField;
        }

        public void setMyField(String myField) {
            this.myField = myField;
        }
    }

    @Test
    public void testWriteTo() throws IOException {

        MyClass myObject = new MyClass("value");

        StringWriter writer = new StringWriter();
        JsonGenerator generator = FACTORY.createGenerator(writer);

        LogstashMarker marker = append("myObject", myObject);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\"myObject\":{\"myField\":\"value\"}}");
    }

    @Test
    public void testEquals() {
        MyClass myObject = new MyClass("value");

        assertThat(append("myObject", myObject)).isEqualTo(append("myObject", myObject));

        assertThat(append("myObject", myObject)).isNotEqualTo(append("myObject", new MyClass("value1")));

        assertThat(append("myObject", myObject)).isNotEqualTo(append("myDifferentObject", myObject));
    }

    @Test
    public void testHashCode() {
        MyClass myObject = new MyClass("value");

        assertThat(append("myObject", myObject).hashCode()).isEqualTo(append("myObject", myObject).hashCode());

        assertThat(append("myObject", myObject).hashCode()).isNotEqualTo(append("myObject", new MyClass("value1")).hashCode());

        assertThat(append("myObject", myObject).hashCode()).isNotEqualTo(append("myDifferentObject", myObject)).hashCode();
    }

}
