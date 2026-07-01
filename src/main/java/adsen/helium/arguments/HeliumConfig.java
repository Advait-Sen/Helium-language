package adsen.helium.arguments;

import adsen.helium.parser.Parser;
import adsen.helium.tokeniser.Tokeniser;
import java.util.List;

public class HeliumConfig implements BaseConfig, Tokeniser.TokeniserConfig, Parser.ParserConfig {
    //todo add version to config
    static final boolean DEFAULT_PARSE = true;
    static final boolean DEFAULT_VERBOSE = false;
    static final boolean DEFAULT_DEBUG = false;

    static final boolean DEFAULT_TOKENISER_VERBOSE = false;
    static final boolean DEFAULT_PARSER_VERBOSE = false;
    static final String DEFAULT_INPUT_FILENAME = "main.he";

    boolean parse = DEFAULT_PARSE;
    boolean verbose = DEFAULT_VERBOSE;
    boolean debug = DEFAULT_VERBOSE;

    boolean printStatements = DEFAULT_PARSER_VERBOSE;
    boolean printParserStackMovements = DEFAULT_PARSER_VERBOSE;
    boolean listTokens = DEFAULT_TOKENISER_VERBOSE;
    String inputFileName = DEFAULT_INPUT_FILENAME;

    public static HeliumConfig parse(String[] args) {
        ArgumentParser.ParseResult<HeliumConfig> result = new ArgParser().parse(args);
        result.showFeedback().ifPresent(System::exit);

        return result.config();
    }

    @Override
    public boolean printStatements() {
        return printStatements;
    }

    @Override
    public boolean printParserStackMovements() {
        return printParserStackMovements;
    }

    @Override
    public boolean listTokens() {
        return listTokens;
    }

    @Override
    public String inputFile() {
        return inputFileName;
    }

    public boolean parse() {
        return parse;
    }

    @Override
    public boolean verbose() {
        return verbose;
    }

    @Override
    public boolean debug() {
        return debug;
    }
}

final class ArgParser extends ArgumentParser<HeliumConfig> {

    /**
     * Constructor.
     */
    ArgParser() {
        super(HeliumConfig::new);
        registerHandler((args, i, config, errors) -> consumeString(args, i, "Main file", errors, f -> config.inputFileName = f), "-m", "--main");
        registerHandler((_, _, config, _) -> consumeFlag(() -> config.parse = false), "-np", "--no-parse");
        registerHandler((_, _, config, _) -> consumeFlag(() -> {
            config.verbose = true;
            //Todo set all verbose flags true
            config.listTokens = true;
            config.printStatements = true;
            config.printParserStackMovements = true;
        }), "-v", "--verbose");

        registerHandler((_, _, config, _) -> consumeFlag(() -> config.debug = true), "--debug");

        registerHandler((_, _, config, _) -> consumeFlag(() -> config.listTokens = true), "--list-tokens");
        registerHandler((_, _, config, _) -> consumeFlag(() -> config.printStatements = true), "--print-statements");
        registerHandler((_, _, config, _) -> consumeFlag(() -> config.printParserStackMovements = true), "--print-parser-stack");
    }

    @Override
    protected void validateValues(HeliumConfig config, List<String> errors) {
        if (!config.parse) {
            if (config.printStatements) {
                errors.add("Cannot print statements with parsing disabled");
            }
            if (config.printParserStackMovements) {
                errors.add("Cannot print parser stack with parsing disabled");
            }
        }
    }

    @Override
    protected String getUsage() {
        return "";//Todo usage
    }

    @Override
    protected String getFeedback(HeliumConfig config) {
        //todo feedback
        return "argument summary:";
    }
}