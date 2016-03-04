package io.devcken.boot

import io.devcken.configs.WebServletConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebAppConfiguration
@ContextConfiguration(classes = [WebServletConfig.class])
class ValidationTest extends Specification {
	@Autowired
	private WebApplicationContext applicationContext;

	private MockMvc mockMvc;

	def setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
	}

	def "validation check를 통과하지 못하면 BAD_REQUEST 코드로 응답되어야 한다."() {
		when:
		def response = mockMvc.perform(post("/employee/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content("{}"))

		then:
		response.andExpect(status().isBadRequest())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath('error').value(true))
	}

	def "parameter가 올바르게 전달된 경우 validation check를 통과해 OK 코드로 응답되어야 한다."() {
		when:
		def response = mockMvc.perform(post("/employee/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content("{\"id\": 1, \"name\": \"employee1\"}"))

		then:
		response.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
	}
}
