package actions.custom;

import actions.Action;
import actions.CommandAction;
import llm.LLMEngine;
import util.Logger;

import java.util.Map;

@CommandAction(command = "chatting_with_ai")
public class ChattingWithAIAction implements Action {
    private static final String PROMPT = "%s"; // a stub for the prompt for now

    @Override
    public void execute(Map<String, String> params) {
        String input = params.get("input");
        if (input != null) {
            String response = LLMEngine.chat(String.format(PROMPT, input));
            Logger.info(response);
        }
    }
}
