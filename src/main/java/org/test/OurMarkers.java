package org.test;

import net.logstash.logback.marker.LogstashMarker;
import net.logstash.logback.marker.Markers;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class OurMarkers {

    public static LogstashMarker appendEntries(Map<?, ?> map) {
        return Markers.appendEntries(map);
    }

    public static LogstashMarker appendFields(Object object) {
        return Markers.appendFields(object);
    }

    public static LogstashMarker append(String fieldName, Object object) {
        return new OurObjectAppendingMarker(fieldName, object);
    }

    public static LogstashMarker appendArray(String fieldName, Object... objects) {
        return new OurObjectAppendingMarker(fieldName, objects);
    }

    public static LogstashMarker appendRaw(String fieldName, String rawJsonValue) {
        return Markers.appendRaw(fieldName, rawJsonValue);
    }

    public static LogstashMarker with(final LogstashMarker first, final LogstashMarker... others) {
        final LogstashMarker current = first;
        for (LogstashMarker each : others) {
            current.and(each);
        }
        return current;
    }

    public static LogstashMarker with(final Pair<String,Object> first, final Pair<String,Object>... others) {
        final LogstashMarker current = append(first.getLeft(), first.getRight());
        for (Pair<String,Object> each : others) {
            current.and( append(each.getLeft(), each.getRight()) );
        }
        return current;
    }
}
