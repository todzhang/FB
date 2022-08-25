package ddb.util;

import java.text.DecimalFormat;

public class ByteUtils {
   public static final String classVersion = "2.2";
   public static final int ASCII = 1;
   public static final int BINARY = 2;
   public static final int DECIMAL = 10;
   public static final int HEX = 16;
   protected static final DecimalFormat DDD = new DecimalFormat("000");
   protected static final DecimalFormat DDDDD = new DecimalFormat("00000");
   protected static final String NEWLINE = System.getProperty("line.separator");
   private static final String[] nibbleString = new String[]{"0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"};
   protected static final String[] hexDigit = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
   protected static final byte[] reverseBits = new byte[]{0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15};

   public static void appendBinaryString(byte value, StringBuffer buffer) {
      byte a = (byte)(value >> 4 & 15);
      byte b = (byte)(value & 15);
      buffer.append(nibbleString[a]);
      buffer.append(nibbleString[b]);
   }

   public static void appendDecimalString(byte value, StringBuffer buffer) {
      buffer.append(DDD.format((long)(value & 255)));
   }

   protected static void appendEmptyValue(int radix, StringBuffer buffer) {
      switch(radix) {
      case 1:
         buffer.append(' ');
         break;
      case 2:
         buffer.append("        ");
         break;
      case 10:
         buffer.append("   ");
         break;
      case 16:
      default:
         buffer.append("  ");
      }

   }

   public static void appendHexString(byte value, StringBuffer buffer) {
      byte a = (byte)(value >> 4 & 15);
      byte b = (byte)(value & 15);
      buffer.append(hexDigit[a]);
      buffer.append(hexDigit[b]);
   }

   public static void appendValue(byte value, int radix, StringBuffer buffer) {
      switch(radix) {
      case 1:
         buffer.append(value >= 32 && value <= 126 ? (char)value : ' ');
         break;
      case 2:
         appendBinaryString(value, buffer);
         break;
      case 10:
         appendDecimalString(value, buffer);
         break;
      case 16:
      default:
         appendHexString(value, buffer);
      }

   }

   public static byte bitReverse(byte value) {
      byte a = reverseBits[(byte)((value & 240) >> 4)];
      byte b = reverseBits[value & 15];
      return (byte)(b << 4 ^ a);
   }

   public static short bitReverse(short value) {
      byte a = bitReverse((byte)(value >> 8 & 255));
      byte b = bitReverse((byte)(value & 255));
      return (short)(a << 8 ^ b & 255);
   }

   public static int bitReverse(int value) {
      short a = bitReverse((short)(value >> 16 & '\uffff'));
      short b = bitReverse((short)(value & '\uffff'));
      return a << 16 ^ b & '\uffff';
   }

   public static long bitReverse(long value) {
      int a = bitReverse((int)(value >> 32 & 4294967295L));
      int b = bitReverse((int)(value & 4294967295L));
      return (long)a << 32 ^ (long)b & 4294967295L;
   }

   public static short byteReverse(short value) {
      int a = value >> 8 & 255;
      int b = (value & 255) << 8;
      return (short)(b ^ a);
   }

   public static int byteReverse(int value) {
      int a = value >> 24 & 255;
      int b = value >> 8 & '\uff00';
      int c = value << 8 & 16711680;
      int d = value << 24 & -16777216;
      return d ^ c ^ b ^ a;
   }

   public static long byteReverse(long value) {
      int a = byteReverse((int)(value >> 32 & 4294967295L));
      int b = byteReverse((int)(value & 4294967295L));
      return (long)b << 32 ^ (long)a & 4294967295L;
   }

