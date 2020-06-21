package org.test;

import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import net.logstash.logback.argument.StructuredArguments;
import net.logstash.logback.marker.SingleFieldAppendingMarker;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Copy of ObjectAppendingMarker
public class OurObjectAppendingMarker extends SingleFieldAppendingMarker {

    public static final String MARKER_NAME = SingleFieldAppendingMarker.MARKER_NAME_PREFIX + "OBJECT";

    private final Object payload;

    public OurObjectAppendingMarker(String fieldName, Object payload) {
        super(MARKER_NAME, fieldName);
        this.payload = payload;
    }

    @Override
    protected void writeFieldValue(JsonGenerator generator) throws IOException {
        if (payload instanceof byte[]) {  // Special case array handling
            generator.writeObject( new String((byte[]) payload, Charsets.UTF_8) );
        }
        else if (payload instanceof Optional) {
            generator.writeObject( ((Optional) payload).orElse("<missing>") );
        }
        else if (payload instanceof Enumeration) {
            generator.writeObject(Iterators.forEnumeration((Enumeration) payload));
        }
        else if (payload instanceof Stream) {
            generator.writeObject(((Stream) payload).map(Object::toString).collect( Collectors.toList() ));
        }
        else {
            generator.writeObject(payload);
        }
    }

    // Should be irrelevant
    @Override
    public Object getFieldValue() {
        return StructuredArguments.toString(payload);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OurObjectAppendingMarker that = (OurObjectAppendingMarker) o;
        return Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), payload);
    }
}
