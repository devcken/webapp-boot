package io.devcken.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RootController {
	private static Logger logger = LoggerFactory.getLogger(RootController.class);

	@RequestMapping("/")
	public String index() {
		logger.debug("This is index.");

		return "index";
	}
}
