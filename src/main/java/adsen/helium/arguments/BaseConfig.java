package adsen.helium.arguments;

/**
 * Base class for all configs, which allows to access global application settings from all major components of the application
 */
public interface BaseConfig {
    boolean verbose();
    boolean debug();
    /**
     * If any verbose flags are true, even if not the overall verbose mode
     */
    boolean anyVerbose();
}
