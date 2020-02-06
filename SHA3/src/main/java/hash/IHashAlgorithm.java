package hash;

public interface IHashAlgorithm<THashOut, THashIn> {
    /**
     * This method is used to encode a value
     * @param toEncode: the value that will be encoded
     * @return the encoded value of the toEncode
     */
    THashOut encode(final THashIn toEncode);
}
