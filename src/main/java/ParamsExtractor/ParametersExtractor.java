package ParamsExtractor;

import java.util.Map;

public interface ParametersExtractor {
    Map<String, String> extractParameters(String input, String intent);
}
