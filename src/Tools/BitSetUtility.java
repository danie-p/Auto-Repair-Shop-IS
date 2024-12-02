package Tools;

import java.util.BitSet;

public class BitSetUtility {
    public static BitSet intToBitSet(int intToConvert) {
        int totalBits = Constants.integerBits;
        BitSet bitSet = new BitSet(totalBits);

        // kazdy int ma 32 bitov
        for (int i = 0; i < totalBits; i++) {
            if ((intToConvert & (1 << i)) != 0) {
                bitSet.set(i);
            }
        }

        return bitSet;
    }

    public static int bitSetToInt(BitSet bitSetToConvert, int totalBits) {
        int intResult = 0;

        for (int i = 0; i < totalBits; i++) {
            // ak ma bit v bitsete na indexe i nastavenu hodnotu 1
            if (bitSetToConvert.get(i))
                // pricitaj 2^i do vysledneho celeho cisla
                intResult += (1 << (totalBits - i - 1));
        }

        return intResult;
    }

    public static BitSet strToBitSet(String strToConvert) {
        int totalBits = Constants.integerBits;
        BitSet bitSet = new BitSet(totalBits);

        // TODO: upravit pre variabilne dlzky strToConvert

        // vezmi posledne 4 znaky zo stringu
        strToConvert = strToConvert.substring(strToConvert.length() - 4);

        int bitIndex = 0;
        for (char c : strToConvert.toCharArray()) {
            // kazdy znak ma 8 bitov
            for (int i = 0; i < 8; i++) {
                // ziskaj zo znaku jeho ASCII int reprezentaciu
                if (((int) c & (1 << i)) != 0) {
                    bitSet.set(bitIndex);
                }
                bitIndex++;
            }
        }

        return bitSet;
    }
}
