package org.test.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.base.Charsets;
import net.logstash.logback.composite.JsonProviders;
import net.logstash.logback.composite.loggingevent.LogstashMarkersJsonProvider;
import net.logstash.logback.decorate.JsonGeneratorDecorator;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.marker.LogstashMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
    private static final JsonProviders<ILoggingEvent> PROVIDERS = new JsonProviders<>();

    static {
        PROVIDERS.addProvider( new LogstashMarkersJsonProvider() );
    }

    public static String markerToString(final LogstashMarker m) {
        final LogstashEncoder encoder = new LogstashEncoder();
        encoder.setJsonGeneratorDecorator( new NonEscapingJsonGeneratorDecorator() );

        final LoggingEvent evt = new LoggingEvent("x.y.z", (ch.qos.logback.classic.Logger) LOGGER, Level.INFO, "Message", null, new String[]{"argA","argB"});
        evt.setMarker(m);

        encoder.start();

        return new String(encoder.encode(evt), Charsets.UTF_8);
    }

    /*
    public static String markerToStringManual(final LogstashMarker m) throws IOException {
        try (final StringWriter writer = new StringWriter()) {
            try (final JsonGenerator generator = new MappingJsonFactory().createGenerator(writer)) {
                generator.writeStartObject();

                final LoggingEvent evt = new LoggingEvent("", (ch.qos.logback.classic.Logger) LOGGER, Level.INFO, "Message", null, new String[]{"argA","argB"});
                evt.setMarker(m);
                PROVIDERS.writeTo(generator, evt);

                generator.writeEndObject();
                generator.flush();

                return writer.toString();
            }
        }
    }
    */

    private static class NonEscapingJsonGeneratorDecorator implements JsonGeneratorDecorator {

        @Override
        public JsonGenerator decorate(JsonGenerator generator) {
            return generator.disable(JsonGenerator.Feature.ESCAPE_NON_ASCII);
        }
    }
}
