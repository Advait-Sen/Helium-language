package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.utils.FileLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static adsen.helium.Helium.printlnVerbose;
import static adsen.helium.utils.Keywords.DEFAULT_MAIN_NAME;

public class Tokeniser {
    private final TokeniserConfig config;
    private final Map<String, List<Token>> tokenMap = new HashMap<>();

    public Tokeniser(TokeniserConfig config) {
        this.config = config;
    }

    public void tokenise() {
        if (!tokenMap.isEmpty()) return;

        FileLoader loader = new FileLoader(DEFAULT_MAIN_NAME);
        loader.readInput();
        readFileContentsIntoTokenList(loader);
        postProcessTokenList(loader.fileName());
    }

    /**
     * Reads the contents of a file into a list of tokens and adds it to {@link #tokenMap}
     */
    private void readFileContentsIntoTokenList(FileLoader loader) {
        String input = loader.getFileContents();
        printlnVerbose(loader.fileName() + ":");
        printlnVerbose(input);

        List<Token> tokens = new ArrayList<>();

        //todo read input into tokens


        tokenMap.put(loader.fileName(), tokens);
    }

    /**
     * Additional optimisations to tokenisation once we have a list of valid tokens
     */
    private void postProcessTokenList(String fileName) {
        // todo post process tokens once they're all read
    }

    public interface TokeniserConfig extends BaseConfig {
        boolean listTokens();

        String inputFile();
    }
}
