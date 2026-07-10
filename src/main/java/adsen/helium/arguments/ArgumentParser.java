package adsen.helium.arguments;


import adsen.helium.utils.TriConsumer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Argument parsing util that is parametric to allow multiple different configs.
 * Originally written for a different project by Enrico Sintoni, with later modifications by me
 * todo see if i can add project link here
 *
 * @param <T> the type of the config passed to the option handlers
 * @author Enrico Sintoni (<a href="https://github.com/Sintuz">Sintuz</a>)
 */
public abstract class ArgumentParser<T extends BaseConfig> {
    private final Map<String, OptionHandler<T>> handlers = new HashMap<>();
    private final Supplier<T> newInstanceSupplier;

    /**
     * Constructor.
     *
     * @param newInstanceSupplier the supplier for a new instance of the config
     */
    protected ArgumentParser(Supplier<T> newInstanceSupplier) {
        this.newInstanceSupplier = newInstanceSupplier;
    }

    /**
     * Determines the name of the jar that has been run.
     * todo figure out how this method works with builds, maybe modify/remove it
     *
     * @return the name or a default one if it couldn't be determined
     */
    public static String getJarName() {
        try {
            String path = ArgumentParser.class.getProtectionDomain().getCodeSource().getLocation().getPath();

            File file = new File(path);
            return file.getName();
        } catch (Exception e) {
            return "client.jar";
        }
    }

    /**
     * Handles a string argument
     *
     * @param name   the name of the option (for summary)
     * @param action the callback modifying the config with the successfully parsed string value
     * @return the handler which reads this parameter
     */
    public OptionHandler<T> stringFlagConsumer(String name, BiConsumer<T, String> action) {
        return (args, i, config, errors) -> {
            if (i + 1 >= args.length) {
                errors.add(name + " required after flag");
                return 0;
            }
            action.accept(config, args[i + 1]);
            return 1;
        };
    }

    /**
     * Custom consumer that just has a flag, whose presence will typically set a boolean config parameter to its nondefault value.
     * Hence, why it consumes no extra args
     */
    public OptionHandler<T> flagConsumer(Consumer<T> action) {
        return (_, _, config, _) -> {
            action.accept(config);
            return 0;
        };
    }


    /**
     * Requires a single additional integer after the current flag.
     *
     * @param name   the name of the option (for summary)
     * @param action the callback with the successfully parsed integer value
     * @return the handler which reads this parameter
     */
    public OptionHandler<T> intFlagConsumer(String name, BiConsumer<T, Integer> action) {
        return (args, i, config, errors) -> {
            if (i + 1 >= args.length) {
                errors.add(name + " required after flag");
                return 0;
            }

            try {
                action.accept(config, Integer.parseInt(args[i + 1]));
            } catch (NumberFormatException _) {
                errors.add(name + " must be an integer");
                return 0;
            }

            return 1;
        };
    }


    /**
     * Consumes a single optional additional integer after the current flag.
     *
     * @param action the callback with the successfully parsed integer value
     * @return the handler which reads this parameter
     */
    public OptionHandler<T> optionalIntFlagConsumer(BiConsumer<T, Integer> action) {
        return (args, i, config, _) -> {
            if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                try {
                    action.accept(config, Integer.parseInt(args[i + 1]));
                    return 1;
                } catch (NumberFormatException _) {
                }
            }
            action.accept(config, null);
            return 0;
        };
    }

    /**
     * Accepts an optional string and an optional integer value after the current flag.
     *
     * @param action the callback with the successfully parsed value/values
     * @return the handler which reads these parameters
     */
    public OptionHandler<T> intAndOptionalStringFlagConsumer(TriConsumer<T, Integer, String> action) {
        return (args, i, config, _) -> {
            Integer port = null;
            String clientAddress = null;
            int consumed = 0;

            if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                try {
                    port = Integer.parseInt(args[i + 1]);
                    consumed++;

                    if (i + 2 < args.length && !args[i + 2].startsWith("-")) {
                        clientAddress = args[i + 2];
                        consumed++;
                    }
                } catch (NumberFormatException _) {
                    clientAddress = args[i + 1];
                    consumed++;
                }
            }

            action.accept(config, port, clientAddress);
            return consumed;
        };
    }


    /**
     * Register the given handler for one or more flags.
     *
     * @param handler the handler to register
     * @param flags   one of more flags for which to register the handler
     */
    public void registerHandler(OptionHandler<T> handler, String... flags) {
        for (String flag : flags) {
            handlers.put(flag, handler);
        }
    }

    /**
     * Main parsing function, accepts the command line arguments and returns what has been parsed.
     *
     * @param args the argument list
     * @return the parsing result with all the necessary values
     */
    public ParseResult<T> parse(String[] args) {
        T config = newInstanceSupplier.get();
        List<String> errors = new ArrayList<>();
        boolean helpRequested = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-h") || arg.equals("--help")) {
                helpRequested = true;
            } else if (handlers.containsKey(arg)) {
                i += handlers.get(arg).handle(args, i, config, errors);
            } else {
                errors.add("Unknown argument: " + arg);
            }
        }

        if (!helpRequested) {
            validateValues(config, errors);
        }

        return new ParseResult<>(this, config, errors, helpRequested);
    }

    /**
     * Validates the final config after reading all flags
     *
     * @param config the read config to validate
     * @param errors the error list
     */
    protected abstract void validateValues(T config, List<String> errors);

    /**
     * Obtains a user-friendly usage for the given config.
     *
     * @return the usage of the config
     */
    protected abstract String getUsage();

    /**
     * Obtains a user-friendly summary given the parsed config.
     *
     * @param config the config for which to give the summary
     * @return the formatted summary
     */
    protected abstract String getSummary(T config);

    /**
     * Interface to handle one or more flags.
     *
     * @param <T> the type of the config
     */
    @FunctionalInterface
    public interface OptionHandler<T extends BaseConfig> {
        /**
         * Handles an instance of one of the flags for which this is registered.
         *
         * @param args   the complete list of arguments
         * @param index  the current flag index in the argument list
         * @param config the current config instance
         * @param errors the error list
         * @return the amount of additional fields consumed
         */
        int handle(String[] args, int index, T config, List<String> errors);
    }

    /**
     * Contains all the necessary values for completely parsed arguments
     *
     * @param parser        the parser instance
     * @param config        the read config
     * @param errors        the error list
     * @param helpRequested whether the user requested the help
     * @param <T>           the type of the config
     */
    public record ParseResult<T extends BaseConfig>(ArgumentParser<T> parser, T config, List<String> errors,
                                                    boolean helpRequested) {
        /**
         * Automatically determines if the parsing of the arguments resulted in errors.
         *
         * @return whether the result contains errors
         */
        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        /**
         * Outputs to the console in the correct streams based on the parsing result.
         *
         * @return an eventual exit code in case of an incorrect input
         */
        public Optional<Integer> showSummary() {
            if (helpRequested()) {
                System.out.println(parser.getUsage());
                return Optional.of(0);
            } else if (hasErrors()) {
                System.err.println("Configuration Errors:");
                errors().forEach(err -> System.err.println("- " + err));
                System.err.println(parser.getUsage());
                return Optional.of(1);
            } else {
                if (config.anyVerbose()) System.out.println(parser.getSummary(config));
                return Optional.empty();
            }
        }
    }
}