package io.devcken.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class HttpErrorExceptionHandler {
	@ExceptionHandler(HttpClientErrorException.class)
	@ResponseBody
	public Map<String, Object> httpClientError(HttpClientErrorException e, HttpServletResponse response) {
		response.setStatus(e.getStatusCode().value());

		Map<String, Object> map = new HashMap<>();

		map.put("statusCode", e.getStatusCode().value());
		map.put("statusText", e.getStatusText());

		ObjectMapper mapper = new ObjectMapper();

		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

		try {
			Map<String, Object> responseBody = mapper.readValue(e.getResponseBodyAsString(), typeRef);

			map.put("body", responseBody);
		} catch (IOException error) {
			map.put("handling-error", error.getMessage());
		}

		return map;
	}
}

