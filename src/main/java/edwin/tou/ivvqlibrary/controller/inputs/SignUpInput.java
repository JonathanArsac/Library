package edwin.tou.ivvqlibrary.controller.inputs;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SignUpInput {

    @NotBlank
    private String username;

    @NotNull
    private Boolean libraire;

    public SignUpInput() {}

    public SignUpInput(String username, Boolean libraire) {
        this.username = username;
        this.libraire = libraire;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setLibraire(Boolean libraire) {
        this.libraire = libraire;
    }

    public Boolean isLibraire() {
        return libraire;
    }
}