   public static final String dump(byte[] b, int radix, int perLine, int offset, int length) {
      if (b != null && b.length != 0 && length != 0) {
         if (offset < 0) {
            throw new IllegalArgumentException("Offset cannot be negative: " + offset);
         } else if (length < 0) {
            throw new IllegalArgumentException("Length cannot be negative: " + length);
         } else if (perLine < 1) {
            throw new IllegalArgumentException("Per-line must be positive: " + perLine);
         } else {
            int start = offset - offset % perLine;
            int end = offset + length;
            StringBuffer buffer = new StringBuffer();
            StringBuffer line = new StringBuffer();
            StringBuffer ascii = new StringBuffer();
            line.append(DDDDD.format((long)start));
            line.append("  ");
            if (start >= offset) {
               appendValue(b[start], radix, line);
               appendValue(b[start], 1, ascii);
            } else {
               appendEmptyValue(radix, line);
               appendEmptyValue(1, ascii);
            }

            int i;
            for(i = start + 1; i < start + perLine; ++i) {
               line.append(' ');
               if (i >= offset && i < end) {
                  appendValue(b[i], radix, line);
                  appendValue(b[i], 1, ascii);
               } else {
                  appendEmptyValue(radix, line);
                  appendEmptyValue(1, ascii);
               }
            }

            line.append("  ");
            line.append(ascii.toString());
            buffer.append(line.toString().trim());
            ascii.setLength(0);
            line.setLength(0);

            for(i = start + perLine; i < end; i += perLine) {
               buffer.append(NEWLINE);
               line.append(DDDDD.format((long)i));
               line.append("  ");
               if (i < end) {
                  appendValue(b[i], radix, line);
                  appendValue(b[i], 1, ascii);
               } else {
                  appendEmptyValue(radix, line);
                  appendEmptyValue(1, ascii);
               }

               for(int j = i + 1; j < i + perLine; ++j) {
                  line.append(' ');
                  if (j < end) {
                     appendValue(b[j], radix, line);
                     appendValue(b[j], 1, ascii);
                  } else {
                     appendEmptyValue(radix, line);
                     appendEmptyValue(1, ascii);
                  }
               }

               line.append("  ");
               line.append(ascii.toString());
               buffer.append(line.toString().trim());
               ascii.setLength(0);
               line.setLength(0);
            }

            return buffer.toString();
         }
      } else {
         return "";
      }
   }

   public static final String dumpBinary(byte[] b) {
      return dump(b, 2, 4, 0, b.length);
   }

   public static final String dumpBinary(byte[] b, int length) {
      return dump(b, 2, 4, 0, length);
   }

   public static final String dumpBinary(byte[] b, int offset, int length) {
      return dump(b, 2, 4, offset, length);
   }

   public static final String dumpDecimal(byte[] b) {
      return dump(b, 10, 10, 0, b.length);
   }

   public static final String dumpDecimal(byte[] b, int length) {
      return dump(b, 10, 10, 0, length);
   }

   public static final String dumpDecimal(byte[] b, int offset, int length) {
      return dump(b, 10, 10, offset, length);
   }

   public static final String dumpHex(byte[] b) {
      return dump(b, 16, 16, 0, b.length);
   }

   public static final String dumpHex(byte[] b, int length) {
      return dump(b, 16, 16, 0, length);
   }

   public static final String dumpHex(byte[] b, int offset, int length) {
      return dump(b, 16, 16, offset, length);
   }

   public static String padOrTruncate(String value, int digits) {
      int length = value.length();
      if (digits <= length) {
         return digits < length ? value.substring(length - digits) : value;
      } else {
         StringBuffer buffer = new StringBuffer(value);

         while(buffer.length() < digits) {
            buffer.insert(0, '0');
         }

         return buffer.toString();
      }
   }

   public static String toBinaryString(byte value) {
      return toString(value, 2);
   }

   public static String toBinaryString(byte value, int digits) {
      return toString((byte)value, 2, digits);
   }

   public static String toBinaryString(short value) {
      return toString((short)value, 2, " ");
   }

   public static String toBinaryString(short value, int digits) {
      return toString((short)value, 2, digits);
   }

   public static String toBinaryString(short value, String delimiter) {
      return toString((short)value, 2, delimiter);
   }

   public static String toBinaryString(int value) {
      return toString((int)value, 2, " ");
   }

   public static String toBinaryString(int value, int digits) {
      return toString((int)value, 2, digits);
   }

   public static String toBinaryString(int value, String delimiter) {
      return toString((int)value, 2, delimiter);
   }

   public static String toBinaryString(long value) {
      return toString(value, 2, " ");
   }

   public static String toBinaryString(long value, int digits) {
      return toString(value, 2, digits);
   }

   public static String toBinaryString(long value, String delimiter) {
      return toString(value, 2, delimiter);
   }

