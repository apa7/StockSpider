package top.apa7.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String toJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String result = mapper.writeValueAsString(object);
            return result;
        } catch (Exception e) {
            return e.toString();
        }
    }

    private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static Map<String, Object> json2Map(String json) {
        Map<String, Object> convertedMap = null;
        try {
            convertedMap = new ObjectMapper().readValue(json, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return convertedMap;
    }

    public static List json2List(String json) {
        List convertedList = null;
        try {
            convertedList = new ObjectMapper().readValue(json, List.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convertedList;
    }

    public static Long parseLong(Object obj){
        if(obj == null){
            return null;
        }
        return Long.valueOf(obj.toString());
    }

    public static Integer parseInteger(Object obj){
        if(obj == null){
            return null;
        }
        return Integer.valueOf(obj.toString());
    }

    public static Double parseDouble(Object obj){
        if(obj == null){
            return null;
        }
        return Double.valueOf(obj.toString());
    }

}