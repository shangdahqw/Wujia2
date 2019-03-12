package com.example.wujia2.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @Author huangqiangwen
 *
 * @create 2019/2/27 下午1:4 @Description
 */
public class JsonUtils {

  public static final ObjectMapper mapper = new ObjectMapper();

  public static String serialize(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj.getClass() == String.class) {
      return (String) obj;
    }
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      System.out.println("json序列化出错：" + e);
      return null;
    }
  }

  public static <T> T parse(String json, Class<T> tClass) {
    try {
      return mapper.readValue(json, tClass);
    } catch (IOException e) {
      System.out.println("json解析出错：" + e);
      return null;
    }
  }

  public static <E> List<E> parseList(String json, Class<E> eClass) {
    try {
      return mapper.readValue(
          json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
    } catch (IOException e) {
      System.out.println("json解析出错：" + e);
      return null;
    }
  }

  public static <K, V> Map<K, V> parseMap(String json, Class<K> kClass, Class<V> vClass) {
    try {
      return mapper.readValue(
          json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
    } catch (IOException e) {
      System.out.println("json解析出错：" + e);
      return null;
    }
  }

  public static <T> T nativeRead(String json, TypeReference<T> type) {
    try {
      return mapper.readValue(json, type);
    } catch (IOException e) {
      System.out.println("json解析出错：" + e);
      return null;
    }
  }
}
