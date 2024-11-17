package Tools;

import java.nio.charset.StandardCharsets;

public class StringProcessing {
    public static String initStringAttribute(String argument, int maxStringLength) {
        if (argument.length() > maxStringLength)
            return argument.substring(0, maxStringLength);
        return argument;
    }

    public static byte[] stringAttributeToByteArray(String attribute, int maxStringLength, int actualStringLength) {
        // dopln medzery do max dlzky popisu
        attribute = attribute + " ".repeat(Math.max(0, maxStringLength - actualStringLength));
        // premen string na pole bajtov pomocou kodovania
        return attribute.getBytes(StandardCharsets.UTF_8);
    }

    public static String byteArrayToStringAttribute(byte[] byteArray, int actualStringLength) {
        // premen precitane pole bajtov na string pomocou kodovania
        String s = new String(byteArray, StandardCharsets.UTF_8);    // US_ASCII ?
        // skrat string na dlzku validnych znakov
        return s.substring(0, actualStringLength);
    }
}
