package adsen.helium.parser;

import adsen.helium.arguments.BaseConfig;
import adsen.helium.arguments.ConfigClass;

public class Parser extends ConfigClass<Parser.ParserConfig> {

    public Parser(ParserConfig config) {
        super(config);
    }

    public interface ParserConfig extends BaseConfig {
        boolean printStatements();

        boolean printParserStackMovements();
    }
}
