/*
 * Copyright (C) 2012  Manatee Works, Inc.
 *
 */

package com.manateeworks;

import java.nio.ByteBuffer;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;


public class BarcodeScanner {

  static {
		System.loadLibrary("BarcodeScannerLib");
	  }
  
  
  /**
   * @name Basic return values for API functions
   * @{
   */
  public static final int  MWB_RT_OK =                  	  0;
  public static final int  MWB_RT_FAIL =                	 -1;
  public static final int  MWB_RT_NOT_SUPPORTED =       	 -2;
  public static final int  MWB_RT_BAD_PARAM =            	 -3;
  
  

  /** @brief  Code39 decoder flags value: require checksum check
   */
  public static final int  MWB_CFG_CODE39_REQUIRE_CHECKSUM =  0x2;
  /**/
  
  /** @brief  Code39 decoder flags value: don't require stop symbol - can lead to false results
   */
  public static final int  MWB_CFG_CODE39_DONT_REQUIRE_STOP = 0x4;
  /**/
      
  /** @brief  Code39 decoder flags value: decode full ASCII
   */
  public static final int MWB_CFG_CODE39_EXTENDED_MODE =      0x8;
  /**/
  
  /** @brief  Code93 decoder flags value: decode full ASCII
   */
  public static final int MWB_CFG_CODE93_EXTENDED_MODE =      0x8;
  /**/

  /** @brief  Code25 decoder flags value: require checksum check
  */
  public static final int  MWB_CFG_CODE25_REQ_CHKSUM =        0x1;
 /**/
  
  /** @brief  Code11 decoder flags value: require checksum check
   *  MWB_CFG_CODE11_REQ_SINGLE_CHKSUM is set by default
   */
  public static final int  MWB_CFG_CODE11_REQ_SINGLE_CHKSUM =  0x1;
  public static final int  MWB_CFG_CODE11_REQ_DOUBLE_CHKSUM =  0x2;
  /**/
  
  /** @brief  MSI Plessey decoder flags value: require checksum check
   *  MWB_CFG_MSI_REQ_10_CHKSUM is set by default
   */
  public static final int  MWB_CFG_MSI_REQ_10_CHKSUM =                 0x01;
  public static final int  MWB_CFG_MSI_REQ_1010_CHKSUM =               0x02;
  public static final int  MWB_CFG_MSI_REQ_11_IBM_CHKSUM =             0x04;
  public static final int  MWB_CFG_MSI_REQ_11_NCR_CHKSUM =             0x08;
  public static final int  MWB_CFG_MSI_REQ_1110_IBM_CHKSUM =           0x10;
  public static final int  MWB_CFG_MSI_REQ_1110_NCR_CHKSUM =           0x20;
  /**/
  
  /** @brief  Codabar decoder flags value: include start/stop symbols in result
  */
  public static final int  MWB_CFG_CODABAR_INCLUDE_STARTSTOP =        0x1;
 /**/
  
  

  /** @brief  Global decoder flags value: apply sharpening on input image
   */
  public static final int  MWB_CFG_GLOBAL_HORIZONTAL_SHARPENING =          0x01;
  public static final int  MWB_CFG_GLOBAL_VERTICAL_SHARPENING =            0x02;
  public static final int  MWB_CFG_GLOBAL_SHARPENING =                     0x03;
  
  /** @brief  Global decoder flags value: apply rotation on input image
   */
  public static final int  MWB_CFG_GLOBAL_ROTATE90 =                       0x04;
  
  /**
   * @name Bit mask identifiers for supported decoder types
   * @{ */
  public static final int MWB_CODE_MASK_NONE =             0x00000000;
  public static final int MWB_CODE_MASK_QR =               0x00000001;
  public static final int MWB_CODE_MASK_DM =               0x00000002;
  public static final int MWB_CODE_MASK_RSS =              0x00000004;
  public static final int MWB_CODE_MASK_39 =               0x00000008;
  public static final int MWB_CODE_MASK_EANUPC =           0x00000010;
  public static final int MWB_CODE_MASK_128 = 	           0x00000020;
  public static final int MWB_CODE_MASK_PDF = 	           0x00000040;
  public static final int MWB_CODE_MASK_AZTEC =	           0x00000080;
  public static final int MWB_CODE_MASK_25 =	           0x00000100;
  public static final int MWB_CODE_MASK_93 =               0x00000200;
  public static final int MWB_CODE_MASK_CODABAR =          0x00000400;
  public static final int MWB_CODE_MASK_DOTCODE =          0x00000800;
  public static final int MWB_CODE_MASK_11 =       	 	   0x00001000;
  public static final int MWB_CODE_MASK_MSI =     	 	   0x00002000;
  public static final int MWB_CODE_MASK_ALL =              0xffffffff;
  /** @} */
  
  
  /**
   * @name Bit mask identifiers for RSS decoder types
   * @{ */
  public static final int MWB_SUBC_MASK_RSS_14 =           0x00000001;
  public static final int MWB_SUBC_MASK_RSS_LIM =          0x00000004;
  public static final int MWB_SUBC_MASK_RSS_EXP =          0x00000008;
  /** @} */
  
