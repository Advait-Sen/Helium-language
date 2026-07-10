package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.arguments.ConfigClass;
import adsen.helium.utils.FileLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokeniser extends ConfigClass<Tokeniser.TokeniserConfig> {
    private final Map<String, List<Token>> tokenMap = new HashMap<>();

    public Tokeniser(TokeniserConfig config) {
        super(config);
    }

    public void tokenise() {
        if (!tokenMap.isEmpty()) return;

        FileLoader loader = new FileLoader(config.inputFile());
        loader.readInput();
        readFileContentsIntoTokenList(loader);
        postProcessTokenList(loader.fileName());
    }

    /**
     * Reads the contents of a file into a list of tokens and adds it to {@link #tokenMap}
     */
    private void readFileContentsIntoTokenList(FileLoader loader) {
        setVerboseCondition(config::listTokens);

        String input = loader.getFileContents();
        printlnVerbose(loader.fileName() + ":");
        printlnVerbose(input);

        List<Token> tokens = new ArrayList<>();

        //todo read input into tokens


        tokenMap.put(loader.fileName(), tokens);

        setDefaultVerbose();
    }

    /**
     * Additional optimisations to tokenisation once we have a list of valid tokens
     */
    private void postProcessTokenList(String fileName) {
        List<Token> tokens = tokenMap.get(fileName);
        // todo post process tokens once they're all read
    }

    public interface TokeniserConfig extends BaseConfig {
        boolean listTokens();

        String inputFile();
    }
}
