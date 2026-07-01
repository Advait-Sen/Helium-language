package adsen.helium;

import adsen.helium.arguments.HeliumConfig;
import adsen.helium.parser.Parser;
import adsen.helium.tokeniser.Tokeniser;

public class Helium {
    static HeliumConfig config;

    static void main(String[] args) {
        config = HeliumConfig.parse(args);

        printlnVerbose();
        printlnVerbose("Starting Helium language server");

        printlnVerbose();
        printlnVerbose("Setting up tokeniser:");
        Tokeniser tokeniser = new Tokeniser(config);

        // tokeniser.tokenise()

        if (config.parse()) {
            printlnVerbose();
            printlnVerbose("Setting up parser:");
            Parser parser = new Parser(config);

            // parser.parse()
        }
    }

    public static void printlnVerbose(Object... args) {
        if (config.verbose()) {
            for (Object arg : args) {
                System.out.print(arg);
            }
            System.out.println();
        }
    }

    public static void printVerbose(Object... args) {
        if (config.verbose()) {
            for (Object arg : args) {
                System.out.print(arg);
            }
        }
    }
}
