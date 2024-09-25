package com.cardanoj.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StringUtilsTest {
    StringUtilsTest() {
    }

    @Test
    void checkLengthTest() {
        String str = "a61bf710c72e671fae4ba01b0d205105e6e7bacf504ebc4ea3b43bb0cc76bb326f17a30d8f1b12c2c4e58b6778f6a26430783065463bdefda922656830783134666638643bb6597a178e6a18971b6827b4dcb50c5c0b71726365486c5578586c576d5a4a637859641b64f4d10bda83efe33bcd995b2806a1d9971b12127f810d7dcee28264554a42333be153691687de9f67";
        String[] splittedString = StringUtils.splitStringEveryNCharacters(str, 1);
        Assertions.assertEquals(str.length(), splittedString.length);
        splittedString = StringUtils.splitStringEveryNCharacters(str, str.length() / 2);
        Assertions.assertEquals(2, splittedString.length);
        String part1 = str.substring(0, str.length() / 2);
        String part2 = str.substring(str.length() / 2);
        Assertions.assertEquals(part1, splittedString[0]);
        Assertions.assertEquals(part2, splittedString[1]);
    }
}
