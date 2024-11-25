package Tools;

import java.util.BitSet;

public class BitSetUtility {
    public static BitSet intToBitSet(int intToConvert) {
        BitSet bitSet = new BitSet();
        int index = 0;

        while (intToConvert != 0) {
            if (intToConvert % 2 != 0) {
                bitSet.set(index);
            }
            ++index;
            intToConvert = intToConvert >>> 1;
        }

        return bitSet;
    }

    public static int bitSetToInt(BitSet bitSetToConvert) {
        int intResult = 0;

        for (int i = 0; i < bitSetToConvert.length(); i++) {
            // ak ma bit v bitsete na indexe i nastavenu hodnotu 1
            if (bitSetToConvert.get(i))
                // pricitaj 2^i do vysledneho celeho cisla
                intResult += (1 << i);
        }

        return intResult;
    }
}
