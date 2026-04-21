package Intent;

import Actions.Action;
import ParamsExtractor.ParametersExtractor;

public class IntentHandler {
    public ParametersExtractor extractor;
    public Action action;

    public IntentHandler(ParametersExtractor extractor, Action action) {
        this.extractor = extractor;
        this.action = action;
    }
}
