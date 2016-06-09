/*
 * Carvajal TyS - 2015
 * @author: JOHGRAME
 * @date: 26-01-2015
 * @summary: Se crea e implementa esta clase, que hace uso del SDK de ManateeWorks, 
 * para realizar la lectura del codigo 2D con la camera
 */

package carvajal.autenticador.android.code2D;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.manateeworks.BarcodeScanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.camera.CameraManager;



/**
 * Este activity, se debe llamar para invocar la pantalla que leerá el codigo 2D. 
 * Devuelve el valor encontrado en un Intent.putExtra en el key "CEDULA".
 * Para ser llamado este valor, desde la pantalla anterior.
 * 
 */
public final class ActivityCapture extends Activity implements SurfaceHolder.Callback
{
	//Estas varibales, almacenan la licencia para la ejecución del SDK
	public static String userLicence = "";
	public static String keyLicence = "";
	
	
	// se optimiza el SDK para la lectura del codigo PDF417
	public static final boolean PDF_OPTIMIZED = true;
	
    // Se define el area, del rectangulo que leerá el codigo 2D
     public static final Rect RECT_LANDSCAPE_1D = new Rect(3, 20, 94, 60);
        
    
    // Variables usadas por el SDK
    private ActivityCaptureHandler handler;
    private byte[] lastResult;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    // Se crea el Intent que contendrá el valor obtenido de la lectura    
    private Intent intentDatos;
    
    public static String lastStringResult;

 
    public Handler getHandler()
    {
        return handler;
    }

    // se carga el layout definido que realizará la lectura del codigo 2D
    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);
        
        // Se configura el SDK, la licencia para leer PDF417
        BarcodeScanner.MWBregisterCode(BarcodeScanner.MWB_CODE_MASK_PDF, userLicence, keyLicence);
        
        BarcodeScanner.MWBsetDirection(BarcodeScanner.MWB_SCANDIRECTION_HORIZONTAL);
    	BarcodeScanner.MWBsetActiveCodes(BarcodeScanner.MWB_CODE_MASK_PDF);
    	BarcodeScanner.MWBsetScanningRect(BarcodeScanner.MWB_CODE_MASK_PDF,  RECT_LANDSCAPE_1D);
        
        // set decoder effort level (1 - 5)
        // for live scanning scenarios, a setting between 1 to 3 will suffice
		// levels 4 and 5 are typically reserved for batch scanning 
    	// se realiza pruebas con nivel 3, y se comporta mejor
        BarcodeScanner.MWBsetLevel(3);
        
        // Se invoca al controlador de la Camara
        CameraManager.init(getApplication());
        
        
        handler = null;
        lastResult = null;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        
    }

    @Override
    protected void onResume()
    {
        super.onResume();
      

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        
        MWOverlay.addOverlay(this, surfaceView);
        
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface)
        {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        }
        else
        {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }        
        
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        
        MWOverlay.removeOverlay();
        
        if (handler != null)
        {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)
        {
            if (lastResult != null)
            {
                if (handler != null)
                {
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
                return true;
            }
        }
        else
            if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA)
            {
                // Handle these events so they don't launch the Camera app
                return true;
            }
        
        return super.onKeyDown(keyCode, event);
    }
    
    
    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!hasSurface)
        {
            hasSurface = true;
            initCamera(holder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        hasSurface = false;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }
    
    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     * 
     * @param rawResult
     *            The contents of the barcode.
     * Decodifica los Bytes obtenido de la lectura
     */

    public void handleDecode(byte[] rawResult)
    {
        inactivityTimer.onActivity();
        lastResult = rawResult;

        String s = "";
        
        try {
			s = new String(rawResult, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			s = "";
			for (int i = 0; i < rawResult.length; i++)
		            s = s + (char) rawResult[i];	
			e.printStackTrace();
		}
        
        
        int bcType = BarcodeScanner.MWBgetLastType();
        
    // se guarda el valor obtenido en la lectura, Dentro del Intent, y se cierra este activity
        if (bcType >= 0)
        	intentDatos = new Intent();
        	intentDatos.putExtra("CEDULA", s);
        	setResult(RESULT_OK, intentDatos);
        	super.finish();
    }

  // iniciar de la camara y controlador de la misma.
    private void initCamera(SurfaceHolder surfaceHolder)
    {
        try
        {
            // Select desired camera resoloution. Not all devices supports all resolutions, closest available will be chosen
            // If not selected, closest match to screen resolution will be chosen
            // High resolutions will slow down scanning proccess on slower devices
        	
        	if (PDF_OPTIMIZED){
        		CameraManager.setDesiredPreviewSize(1280, 720);
        	} else {
        		CameraManager.setDesiredPreviewSize(800, 480);	
        	}
        	
            
            CameraManager.get().openDriver(surfaceHolder, (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT));
        }
        catch (IOException ioe)
        {
            displayFrameworkBugMessageAndExit();
            return;
        }
        catch (RuntimeException e)
        {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
        	// en caso de no poder conectarse con la camara
            displayFrameworkBugMessageAndExit();
            return;
        }
        if (handler == null)
        {
            handler = new ActivityCaptureHandler(this);
        }
    }

    /**
     * en caso de algun fallo con la lectura de la camara, se genera este evento. 
     */
    private void displayFrameworkBugMessageAndExit()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialogInterface, int i)
            {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	intentDatos = new Intent();
    	setResult(3);
    	super.finish();
    }
   

}
