package integrity;

public interface IIntegrity {

    /**
     * @param first: the bytes of the first object
     * @param second: the bytes of the second object
     * @return true if those entities are the same or false otherwise
     */
    boolean isSame(final Byte[] first, final Byte[] second);

    /**
     *
     * @param object: the object that is evaluated
     * @param expectedHash: the value of hash that is expected
     * @return true if the value of hash is equal with the @param expectedHash
     */
    default boolean hasHash(final Byte[] object, final String expectedHash) {
        return getHashBlock(object).equals(expectedHash);
    }

    /**
     * Get the hash value on block of bytes
     * @param object: the object that will be hashed
     * @return a string value representing the hash value for the object
     */
    String getHashBlock(final Byte[] object);

    /**
     * Get the hash value of entire object
     * @param object: the object that is computed
     * @return a string that represents the hash value
     */
    String getHash(final Byte[] object);
}
