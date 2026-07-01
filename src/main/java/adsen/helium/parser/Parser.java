package adsen.helium.parser;

import adsen.helium.arguments.BaseConfig;

public class Parser {
    private final ParserConfig config;

    public Parser(ParserConfig config) {
        this.config = config;
    }

    public interface ParserConfig extends BaseConfig {
        boolean printStatements();
        boolean printParserStackMovements();
    }
}