   public static String toBinaryString(byte[] b) {
      return toString(b, 2, " ", 0, b.length);
   }

   public static String toBinaryString(byte[] b, String delimiter) {
      return toString(b, 2, delimiter, 0, b.length);
   }

   public static String toBinaryString(byte[] b, int offset, int length) {
      return toString(b, 2, " ", offset, length);
   }

   public static String toBinaryString(byte[] b, String delimiter, int offset, int length) {
      return toString(b, 2, delimiter, offset, length);
   }

   public static String toHexString(byte value) {
      return toString(value, 16);
   }

   public static String toHexString(byte value, int digits) {
      return toString((byte)value, 16, digits);
   }

   public static String toHexString(short value, int digits) {
      return toString((short)value, 16, digits);
   }

   public static String toHexString(short value) {
      return toString((short)value, 16, " ");
   }

   public static String toHexString(short value, String delimiter) {
      return toString((short)value, 16, delimiter);
   }

   public static String toHexString(int value, int digits) {
      return toString((int)value, 16, digits);
   }

   public static String toHexString(int value) {
      return toString((int)value, 16, " ");
   }

   public static String toHexString(int value, String delimiter) {
      return toString((int)value, 16, delimiter);
   }

   public static String toHexString(long value, int digits) {
      return toString(value, 16, digits);
   }

   public static String toHexString(long value) {
      return toString(value, 16, " ");
   }

   public static String toHexString(long value, String delimiter) {
      return toString(value, 16, delimiter);
   }

   public static String toHexString(byte[] b) {
      return toString(b, 16, " ", 0, b.length);
   }

   public static String toHexString(byte[] b, String delimiter) {
      return toString(b, 16, delimiter, 0, b.length);
   }

   public static String toHexString(byte[] b, int offset, int length) {
      return toString(b, 16, " ", offset, length);
   }

   public static String toHexString(byte[] b, String delimiter, int offset, int length) {
      return toString(b, 16, delimiter, offset, length);
   }

   public static String toString(byte value, int radix) {
      StringBuffer buffer = new StringBuffer();
      appendValue(value, radix, buffer);
      return buffer.toString();
   }

   public static String toString(byte value, int radix, int digits) {
      return padOrTruncate(toString((short)value, radix, ""), digits);
   }

   public static String toString(short value, int radix, int digits) {
      return padOrTruncate(toString(value, radix, ""), digits);
   }

   public static String toString(short value, int radix, String delimiter) {
      StringBuffer buffer = new StringBuffer();
      appendValue((byte)(value >> 8 & 255), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)(value & 255), radix, buffer);
      return buffer.toString();
   }

   public static String toString(int value, int radix, int digits) {
      return padOrTruncate(toString(value, radix, ""), digits);
   }

   public static String toString(int value, int radix, String delimiter) {
      StringBuffer buffer = new StringBuffer();
      appendValue((byte)(value >> 24 & 255), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)(value >> 16 & 255), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)(value >> 8 & 255), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)(value & 255), radix, buffer);
      return buffer.toString();
   }

   public static String toString(long value, int radix, int digits) {
      return padOrTruncate(toString(value, radix, ""), digits);
   }

   public static String toString(long value, int radix, String delimiter) {
      StringBuffer buffer = new StringBuffer();
      appendValue((byte)((int)(value >> 56 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 48 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 40 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 32 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 24 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 16 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value >> 8 & 255L)), radix, buffer);
      buffer.append(delimiter);
      appendValue((byte)((int)(value & 255L)), radix, buffer);
      return buffer.toString();
   }

   public static String toString(byte[] b, int radix, String delimiter, int offset, int length) {
      if (offset < 0) {
         throw new IllegalArgumentException("Offset cannot be negative: " + offset);
      } else if (offset >= b.length) {
         return "";
      } else {
         int end = length + offset - 1;
         if (end >= b.length) {
            end = b.length - 1;
         }

         StringBuffer buffer = new StringBuffer();
         appendValue(b[offset], radix, buffer);

         for(int i = offset + 1; i <= end; ++i) {
            buffer.append(delimiter);
            appendValue(b[i], radix, buffer);
         }

         return buffer.toString();
      }
   }

   public static void printUsage() {
   }
}
