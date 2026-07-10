package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.arguments.ConfigClass;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns an input string into a list of {@link Token}
 */
public class StringTokeniser extends ConfigClass<StringTokeniser.StringTokeniserConfig> {
    protected StringTokeniser(String input, Tokeniser.TokeniserConfig config) {
        super(new StringTokeniserConfig() {
            @Override
            public boolean listTokens() {
                return config.listTokens();
            }

            @Override
            public String input() {
                return input;
            }

            @Override
            public boolean verbose() {
                return config.verbose();
            }

            @Override
            public boolean debug() {
                return config.debug();
            }

            @Override
            public boolean anyVerbose() {
                return config.anyVerbose();
            }
        });
    }

    public List<Token> tokenise(){
        setVerboseCondition(config::listTokens);

        printlnVerbose(config.input());

        List<Token> tokens = new ArrayList<>();

        //todo read input into tokens

        setDefaultVerbose();

        return tokens;
    }


    protected interface StringTokeniserConfig extends BaseConfig {
        boolean listTokens();

        String input();
    }
}
