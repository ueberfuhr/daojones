package de.ars.daojones.internal.runtime.test.utilities;

import java.nio.charset.Charset;

/**
 * A Base64 encoder/decoder.
 * 
 * <p>
 * This class is used to encode and decode data in Base64 format as described in
 * RFC 1521.
 * 
 * <p>
 * Project home page: <a
 * href="http://www.source-code.biz/base64coder/java/">www.
 * source-code.biz/base64coder/java</a><br>
 * Author: Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br>
 * Multi-licensed: EPL / LGPL / GPL / AL / BSD.
 */
public abstract class Base64Coder {

  // The line separator string of the operating system.
  private static final String SYSTEM_LINE_SEPARATOR = System.getProperty( "line.separator" );

  // Mapping table from 6-bit nibbles to Base64 characters.
  private static char[] map1 = new char[64];
  static {
    int i = 0;
    for ( char c = 'A'; c <= 'Z'; c++ ) {
      Base64Coder.map1[i++] = c;
    }
    for ( char c = 'a'; c <= 'z'; c++ ) {
      Base64Coder.map1[i++] = c;
    }
    for ( char c = '0'; c <= '9'; c++ ) {
      Base64Coder.map1[i++] = c;
    }
    Base64Coder.map1[i++] = '+';
    Base64Coder.map1[i++] = '/';
  }

  // Mapping table from Base64 characters to 6-bit nibbles.
  private static byte[] map2 = new byte[128];
  static {
    for ( int i = 0; i < Base64Coder.map2.length; i++ ) {
      Base64Coder.map2[i] = -1;
    }
    for ( int i = 0; i < 64; i++ ) {
      Base64Coder.map2[Base64Coder.map1[i]] = ( byte ) i;
    }
  }

  /**
   * Encodes a string into Base64 format. No blanks or line breaks are inserted.
   * 
   * @param s
   *          A String to be encoded.
   * @param charset
   *          the charset
   * @return A String containing the Base64 encoded data.
   */
  public static String encodeString( final String s, final Charset charset ) {
    return new String( Base64Coder.encode( s.getBytes( charset ) ) );
  }

  /**
   * Encodes a byte array into Base 64 format and breaks the output into lines
   * of 76 characters. This method is compatible with
   * <code>sun.misc.BASE64Encoder.encodeBuffer(byte[])</code>.
   * 
   * @param in
   *          An array containing the data bytes to be encoded.
   * @return A String containing the Base64 encoded data, broken into lines.
   */
  public static String encodeLines( final byte[] in ) {
    return Base64Coder.encodeLines( in, 0, in.length, 76, Base64Coder.SYSTEM_LINE_SEPARATOR );
  }

  /**
   * Encodes a byte array into Base 64 format and breaks the output into lines.
   * 
   * @param in
   *          An array containing the data bytes to be encoded.
   * @param iOff
   *          Offset of the first byte in <code>in</code> to be processed.
   * @param iLen
   *          Number of bytes to be processed in <code>in</code>, starting at
   *          <code>iOff</code>.
   * @param lineLen
   *          Line length for the output data. Should be a multiple of 4.
   * @param lineSeparator
   *          The line separator to be used to separate the output lines.
   * @return A String containing the Base64 encoded data, broken into lines.
   */
  public static String encodeLines( final byte[] in, final int iOff, final int iLen, final int lineLen,
          final String lineSeparator ) {
    final int blockLen = ( lineLen * 3 ) / 4;
    if ( blockLen <= 0 ) {
      throw new IllegalArgumentException();
    }
    final int lines = ( iLen + blockLen - 1 ) / blockLen;
    final int bufLen = ( ( iLen + 2 ) / 3 ) * 4 + lines * lineSeparator.length();
    final StringBuilder buf = new StringBuilder( bufLen );
    int ip = 0;
    while ( ip < iLen ) {
      final int l = Math.min( iLen - ip, blockLen );
      buf.append( Base64Coder.encode( in, iOff + ip, l ) );
      buf.append( lineSeparator );
      ip += l;
    }
    return buf.toString();
  }

  /**
   * Encodes a byte array into Base64 format. No blanks or line breaks are
   * inserted in the output.
   * 
   * @param in
   *          An array containing the data bytes to be encoded.
   * @return A character array containing the Base64 encoded data.
   */
  public static char[] encode( final byte[] in ) {
    return Base64Coder.encode( in, 0, in.length );
  }

  /**
   * Encodes a byte array into Base64 format. No blanks or line breaks are
   * inserted in the output.
   * 
   * @param in
   *          An array containing the data bytes to be encoded.
   * @param iLen
   *          Number of bytes to process in <code>in</code>.
   * @return A character array containing the Base64 encoded data.
   */
  public static char[] encode( final byte[] in, final int iLen ) {
    return Base64Coder.encode( in, 0, iLen );
  }

