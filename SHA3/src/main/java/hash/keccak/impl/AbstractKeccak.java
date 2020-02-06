package hash.keccak.impl;

import com.mifmif.common.regex.Generex;
import hash.keccak.IKeccak;

import java.util.List;

public abstract class AbstractKeccak implements IKeccak {

    private final int rBits;
    private final int cBits;

    public AbstractKeccak(final int rBits, final int cBits) {
        this.rBits = rBits;
        this.cBits = cBits;
    }

    protected int getRBits() {
        return rBits;
    }

    protected int getCBits() {
        return cBits;
    }

    protected int getTotalBitsNumber() {
        return getCBits() + getRBits();
    }

    /**
     * Adds padding to bits
     *
     * @param bits: the list that will be padded
     */
    protected void padBits(final List<Integer> bits) {

        //convert to bit string
        final StringBuilder bitsAsString = new StringBuilder(bits
                .stream()
                .map(Object::toString)
                .reduce("", (k, v) -> k + v)
        );

        bitsAsString.append("01");

        //simulate the shortest append P11
        final int appendSize = bitsAsString.length() + 2;
        //compute the number of zeros that need to be added
        final int numberOfZeros =
                (int) Math.ceil((appendSize + .0) / getRBits()) * getRBits() - appendSize;

        //generate a number of zeros and add them to string
        bitsAsString
                .append(1)
                .append(
                        new Generex(
                                String.format("0{%d}", numberOfZeros)
                        ).getFirstMatch()
                ).append(1);

        bits.clear();

        //add into list
        bitsAsString
                .toString()
                .chars()
                .map(Character::getNumericValue)
                .forEach(bits::add);
    }
}
