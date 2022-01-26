// $Id: GostHash.java 45536 2013-04-08 13:23:58Z zinal $
package ru.zinal.gosthash;

import java.io.UnsupportedEncodingException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * РћСЃРЅРѕРІРЅРѕР№ РїСѓР±Р»РёС‡РЅС‹Р№ РёРЅС‚РµСЂС„РµР№СЃ РґРѕСЃС‚СѓРїР° Рє СЂР°СЃС‡РµС‚Сѓ С…РµС€-СЃСѓРјРјС‹ Р“РћРЎРў 34.11-94.
 * @author zinal
 * @since 2011-01-13
 */
public abstract class GostHash {

  private GostHash() {}

  //private static final Logger log = LoggerFactory.getLogger(GostHash.class);
  private static final Object guard = new Object();
  private static Boolean hasNative = null;

  public static GostHashIface createImpl() {
    synchronized(guard) {
      if ( hasNative==null ) {
        try {
          System.loadLibrary("gosthash");
          hasNative = Boolean.TRUE;
          //log.info("Р�СЃРїРѕР»СЊР·СѓРµС‚СЃСЏ СЂРµР°Р»РёР·Р°С†РёСЏ С…РµС€-С„СѓРЅРєС†РёРё Р“РћРЎРў Р  34.11-94 РІ РІРёРґРµ Р±РёР±Р»РёРѕС‚РµРєРё JNI");
        } catch(UnsatisfiedLinkError x) {
          hasNative = Boolean.FALSE;
          //log.info("Р�СЃРїРѕР»СЊР·СѓРµС‚СЃСЏ СЂРµР°Р»РёР·Р°С†РёСЏ С…РµС€-С„СѓРЅРєС†РёРё Р“РћРЎРў Р  34.11-94 РЅР° СЏР·С‹РєРµ Java");
        }
      }
    }
    if ( hasNative.booleanValue() )
      return new GostHashNative();
    return new GostHashJava();
  }

  static final String HEXES = "0123456789abcdef";
  public static String convert(byte[] hash) {
    if ( hash==null || hash.length<32 )
      return null;
    final StringBuilder sb = new StringBuilder();
    for ( int i=0; i<32; ++i ) {
      final byte b = hash[31-i];
      sb.append(HEXES.charAt((b & 0xF0) >> 4))
         .append(HEXES.charAt((b & 0x0F)));
    }
    return sb.toString();
  }

  public static byte[] calc(java.io.File f) {
    final GostHashIface ctx = createImpl();
    ctx.init();
    try {
      return ctx.calcHash(f);
    } finally {
      ctx.done();
    }
  }

  public static String calcStr(java.io.File f) {
    final GostHashIface ctx = createImpl();
    ctx.init();
    try {
      return convert(ctx.calcHash(f));
    } finally {
      ctx.done();
    }
  }

  public static String calcStr(String fname) {
    return calcStr(new java.io.File(fname));
  }

  public static byte[] hash(String text) {
    final byte[] block;
    try {
      block = text.getBytes("UTF-8");
    } catch(UnsupportedEncodingException uee) {
      throw new RuntimeException("РЎР±РѕР№ РїСЂРё РїСЂРµРѕР±СЂР°Р·РѕРІР°РЅРёРё СЃС‚СЂРѕРєРё РІ Р±Р»РѕРє РґР°РЅРЅС‹С…", uee);
    }
    return hash(block);
  }

  public static byte[] hash(byte[] block) {
    final GostHashIface ctx = createImpl();
    ctx.init();
    try {
      ctx.hashBlock(block, 0, block.length);
      return ctx.finishHash();
    } finally {
      ctx.done();
    }
  }

  public static String hashStr(String text) {
    return convert(hash(text));
  }

  public static String hashStr(byte[] block) {
    return convert(hash(block));
  }

}
