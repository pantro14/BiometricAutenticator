/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * A Derivative Work, changed by Manatee Works.
 *
 */

package carvajal.autenticador.android.code2D;



import com.manateeworks.BarcodeScanner;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import carvajal.autenticador.android.activity.R;


final class DecodeHandler extends Handler
{

    private final ActivityCapture activity;

    DecodeHandler(ActivityCapture activity)
    {
        this.activity = activity;
    }

    @Override
  public void handleMessage(Message message) {
	    if (message.what == R.id.decode) {
			//Log.d(TAG, "Got decode message");
			decode((byte[]) message.obj, message.arg1, message.arg2);
		} else if (message.what == R.id.quit) {
			Looper.myLooper().quit();
		}
    }
    
   
    /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
     * 
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
     */
  private void decode(byte[] data, int width, int height) {

	  //Check for barcode inside buffer
        byte[] rawResult = BarcodeScanner.MWBscanGrayscaleImage(data, width,height);
        
        //ignore results less than 4 characters - probably false detection
        if (rawResult != null && rawResult.length > 4 || (rawResult != null && (rawResult.length > 0 && 
        		BarcodeScanner.MWBgetLastType() != BarcodeScanner.FOUND_39 && 
        		BarcodeScanner.MWBgetLastType() != BarcodeScanner.FOUND_25_INTERLEAVED && 
        		BarcodeScanner.MWBgetLastType() != BarcodeScanner.FOUND_25_STANDARD)))
        {

            Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        }
        else
        {
            Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }

}
