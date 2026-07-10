package adsen.helium.tokeniser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.arguments.ConfigClass;
import adsen.helium.utils.FileLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns a starting input file into a list of tokens, plus grabbing any dependencies/imports and getting their tokens too
 */
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
        printlnVerboseConditional(config.listTokens(),loader.fileName() + ":");

        StringTokeniser stringTokeniser = new StringTokeniser(loader.getFileContents(), config);

        List<Token> tokens = stringTokeniser.tokenise();

        tokenMap.put(loader.fileName(), tokens);
    }

    /**
     * Additional optimisations to tokenisation once we have a list of valid tokens
     */
    private void postProcessTokenList(String fileName) {
        List<Token> tokens = tokenMap.get(fileName);
        // todo post process tokens once they're all read
    }

    public interface TokeniserConfig extends BaseConfig {
        //todo see if this should just be kept in StringTokeniser. In that case, TokeniserConfig can extend StringTokeniser or smth
        boolean listTokens();

        String inputFile();
    }
}
