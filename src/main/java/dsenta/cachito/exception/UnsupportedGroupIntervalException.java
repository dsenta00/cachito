package dsenta.cachito.exception;

public class UnsupportedGroupIntervalException extends IllegalArgumentException {
    public UnsupportedGroupIntervalException(String interval) {
        super(String.format("Unsupported group interval %s", interval));
    }
}
