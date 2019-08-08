package com.example.demo.okhttp3;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * @author relics9
 *
 * @param <T>
 */
@Slf4j
public class Okhttp3<T> {

	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	private final OkHttpClient httpClient;
	private final ObjectMapper mapper;
	private final String url;

	/**
	 * @param url
	 */
	public Okhttp3(String url) {
		// set URL.
		this.url = url;
		// create ObjectMapper instance
		this.mapper = new ObjectMapper()
				// .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
				.enable(SerializationFeature.INDENT_OUTPUT);
		// create OkHttpClient instance
		HttpLoggingInterceptor httpLogging = new HttpLoggingInterceptor();
		httpLogging.level(Level.BODY);
		this.httpClient = new OkHttpClient.Builder().addInterceptor(httpLogging).build();

	}

	/**
	 * @param o
	 * @return
	 */
	private String serialize(Object o) {
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			log.error("Json serialize error", e);
		}
		return null;
	}

	/**
	 * @param json
	 * @param valueType
	 * @return
	 */
	private T deserialize(String json, Class<T> valueType) {
		try {
			return (T) mapper.readValue(json, valueType);
		} catch (IOException e) {
			log.error("Json deserialize error", e);
		}
		return null;
	}

	/**
	 * @param json
	 * @return
	 */
	public String post(String json) {
		RequestBody body = RequestBody.create(json, JSON);
		Request request = new Request.Builder().url(url).post(body).build();
		try (Response response = httpClient.newCall(request).execute()) {
			return response.body().string();
		} catch (IOException e) {
			log.error("Http request error", e);
		}
		return null;
	}

	/**
	 * @param json
	 * @param valueType
	 * @return
	 */
	public T post(String json, Class<T> valueType) {
		String result = post(json);
		if (StringUtils.isNoneBlank(result))
			return (T) deserialize(result, valueType);
		return null;
	}

	/**
	 * @param requestObj
	 * @param valueType
	 * @return
	 */
	public T post(Object requestObj, Class<T> valueType) {
		String json = serialize(requestObj);
		if (StringUtils.isBlank(json))
			return null;
		String result = post(json);
		if (StringUtils.isNoneBlank(result))
			return (T) deserialize(result, valueType);
		return null;
	}

}
