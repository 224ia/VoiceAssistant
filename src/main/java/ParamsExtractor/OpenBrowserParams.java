package ParamsExtractor;

import ConfigParser.ProgramPaths;

import java.util.HashMap;
import java.util.Map;

public class OpenBrowserParams implements ParametersExtractor {
    @Override
    public Map<String, String> extractParameters(String input, String intent)  {
        var params = new HashMap<String, String>();

        params.put("path", ProgramPaths.get("firefox"));

        if (input.contains("едж")) params.put("path", ProgramPaths.get("edge"));
        if (input.contains("файрфокс")) params.put("path", ProgramPaths.get("firefox"));

        return params;
    }
}
