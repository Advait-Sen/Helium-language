package adsen.helium.arguments;

import java.util.function.Supplier;

/**
 * Class that can accept a {@link BaseConfig} config
 */
public abstract class ConfigClass<T extends BaseConfig> {
    protected final T config;

    protected ConfigClass(T config) {
        this.config = config;
        conditionSupplier = config::verbose;
    }

    public void printlnVerbose(Object x) {
        if (conditionSupplier.get()) {
            System.out.println(x);
        }
    }

    public void printlnVerbose() {
        if (conditionSupplier.get()) {
            System.out.println();
        }
    }

    private Supplier<Boolean> conditionSupplier;

    protected void setVerboseCondition(Supplier<Boolean> condition){
        this.conditionSupplier = condition;
    }

    protected void setDefaultVerbose(){
        conditionSupplier = config::verbose;
    }
}
