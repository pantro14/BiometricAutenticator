package carvajal.autenticador.android.framework.possdk;

import carvajal.autenticador.android.util.Util;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

/**
 * La Clase ImagenComprobanteView encapsula los atributos y métodos necesarios
 * para generar la imagen del comprobante de autenticación, con la información
 * requeria para imprimir.
 * 
 * @author davparpa - David Pardo
 * @version 1.0
 * @since 17 de Febrero de 2015
 */
public class ImagenComprobanteView extends ImageView {

	/** variable global del nombre del acto electoral y título del comprobante */
	private String nombreEleccion;
	/** varaible global del número de identificación del elector. */
	private String cedulaElector;
	/** varaible global de la mesa en la cual el elector debe votar */
	private String numeroMesaElector;
	/**
	 * varaible global de la fecha calendario en la cual se hace la
	 * autenticación
	 */
	private String fechaAutenticacion;
	
	private String mac;

	/**
	 * varaible globla de Pintado, especifica las propiedades de la fuente de
	 * textos Grande
	 */
	private Paint paintFuenteGrande = new Paint();
	/**
	 * varaible globla de Pintado, especifica las propiedades de la fuente de
	 * textos Pequeños
	 */
	private Paint paintFuentePequena = new Paint();
	private Paint paintFuenteMediana = new Paint();

	/** varaible globla que representa el ancho del canvas a pintar */
	private int X;
	/** varaible globla que representa el alto del canvas a pintar */
	private int Y;

	private boolean duplicado;

	private boolean esJurado;

	Context context;

	/**
	 * Método construtor de la clase ImagenComprobanteView
	 * 
	 * @param context
	 *            Es el contexto base que invoca el constructor
	 * @param nombreEleccion
	 *            titulo del comprobante con el nombre del acto electoral
	 * @param cedulaElector
	 *            número de identificación del elector.
	 * @param numeroMesaElector
	 *            mesa en la cual el elector debe votar.
	 * @param fechaAutenticacion
	 *            fecha calendario en la cual se hace la autenticación
	 */
	public ImagenComprobanteView(Context context, String nombreEleccion,
			String cedulaElector, String numeroMesaElector,
			String fechaAutenticacion, boolean duplicado, boolean esJurado) {
		super(context);
		this.nombreEleccion = nombreEleccion;
		this.cedulaElector = cedulaElector;
		this.numeroMesaElector = numeroMesaElector;
		this.fechaAutenticacion = fechaAutenticacion;
		this.mac = Util.obtenerMAC(context);
		this.duplicado = duplicado;
		this.esJurado = esJurado;
		this.context = context;
	}

	/*
	 * En esta función se reciben las dimensiones de anchura y altura de la
	 * imagen del comprobante de autenticación y se asignan a los atributos
	 * globales X e Y.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		X = 300;
		Y = 200;
	}

	/*
	 * En esta función se realiza el dibujo del canvas por medio de los datos
	 * recibidos en el constructor de la clase y se organizan a través de
	 * fracciones de las coordenadas X e Y.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		paintFuenteGrande.setColor(Color.BLACK);
		// Se agrega la propiedad de color negro al Paint
		paintFuenteGrande.setTextSize(19);
		// Se agrega la propiedad de tamaño del texto al Paint.

		paintFuentePequena.setColor(Color.BLACK);
		// Se agrega la propiedad de color negro al Paint
		paintFuentePequena.setTextSize(15);
		// Se agrega la propiedad de tamaño del texto al Paint.

		paintFuenteMediana.setColor(Color.BLACK);
		// Se agrega la propiedad de color negro al Paint
		paintFuenteMediana.setTextSize(17);
		// Se agrega la propiedad de tamaño del texto al Paint.

		canvas.drawText("CERTIFICADO DE BIOMETRÍA", X / 12, 2 * Y / 9,
				paintFuenteGrande);
		// Se dibuja texto en el canvas
		canvas.drawText(nombreEleccion, X / 12, ((5 * Y) + 9) / 18/* Y/3 */,
				paintFuentePequena);
		// Se dibuja texto en el canvas
		canvas.drawText("CC: " + cedulaElector, X / 6, 4 * Y / 9,
				paintFuenteMediana);
		// Se dibuja texto en el canvas
		if (!esJurado) {
			// Si NO es Jurado, la mesa se debe imprimir en el comprobante
			canvas.drawText("MESA: " + numeroMesaElector, X / 6, /* Y/2 */
					5 * Y / 9, paintFuenteMediana);
			// Se dibuja texto en el canvas
		} else {
			// Si es Jurado, la mesa se debe imprimir en el comprobante
			canvas.drawText("JURADO", X / 6, /* Y/2 */5 * Y / 9,
					paintFuenteMediana);
		}

		canvas.drawText("FECHA: " + fechaAutenticacion, X / 6, /* 10*Y/18 */
				2 * Y / 3, paintFuenteMediana);
		// Se dibuja texto en el canvas
		/*
		 * canvas.drawText("PID: abcdefg", X/12, 2*Y/37*Y/9,
		 * paintFuentePequena);
		 */
		canvas.drawText("PID: " + mac, X / 12, 15 * Y / 18, paintFuentePequena);
		// Se dibuja texto en el canvas

		if (duplicado) {
			// ES UN DUPLICADO
			canvas.drawText("DUPLICADO", X / 2, 5 * Y / 9, paintFuenteGrande);
		}
	}
}
