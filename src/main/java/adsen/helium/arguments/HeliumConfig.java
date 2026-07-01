package adsen.helium.arguments;

import adsen.helium.parser.Parser;
import adsen.helium.tokeniser.Tokeniser;
import java.util.ArrayList;
import java.util.List;

public class HeliumConfig implements BaseConfig, Tokeniser.TokeniserConfig, Parser.ParserConfig {
    //todo add version to config
    public static final boolean DEFAULT_PARSE = true;
    public static final boolean DEFAULT_VERBOSE = false;
    public static final boolean DEFAULT_DEBUG = false;

    public static final boolean DEFAULT_TOKENISER_VERBOSE = false;
    public static final boolean DEFAULT_PARSER_VERBOSE = false;
    public static final String DEFAULT_INPUT_FILENAME = "main.he";

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

    @Override
    public boolean anyVerbose() {
        return listTokens || printParserStackMovements || printStatements;
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
            //Should add new verbose flags here to be set true
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
        return """
                Usage: java -jar %s [options]
                
                Options:
                  -m, --main <helium_file>     Main code file name (Default: %s)
                  -v, --verbose                Enables verbose output
                  --debug                      Starts with debug mode
                  -np, --no-parse              Disables parsing
                  --list-tokens                Lists all tokens found
                  --print-statements           Prints all parsed statements
                  --print-parser-stack         Prints movements in parser stack
                """.formatted(getJarName(), HeliumConfig.DEFAULT_INPUT_FILENAME);
    }

    @Override
    protected String getSummary(HeliumConfig config) {
        List<String> lines = new ArrayList<>();
        if (config.verbose()) {
            lines.add("Starting verbose mode");
            lines.add("");
        }
        lines.add("Helium language server config:");
        lines.add(" - Main file: %s".formatted(config.inputFile()));
        lines.add(" - Debug mode: %s".formatted(config.debug()));
        lines.add(" - Parsing: %s".formatted(config.parse() ? "enabled" : "disabled"));

        if (config.anyVerbose()) {
            lines.add("");
            lines.add("Extra verbose info:");
            if (config.listTokens()) lines.add(" - Listing Tokens");
            if (config.printStatements()) lines.add(" - Printing Statements");
            if (config.printParserStackMovements()) lines.add(" - Printing Parser Stack");
        }

        return String.join("\n", lines);
    }
}