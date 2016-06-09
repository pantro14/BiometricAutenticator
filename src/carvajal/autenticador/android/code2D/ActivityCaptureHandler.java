/*
 * Copyright (C) 2008 ZXing authors
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
 * A Derivative Work, changed by Manatee Works, Inc.
 *
 */

package carvajal.autenticador.android.code2D;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.camera.CameraManager;
import android.os.Handler;
import android.os.Message;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 */
public final class ActivityCaptureHandler extends Handler
{

    private final ActivityCapture activity;
    private final DecodeThread decodeThread;
    private State state;

    private enum State
    {
        PREVIEW, SUCCESS, DONE
    }

    ActivityCaptureHandler(ActivityCapture activity)
    {
        this.activity = activity;
        decodeThread = new DecodeThread(activity);
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        CameraManager.get().startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message)
    {
        if (message.what == R.id.auto_focus) {
			// When one auto focus pass finishes, start another. This is the
            // closest thing to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what
            // else to do.
            if (state == State.PREVIEW)
            {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
		} else if (message.what == R.id.restart_preview) {
			restartPreviewAndDecode();
		} else if (message.what == R.id.decode_succeeded) {
			state = State.SUCCESS;
			activity.handleDecode((byte[]) message.obj);
		} else if (message.what == R.id.decode_failed) {
			// We're decoding as fast as possible, so when one decode fails,
            // start another.
            state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
		}
    }

    public void quitSynchronously()
    {
        state = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try
        {
            decodeThread.join();
        }
        catch (InterruptedException e)
        {
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    private void restartPreviewAndDecode()
    {
        if (state == State.SUCCESS)
        {
            state = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }

}
