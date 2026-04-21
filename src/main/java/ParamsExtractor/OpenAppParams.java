package ParamsExtractor;

import ConfigParser.ProgramPaths;

import java.util.HashMap;
import java.util.Map;

public class OpenAppParams implements ParametersExtractor {
    @Override
    public Map<String, String> extractParameters(String input, String intent) {
        var params = new HashMap<String, String>();

        if (input.contains("телеграм")) params.put("path", ProgramPaths.get("telegram"));
        if (input.contains("дискорд")) params.put("path", ProgramPaths.get("discord"));
        if (input.contains("стим") || input.contains("стім")) params.put("path", ProgramPaths.get("steam"));

        return params;
    }
}
