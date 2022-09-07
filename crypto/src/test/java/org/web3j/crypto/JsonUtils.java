
package org.web3j.crypto;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;


public class JsonUtils {
    private static Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateUtils.TIMEZONE_GMT));
//        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateUtils.TIMEZONE_GMT));
//        mapper.registerModule(javaTimeModule);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static JavaType getCollectionType(Class<?> collectionClass, JavaType... parameterTypes) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, parameterTypes);
    }

    /**
     * json -> List
     *
     * @param <T>
     * @param jsonBytes
     * @param clazz
     * @return
     */
    public static <T> List<T> getObjectList(byte[] jsonBytes, Class<T> clazz) {
        try {
            JavaType javaType = getCollectionType(List.class, clazz);
            return mapper.readValue(jsonBytes, javaType);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.getObjectList error, className=%s", clazz.getName()), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }


    /**
     * json -> List
     *
     * @param <T>
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> List<T> getObjectList(String jsonString, Class<T> clazz) {
        try {
            if (jsonString == null || jsonString.trim().equals("")) {
                return Collections.emptyList();
            }
            JavaType javaType = getCollectionType(List.class, clazz);
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.getObjectList error,jsonString=%s, className=%s", jsonString, clazz.getName()), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    public static <T> T getObject(String jsonString, JavaType valueType) {
        try {
            return mapper.readValue(jsonString, valueType);
        } catch (Exception e) {
            logger.error(String.format("JsonUtils.getObject error,jsonString=%s, valueType=%s", jsonString, valueType), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    public static <T> T getObject(byte[] bytes, JavaType valueType) {
        try {
            return mapper.readValue(bytes, valueType);
        } catch (Exception e) {
            logger.error(String.format("JsonUtils.getObject error, valueType=%s", valueType), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    /**
     * json -> Map
     *
     * @param jsonString
     * @param k
     * @param v
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> getObjectMap(String jsonString, Class<K> k, Class<V> v) {
        try {
            JavaType javaType = getCollectionType(HashMap.class, k, v);
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.getObjectMap error,jsonString=%s", jsonString), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    /**
     * obj(map,List,Set,obj) -> json
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.toJson error,obj=%s", obj), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    /**
     * Object -> byte[]
     *
     * @param obj
     * @return
     */
    public static byte[] toBytes(Object obj) {
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.toBytes error,obj=%s", obj), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    /**
     * byte[] -> Object
     *
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        try {
            return mapper.readValue(bytes, clazz);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.toObject error,clazz=%s", clazz.getName()), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }

    /**
     * String -> Object
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T toObject(String json, Class<T> clazz) {

        try {
            if (json == null || "".equals(json) || clazz == null) {
                return null;
            }
            if (clazz.getName().equals(String.class.getName())) {
                return (T) json;
            }
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.error(String.format("JsonUtils.toObject error,clazz=%s", clazz.getName()), e);
            throw new IllegalArgumentException("JSON format is illegal！");
        }
    }


    public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
        private TimeZone timeZone;

        public LocalDateTimeSerializer() {
        }

        public LocalDateTimeSerializer(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            if (value != null) {
                long timestamp = value.atZone(timeZone.toZoneId()).toInstant().toEpochMilli();
                gen.writeNumber(timestamp);
            }
        }
    }

    public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
        private TimeZone timeZone;

        public LocalDateTimeDeserializer() {
        }

        public LocalDateTimeDeserializer(TimeZone timeZone) {
            this.timeZone = timeZone;
        }

        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext deserializationContext)
                throws IOException {
            long timestamp = p.getValueAsLong();
            if (timestamp > 0) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), timeZone.toZoneId());
            } else {
                return null;
            }
        }
    }

}
