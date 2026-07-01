package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;

public class Tokeniser {
    private final TokeniserConfig config;

    public Tokeniser(TokeniserConfig config) {
        this.config = config;
    }



    public interface TokeniserConfig extends BaseConfig {
        boolean listTokens();
        String inputFile();
    }
}
