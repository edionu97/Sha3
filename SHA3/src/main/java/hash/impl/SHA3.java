package hash.impl;

import hash.IHashAlgorithm;
import hash.keccak.IKeccak;

import java.util.List;

public class SHA3 implements IHashAlgorithm<String, List<Integer>> {

    private final IKeccak keccak;
    private final int outBitsNr;

    public SHA3(final IKeccak keccak, final int outBitsNr) {
        this.keccak = keccak;
        this.outBitsNr = outBitsNr;
    }

    @Override
    public String encode(final List<Integer> toEncode) {
        //get first 224 of bits
        final List<Integer> bits = keccak
                .getBits(toEncode)
                .subList(0, outBitsNr);

        //convert the value from bit list to string
        final String str = bits
                .stream()
                .map(Object::toString)
                .reduce("", (k, v) -> k + v);

        //convert bytes
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i < outBitsNr; i += 8){
            //convert from string to int from binary
            final var value = Integer.valueOf(str.substring(i, i + 8), 2);
            //append the hex value
            builder.append(
                    Integer.toHexString(value)
            );
        }

        //get the hash value
        return builder.toString();
    }

}

