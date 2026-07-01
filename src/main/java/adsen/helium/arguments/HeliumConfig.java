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
        result.showSummary().ifPresent(System::exit);

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
        registerHandler(stringFlagConsumer("Main file", (config, f) -> config.inputFileName = f), "-m", "--main");
        registerHandler(flagConsumer(config -> config.parse = false), "-np", "--no-parse");
        registerHandler(flagConsumer(config -> {
            config.verbose = true;
            //Todo set all verbose flags true
            config.listTokens = true;
            config.printStatements = true;
            config.printParserStackMovements = true;
        }), "-v", "--verbose");

        registerHandler(flagConsumer(config -> config.debug = true), "--debug");

        registerHandler(flagConsumer(config -> config.listTokens = true), "--list-tokens");
        registerHandler(flagConsumer(config -> config.printStatements = true), "--print-statements");
        registerHandler(flagConsumer(config -> config.printParserStackMovements = true), "--print-parser-stack");
    }

    @Override
    protected void validateValues(HeliumConfig config, List<String> errors) {
        if (!config.parse()) {
            if (!config.verbose()) { //if the config is verbose then these will always be true, even if they're not supposed to be
                if (config.printStatements()) {
                    errors.add("Cannot print statements with parsing disabled");
                }
                if (config.printParserStackMovements()) {
                    errors.add("Cannot print parser stack with parsing disabled");
                }
            }
        }
    }

    @Override
    protected String getUsage() {
        return "";//Todo usage
    }

    @Override
    protected String getSummary(HeliumConfig config) {
        //todo summary
        return "argument summary:";
    }
}