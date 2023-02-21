package edwin.tou.ivvqlibrary.controller.outputs;

import java.util.UUID;

public class SignUpOutput {

    UUID apiKey;

    public SignUpOutput() {}

    private SignUpOutput(UUID apiKey) {
        setApiKey(apiKey);
    }

    public static SignUpOutput of(UUID apiKey) {
        return new SignUpOutput(apiKey);
    }

    public UUID getApiKey() {
        return apiKey;
    }

    public void setApiKey(UUID apiKey) {
        this.apiKey = apiKey;
    }
}
