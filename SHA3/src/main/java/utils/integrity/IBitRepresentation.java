package utils.integrity;

@FunctionalInterface
public interface IBitRepresentation <T> {
    /**
     * Get the objects bytes
     * @param object: the object that will be decomposed in bytes
     * @return a list of bytes
     */
    byte[] getBytes(final T object);
}
