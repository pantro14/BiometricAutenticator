package carvajal.autenticador.android.framework.possdk;

import java.util.ArrayList;

import carvajal.autenticador.android.util.Util;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.ImageView;

/**
 * La Clase ImagenReporteView encapsula los atributos y métodos necesarios
 * para generar la imagen del reporte de autenticaciones y de las aprobadas
 * por el delegado, con la información requeria para imprimir.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since 09 de Abril de 2015
 */
public class ImagenReporteView extends ImageView {
	
	/** varaible globla que representa el ancho del canvas a pintar */
	private int X;
	/** varaible globla que representa el alto del canvas a pintar */
	private int Y;
	
	
	/**
	 * varaible globla de Pintado, especifica las propiedades de la fuente de
	 * textos Grande
	 */
	private Paint paintFuenteGrande = new Paint();
	/**
	 * varaible globla de Pintado, especifica las propiedades de la fuente de
	 * textos Mediano
	 */
	private Paint paintFuenteMediana = new Paint();
	/**
	 * varaible globla de Pintado, especifica las propiedades de la fuente de
	 * textos Pequeña
	 */
	private Paint paintFuentePequena = new Paint();
	/**
	 * varaible globla de Pintado, especifica las propiedades de la línea
	 * sobre la cuál se va a hacer la firma del supervisor
	 */
	private Paint paintLineaFirma = new Paint();	
	
	/**
	 * varaible globla de Path (Trazo de línea), especifica las propiedades
	 * geométrica de la línea de la firma del supervisor.
	 */
	private Path lineaFirma = new Path();
	// MAC address del dispositivo móvil específico.
	private String mac;
	
	private String codigoPuesto;
	
	private String puesto;
	
	ArrayList<Long> listaCedulas = new ArrayList<Long>();
	
	/**
	 * Método construtor de la clase ImagenReporteView
	 * 
	 * @param context
	 * 			  Es el contexto base que invoca el constructor
	 * @param x
	 *            varaible de miembro, es el ancho del canvas a pintar parametrizado
	 * @param y
	 *            varaible de miembro, es el alto del canvas a pintar parametrizado
	 */
	public ImagenReporteView(Context context, int x, int y, String codigoPuesto, String puesto, ArrayList<Long> listaCedulas) {
		super(context);
		this.mac = Util.obtenerMAC(context);
		this.X = x;
		this.Y = y;
		this.codigoPuesto = codigoPuesto;
		this.puesto = puesto;
		this.listaCedulas = listaCedulas;
	}
	
	/*
	 * En esta función se realiza el dibujo del canvas por medio de los datos
	 * recibidos en el constructor de la clase y se organizan a través de
	 * fracciones de las coordenadas X e Y.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Se agrega la propiedad de color negro al Paint
		paintFuenteGrande.setColor(Color.BLACK);
		// Se agrega la propiedad de tamaño del texto al Paint.
		paintFuenteGrande.setTextSize(17);
		
		// Se agrega la propiedad de color negro al Paint
		paintFuentePequena.setColor(Color.BLACK);
		// Se agrega la propiedad de tamaño del texto al Paint.
		paintFuentePequena.setTextSize(14);
		
		// Se agrega la propiedad de color negro al Paint
		paintFuenteMediana.setColor(Color.BLACK);
		// Se agrega la propiedad de tamaño del texto al Paint.
		paintFuenteMediana.setTextSize(16);
		
		// Parámetro de guía con el fin de ajusta las coordenadas en la altura del reporte
		int y0 = 0;
		// el parámetro de guía de la coordenada de altura se incrementa en 15 unidades
		y0+=15;
		
		/* Se dibuja texto en el canvas, en este caso el título del reporte
		 * con el texto, y sobre las coordenadas x=0 e y=y0 que en ese caso vale 15.
		 */
		canvas.drawText("REPORTE APROBADOS POR DELEGADO", 0, y0,
				paintFuenteMediana);
		// el parámetro de guía de la coordenada de altura se incrementa en 20 unidades
		y0+=20;
		/* Se dibuja texto en el canvas, en este caso la MAC del dispositivo del reporte
		 * con el texto, y sobre las coordenadas x=la doseava parte del ancho e y=y0 que en ese caso vale 35.
		 */
		canvas.drawText("PID: " + mac, X / 12, y0, paintFuentePequena);
		// el parámetro de guía de la coordenada de altura se incrementa en 15 unidades
		y0+=15;
		/* Se dibuja texto en el canvas, en este caso el Código del Puesto del reporte
		 * con el texto, y sobre las coordenadas x=0 e y=y0 que en ese caso vale 50.
		 */
		canvas.drawText("COD. COLEGIO ELECTORAL: "+codigoPuesto , 0, y0, paintFuentePequena);	
		// el parámetro de guía de la coordenada de altura se incrementa en 15 unidades
		y0+=15;
		/* Se dibuja texto en el canvas, en este caso el Puesto cabecera municipal del reporte
		 * con el texto, y sobre las coordenadas x=0 e y=y0 que en ese caso vale 65.
		 */
		canvas.drawText(puesto , 0, y0, paintFuentePequena);	
		
		/* el parámetro de guía de la coordenada de altura se disminuye en 40 unidades
		*  con respecto al tamaño total de la altura que es Y */
		y0=Y-40;
		
		// Se agrega la propiedad de color NEGRO a la línea de la firma
		paintLineaFirma.setColor(Color.BLACK);
		// Se agrega la propiedad de ancho de línea.
		paintLineaFirma.setStrokeWidth(3);
		// Se agrega la propiedad de estilo de línea continua
		paintLineaFirma.setStyle(Paint.Style.STROKE);
		// Se asigna la coordenada inicial de la línea en x=0 y y=y0 
		lineaFirma.moveTo(0, y0);
		// Se asigna la coordenada inicial de la línea en x=0 y y=y0 
		lineaFirma.lineTo(X, y0); 
		//se dibuja la línea
		canvas.drawPath(lineaFirma, paintLineaFirma);
		
		/* el parámetro de guía de la coordenada de altura se disminuye en 100 unidades
		*  con respecto al tamaño total de la altura que es Y */
		y0=Y-100;
		/* Se dibuja texto en el canvas, en este caso la firma del delegado del reporte
		 * con el texto, y sobre las coordenadas x=0 e y=y0.
		 */
		canvas.drawText("Firma de Delegado:" , 0, y0, paintFuentePequena);	
		
		
		/* Prueba de concepto para pintar las cédulas
		 *  después se debe cambiar por el resultado del query
		 */
		
		//	coordenada incial en el eje y de cada columna
		int y1 = 80;
		int y2 = 80;
		int y3 = 80;

		for(Long cedula:listaCedulas){	//se hace un bucle en la lista de cédulas
			if(y1<=(Y-140)){//	Si el valor de la primera columna no excede 140 pixeles inferiores
				y1 += 15;
				canvas.drawText(String.valueOf(cedula) , 0, y1, paintFuentePequena);
			}
			else{
				if(y2<=(Y-140)){//	Si el valor de la segunda columna no excede 140 pixeles inferiores
					y2 += 15;
					canvas.drawText(String.valueOf(cedula) , X / 3, y2, paintFuentePequena);
				}
				else{
					if(y3<=(Y-140)){//	Si el valor de la tercera columna no excede 140 pixeles inferiores
						y3 += 15;
						canvas.drawText(String.valueOf(cedula) , 2*X / 3, y3, paintFuentePequena);
					}
				}
			}
		}
	}	


}
