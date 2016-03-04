package io.devcken.exception;

import org.springframework.validation.Errors;

/**
 * 올바르지 않은 요청이 들어온 경우 발생하는 {@link RuntimeException RuntimeException}을 정의한다.
 *
 * @author Leejun,CHOI
 */
public class InvalidRequestException extends RuntimeException {
	private Errors errors;

	public InvalidRequestException(String message, Errors errors) {
		super(message);

		this.errors = errors;
	}

	public Errors getErrors() {
		return errors;
	}
}
