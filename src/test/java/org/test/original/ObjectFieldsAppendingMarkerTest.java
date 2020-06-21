package org.test.original;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import net.logstash.logback.marker.LogstashMarker;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static net.logstash.logback.marker.Markers.appendFields;
import static org.assertj.core.api.Assertions.assertThat;

// Based upon org.test.original.ObjectFieldsAppendingMarkerTest
public class ObjectFieldsAppendingMarkerTest {

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

        LogstashMarker marker = appendFields(myObject);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{\"myField\":\"value\"}");
    }

    @Test
    public void testWriteTo_nonUnwrappable() throws IOException {

        StringWriter writer = new StringWriter();
        JsonGenerator generator = FACTORY.createGenerator(writer);

        LogstashMarker marker = appendFields(1L);
        generator.writeStartObject();
        marker.writeTo(generator);
        generator.writeEndObject();
        generator.flush();

        assertThat(writer.toString()).isEqualTo("{}");
    }

    @Test
    public void testEquals() {
        MyClass myObject = new MyClass("value");

        assertThat(appendFields(myObject)).isEqualTo(appendFields(myObject));

        assertThat(appendFields(myObject)).isNotEqualTo(appendFields(new MyClass("value1")));
    }

    @Test
    public void testHashCode() {
        MyClass myObject = new MyClass("value");

        assertThat(appendFields(myObject).hashCode()).isEqualTo(appendFields(myObject).hashCode());

        assertThat(appendFields(myObject).hashCode()).isNotEqualTo(appendFields(new MyClass("value1")).hashCode());
    }

}