  /**
   * Encodes a byte array into Base64 format. No blanks or line breaks are
   * inserted in the output.
   * 
   * @param in
   *          An array containing the data bytes to be encoded.
   * @param iOff
   *          Offset of the first byte in <code>in</code> to be processed.
   * @param iLen
   *          Number of bytes to process in <code>in</code>, starting at
   *          <code>iOff</code>.
   * @return A character array containing the Base64 encoded data.
   */
  public static char[] encode( final byte[] in, final int iOff, final int iLen ) {
    final int oDataLen = ( iLen * 4 + 2 ) / 3; // output length without padding
    final int oLen = ( ( iLen + 2 ) / 3 ) * 4; // output length including padding
    final char[] out = new char[oLen];
    int ip = iOff;
    final int iEnd = iOff + iLen;
    int op = 0;
    while ( ip < iEnd ) {
      final int i0 = in[ip++] & 0xff;
      final int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
      final int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
      final int o0 = i0 >>> 2;
      final int o1 = ( ( i0 & 3 ) << 4 ) | ( i1 >>> 4 );
      final int o2 = ( ( i1 & 0xf ) << 2 ) | ( i2 >>> 6 );
      final int o3 = i2 & 0x3F;
      out[op++] = Base64Coder.map1[o0];
      out[op++] = Base64Coder.map1[o1];
      out[op] = op < oDataLen ? Base64Coder.map1[o2] : '=';
      op++;
      out[op] = op < oDataLen ? Base64Coder.map1[o3] : '=';
      op++;
    }
    return out;
  }

  /**
   * Decodes a string from Base64 format. No blanks or line breaks are allowed
   * within the Base64 encoded input data.
   * 
   * @param charset
   *          the charset
   * @param s
   *          A Base64 String to be decoded.
   * @return A String containing the decoded data.
   * @throws IllegalArgumentException
   *           If the input is not valid Base64 encoded data.
   */
  public static String decodeString( final String s, final Charset charset ) {
    return new String( Base64Coder.decode( s ), charset );
  }

  /**
   * Decodes a byte array from Base64 format and ignores line separators, tabs
   * and blanks. CR, LF, Tab and Space characters are ignored in the input data.
   * This method is compatible with
   * <code>sun.misc.BASE64Decoder.decodeBuffer(String)</code>.
   * 
   * @param s
   *          A Base64 String to be decoded.
   * @return An array containing the decoded data bytes.
   * @throws IllegalArgumentException
   *           If the input is not valid Base64 encoded data.
   */
  public static byte[] decodeLines( final String s ) {
    final char[] buf = new char[s.length()];
    int p = 0;
    for ( int ip = 0; ip < s.length(); ip++ ) {
      final char c = s.charAt( ip );
      if ( c != ' ' && c != '\r' && c != '\n' && c != '\t' ) {
        buf[p++] = c;
      }
    }
    return Base64Coder.decode( buf, 0, p );
  }

  /**
   * Decodes a byte array from Base64 format. No blanks or line breaks are
   * allowed within the Base64 encoded input data.
   * 
   * @param s
   *          A Base64 String to be decoded.
   * @return An array containing the decoded data bytes.
   * @throws IllegalArgumentException
   *           If the input is not valid Base64 encoded data.
   */
  public static byte[] decode( final String s ) {
    return Base64Coder.decode( s.toCharArray() );
  }

  /**
   * Decodes a byte array from Base64 format. No blanks or line breaks are
   * allowed within the Base64 encoded input data.
   * 
   * @param in
   *          A character array containing the Base64 encoded data.
   * @return An array containing the decoded data bytes.
   * @throws IllegalArgumentException
   *           If the input is not valid Base64 encoded data.
   */
  public static byte[] decode( final char[] in ) {
    return Base64Coder.decode( in, 0, in.length );
  }

  /**
   * Decodes a byte array from Base64 format. No blanks or line breaks are
   * allowed within the Base64 encoded input data.
   * 
   * @param in
   *          A character array containing the Base64 encoded data.
   * @param iOff
   *          Offset of the first character in <code>in</code> to be processed.
   * @param iLen
   *          Number of characters to process in <code>in</code>, starting at
   *          <code>iOff</code>.
   * @return An array containing the decoded data bytes.
   * @throws IllegalArgumentException
   *           If the input is not valid Base64 encoded data.
   */
  public static byte[] decode( final char[] in, final int iOff, final int iLen ) {
    int len = iLen;
    if ( len % 4 != 0 ) {
      throw new IllegalArgumentException( "Length of Base64 encoded input string is not a multiple of 4." );
    }
    while ( len > 0 && in[iOff + len - 1] == '=' ) {
      len--;
    }
    final int oLen = ( len * 3 ) / 4;
    final byte[] out = new byte[oLen];
    int ip = iOff;
    final int iEnd = iOff + len;
    int op = 0;
    while ( ip < iEnd ) {
      final int i0 = in[ip++];
      final int i1 = in[ip++];
      final int i2 = ip < iEnd ? in[ip++] : 'A';
      final int i3 = ip < iEnd ? in[ip++] : 'A';
      if ( i0 > 127 || i1 > 127 || i2 > 127 || i3 > 127 ) {
        throw new IllegalArgumentException( "Illegal character in Base64 encoded data." );
      }
      final int b0 = Base64Coder.map2[i0];
      final int b1 = Base64Coder.map2[i1];
      final int b2 = Base64Coder.map2[i2];
      final int b3 = Base64Coder.map2[i3];
      if ( b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0 ) {
        throw new IllegalArgumentException( "Illegal character in Base64 encoded data." );
      }
      final int o0 = ( b0 << 2 ) | ( b1 >>> 4 );
      final int o1 = ( ( b1 & 0xf ) << 4 ) | ( b2 >>> 2 );
      final int o2 = ( ( b2 & 3 ) << 6 ) | b3;
      out[op++] = ( byte ) o0;
      if ( op < oLen ) {
        out[op++] = ( byte ) o1;
      }
      if ( op < oLen ) {
        out[op++] = ( byte ) o2;
      }
    }
    return out;
  }

  // Dummy constructor.
  private Base64Coder() {
  }

} // end class Base64Coder
