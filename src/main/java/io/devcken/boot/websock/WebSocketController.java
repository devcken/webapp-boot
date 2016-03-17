package io.devcken.boot.websock;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WebSocketController {
	@RequestMapping("/websocket")
	public String testPage() {
		return "websocket";
	}

	@MessageMapping("/calc")
	@SendTo("/sum/result")
	public Map<String, Integer> sum(Map<String, String> inputs) throws Exception {
		Map<String, Integer> result = new HashMap<>();

		if (!inputs.containsKey("lhs") || !inputs.containsKey("rhs")) {
			return result;
		}

		Integer lhs = Integer.parseInt(inputs.get("lhs"));
		Integer rhs = Integer.parseInt(inputs.get("rhs"));

		result.put("lhs", lhs);
		result.put("rhs", rhs);
		result.put("result", lhs + rhs);

		return result;
	}
}
