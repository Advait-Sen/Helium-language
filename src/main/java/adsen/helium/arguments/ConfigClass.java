package adsen.helium.arguments;

import java.util.function.Supplier;

/**
 * Class that can accept a {@link BaseConfig} config
 */
public abstract class ConfigClass<T extends BaseConfig> {
    protected final T config;
    private Supplier<Boolean> conditionSupplier;

    protected ConfigClass(T config) {
        this.config = config;
        conditionSupplier = config::verbose;
    }

    protected void printlnVerboseConditional(boolean condition, Object x) {
        if (config.verbose() && condition) {
            System.out.println(x);
        }
    }

    protected void printlnVerboseConditional(boolean condition) {
        if (config.verbose() && condition) {
            System.out.println();
        }
    }

    protected void printlnVerbose(Object x) {
        if (conditionSupplier.get()) {
            System.out.println(x);
        }
    }

    protected void printlnVerbose() {
        if (conditionSupplier.get()) {
            System.out.println();
        }
    }

    protected void setVerboseCondition(Supplier<Boolean> condition) {
        this.conditionSupplier = condition;
    }

    protected void setDefaultVerbose() {
        conditionSupplier = config::verbose;
    }
}
