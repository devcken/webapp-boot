package io.devcken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외를 처리하기 위한 handler를 구현한다.
 * <p>전역에서 처리해야 하는 예외를 추가하려면 handler 내에 {@link ExceptionHandler} annotation이
 * 적용된 메서드를 추가한다.</p>
 *
 * @author Leejun Choi
 */
@ControllerAdvice
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class GlobalExceptionHandler {
	/**
	 * {@link InvalidRequestException InvalidRequestException}가 발생한 경우
	 * 예외 정보에 대해 RESTful하게 리포트한다. {@link InvalidRequestException}는 request의 유효성 검사와
	 * 관련된 예외이므로 {@link Errors}를 전달하게 되어 있다.
	 * <p>{@link Errors}는 전달하지 못하므로 {@link Errors#getAllErrors()}를 통해 오류 목록을 전달한다.</p>
	 *
	 * @param e {@link InvalidRequestException InvalidRequestException}
	 * @return {@link java.util.Map}
	 */
	@ExceptionHandler(InvalidRequestException.class)
	@ResponseBody
	public Map<String, Object> invalidRequestException(InvalidRequestException e, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();

		Errors errors = e.getErrors();

		if (!errors.hasErrors()) return map;

		map.put("error", true);
		map.put("message", e.getMessage());
		map.put("errorCount", errors.getErrorCount());
		map.put("errors", errors.getAllErrors());

		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		return map;
	}

	/**
	 * <p><code>@RequestParam</code>과 <code>@Valid</code>가 annotated된
	 * http parameter argument에 대한 {@link MissingServletRequestParameterException} 예외 발생 시
	 * 이를 bad request로 간주하고 관련 내용을 {@link ResponseBody}를 이용해 사용자에게 전달한다.</p>
	 * <p>해당 핸들러에 인입되는 경우는 request 요청 시 parameter 요건을 충족시키지 못했기 때문이다.</p>
	 *
	 * @param e {@link MissingServletRequestParameterException MissingServletRequestParameterException}
	 * @param response {@link HttpServletResponse}
	 * @return {@link java.util.Map}
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Map<String, Object> missingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<>();

//		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		map.put("error", true);
		map.put("message", e.getMessage());

		return map;
	}
}
