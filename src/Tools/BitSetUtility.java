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

        // variabilne dlzky strToConvert

        if (strToConvert.length() < 4) {
            StringBuilder sb = new StringBuilder(strToConvert);
            while (sb.length() < 4) {
                sb.append(" ");
            }
            strToConvert = sb.toString();
        }

        // vezmi posledne 4 znaky zo stringu
        strToConvert = strToConvert.substring(strToConvert.length() - 4);

        int bitIndex = 0;
        char[] strAsCharArr = strToConvert.toCharArray();

        // prechadzaj v opacnom poradi (vacsia variabilita pre generovane kluce)
        for (int i = strAsCharArr.length - 1; i >= 0; i--) {
            // kazdy znak ma 8 bitov
            for (int j = 0; j < 8; j++) {
                // ziskaj zo znaku jeho ASCII int reprezentaciu
                if (((int) strAsCharArr[i] & (1 << j)) != 0) {
                    bitSet.set(bitIndex);
                }
                bitIndex++;
            }
        }

        /*
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
         */

        return bitSet;
    }
}
