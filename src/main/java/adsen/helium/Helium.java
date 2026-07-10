package adsen.helium;

import adsen.helium.arguments.ConfigClass;
import adsen.helium.arguments.HeliumConfig;
import adsen.helium.parser.Parser;
import adsen.helium.tokeniser.Tokeniser;

public class Helium extends ConfigClass<HeliumConfig> {

    protected Helium(HeliumConfig config) {
        super(config);
    }

    static void main(String[] args) {
        HeliumConfig config = HeliumConfig.parse(args);

        Helium application = new Helium(config);
        application.run();
    }

    private void run() {

        printlnVerbose();
        printlnVerbose("Starting Helium language server");

        printlnVerbose();
        printlnVerbose("Setting up tokeniser:");
        Tokeniser tokeniser = new Tokeniser(config);

        tokeniser.tokenise();

        // tokeniser.tokenise()

        if (config.parse()) {
            printlnVerbose();
            printlnVerbose("Setting up parser:");
            Parser parser = new Parser(config);

            // parser.parse()
        }
    }
}