  /**
   * @name Bit mask identifiers for Code 2 of 5 decoder types
   * @{ */
  public static final int MWB_SUBC_MASK_C25_INTERLEAVED =  0x00000001;
  public static final int MWB_SUBC_MASK_C25_STANDARD =     0x00000002;
  public static final int MWB_SUBC_MASK_C25_ITF14  =       0x00000004;
  /** @} */
  
  /**
   * @name Bit mask identifiers for UPC/EAN decoder types
   * @{ */
  public static final int MWB_SUBC_MASK_EANUPC_EAN_13 =    0x00000001;
  public static final int MWB_SUBC_MASK_EANUPC_EAN_8 =     0x00000002;
  public static final int MWB_SUBC_MASK_EANUPC_UPC_A =     0x00000004;
  public static final int MWB_SUBC_MASK_EANUPC_UPC_E =     0x00000008;
  /** @} */
  
  /**
   * @name Bit mask identifiers for 1D scanning direction 
   * @{ */
  public static final int MWB_SCANDIRECTION_HORIZONTAL =   0x00000001;
  public static final int MWB_SCANDIRECTION_VERTICAL =     0x00000002;
  public static final int MWB_SCANDIRECTION_OMNI =         0x00000004;
  public static final int MWB_SCANDIRECTION_AUTODETECT =   0x00000008;
  /** @} */
  
  public static final int FOUND_NONE = 			0;
  public static final int FOUND_DM = 			1;
  public static final int FOUND_39 = 			2;
  public static final int FOUND_RSS_14 = 		3;
  public static final int FOUND_RSS_14_STACK = 	4;
  public static final int FOUND_RSS_LIM = 		5;
  public static final int FOUND_RSS_EXP = 		6;
  public static final int FOUND_EAN_13 = 		7;
  public static final int FOUND_EAN_8 = 		8;
  public static final int FOUND_UPC_A = 		9;
  public static final int FOUND_UPC_E = 		10;
  public static final int FOUND_128 = 			11;
  public static final int FOUND_PDF = 			12;
  public static final int FOUND_QR = 			13;
  public static final int FOUND_AZTEC= 			14;
  public static final int FOUND_25_INTERLEAVED =15;
  public static final int FOUND_25_STANDARD =   16;
  public static final int FOUND_93 = 			17;
  public static final int FOUND_CODABAR =		18;
  public static final int FOUND_DOTCODE =		19;
  public static final int FOUND_128_GS1 =		20;
  public static final int FOUND_ITF14 =			21;
  public static final int FOUND_11 =			22;
  public static final int FOUND_MSI =			23;
  
  
  
  public native static int MWBinit (Activity activity);
  public native static int MWBgetLibVersion ();
  public native static int MWBgetSupportedCodes ();
  public native static int MWBsetScanningRect (int codeMask, float left, float top, float width, float height);
  public native static float[] MWBgetScanningRectArray (int codeMask);
  public native static int MWBregisterCode (int codeMask, String userName,String key);
  public native static int MWBsetActiveCodes(int codeMask);
  public native static int MWBgetActiveCodes();
  public native static int MWBsetActiveSubcodes(int codeMask, int subMask);
  public native static int MWBcleanupLib ();
  public native static int MWBgetLastType ();
  public native static int MWBisLastGS1 ();
  public native static byte[] MWBscanGrayscaleImage (byte[]gray,int width,int height);
  public native static int MWBsetFlags(int codeMask, int flags);
  public native static int MWBsetLevel(int level);
  public native static int MWBsetDirection(int direction);
  public native static int MWBgetDirection();
  public native static int MWBvalidateVIN(byte[] vin);
  public native static float[] MWBgetBarcodeLocation();
  
  public static int MWBsetScanningRect (int codeMask, Rect rect){
	  
	  return MWBsetScanningRect(codeMask, rect.left, rect.top, rect.width() + rect.left, rect.height() + rect.top);
	  
  }
  
  public static RectF MWBgetScanningRect (int codeMask) {
	float f[] = MWBgetScanningRectArray(codeMask);
	return new RectF(f[0], f[1], f[2], f[3]);
	  
  }
   
 
}
