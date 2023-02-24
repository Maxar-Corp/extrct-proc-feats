/*
 * Copyright 2021 Maxar Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SBIR DATA RIGHTS
 * Contract No. HM0476-16-C-0022
 * Contractor Name: Maxar Technologies
 * Contractor Address: 2325 Dulles Corner Blvd. STE 1000, Herndon VA 20171
 * Expiration of SBIR Data Rights Period: 2/13/2029
 *
 * The Government's rights to use, modify, reproduce, release, perform, display,
 * or disclose technical data or computer software marked with this legend are
 * restricted during the period shown as provided in paragraph (b)(4) of the
 * Rights in Noncommercial Technical Data and Computer Software-Small Business
 * Innovation Research (SBIR) Program clause contained in the above identified
 * contract. No restrictions apply after the expiration date shown above. Any
 * reproduction of technical data, computer software, or portions thereof marked
 * with this legend must also reproduce the markings.
 */


import java.math.BigInteger;
import java.util.BitSet;

/**
 * Converter for moving between BitSets, binary strings, base 36, and base 10 numbers
 *
 * @author mstabile
 *
 */
public class BinaryConverter {

    public static String toBinary(BitSet bitSet) {
        StringBuilder binaryString = new StringBuilder();
        int[] setBits = getSetBits(bitSet);
        if (setBits.length == 0) {
            return "";
        }
        int setIndex = 0;
        for (int i = setBits[setIndex]; i >= 0; i--) {
            if (setIndex < setBits.length && setBits[setIndex] == i) {
                binaryString.append("1");
                setIndex++;
            } else {
                binaryString.append("0");
            }
        }
        return binaryString.toString();
    }

    public static String toBase36(Long base10long) {
        return Long.toString(base10long, 36);
    }

    public static String toBase36(String binaryString) {
        return new BigInteger(binaryString, 2).toString(36);
    }

    public static String toBase36(BitSet bitSet) {
        return toBase36(toBinary(bitSet));
    }

    public static BitSet toBitSet(BigInteger base10bigInt) {
        int length = base10bigInt.bitLength();
        BitSet bitSet = new BitSet(length);
        for (int i = 0; i < length; i++) {
            bitSet.set(i, base10bigInt.testBit(i));
        }
        return bitSet;
    }

    public static BitSet toBitSetFromBase36(String base36String) {
        if (base36String == null || base36String.isEmpty()) {
            return new BitSet();
        }
        return toBitSet(new BigInteger(base36String, 36));
    }

    /**
     * Return and array of indices of set bits in reverse order
     *
     * @param bitSet
     * @return
     */
    public static int[] getSetBits(BitSet bitSet) {
        if(bitSet == null) {
            return new int[0];
        }

        int[] setBits = new int[bitSet.cardinality()];
        int setIndex = setBits.length - 1;
        for (int i = bitSet.nextSetBit(0); i != -1; i = bitSet.nextSetBit(i + 1)) {
            setBits[setIndex] = i;
            setIndex--;
        }
        return setBits;
    }
}
