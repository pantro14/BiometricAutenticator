package carvajal.autenticador.android.framework.morphosmart;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import carvajal.autenticador.android.framework.morphosmart.exception.ConexionHuelleroException;
import carvajal.autenticador.android.framework.morphosmart.exception.MorphoSmartException;
import carvajal.autenticador.android.framework.morphosmart.info.ProcessInfo;

import com.morpho.morphosmart.sdk.ITemplateType;
import com.morpho.morphosmart.sdk.MorphoDevice;
import com.morpho.morphosmart.sdk.Template;
import com.morpho.morphosmart.sdk.TemplateFVP;
import com.morpho.morphosmart.sdk.TemplateFVPType;
import com.morpho.morphosmart.sdk.TemplateList;
import com.morpho.morphosmart.sdk.TemplateType;

/**
 * Clase que permite obtener y generar las plantillas utilizadas en para la
 * verificación de la huella.
 * 
 * @author grasotos
 * @date 24-Feb-2015
 */
public class MorphoSmartManager {

	/**
	 * Log de la aplicación Android Log4j
	 */
	private final static Logger log4jDroid = Logger.getLogger(MorphoSmartManager.class);

	/**
	 * Constructor por defecto.
	 * 
	 * @author grasotos
	 * @date 24-Feb-2015
	 */
	public MorphoSmartManager() {
	}

	/**
	 * Permite almacenar los logs del huellero.
	 * 
	 * @param morphoDevice
	 *            dispositivo Morpho.
	 * @author grasotos
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	@SuppressLint("SimpleDateFormat")
	public void getAndWriteFFDLogs(MorphoDevice morphoDevice) throws MorphoSmartException {
		try {
			String ffdLogs = morphoDevice.getFFDLogs();

			if (ffdLogs != null) {
				String serialNbr = ProcessInfo.getInstance().getMSOSerialNumber();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String currentDateandTime = sdf.format(new Date());
				String saveFile = "sdcard/" + serialNbr + "_" + currentDateandTime + "_Audit.log";

				FileWriter fstream = new FileWriter(saveFile, true);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(ffdLogs);
				out.close();

			}
		} catch (IOException e) {
			log4jDroid.error("AutenticadorAndroidProject:MorphoSmartManager:getAndWriteFFDLogs:", e);
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:MorphoSmartManager:getAndWriteFFDLogs:", e);
			throw new MorphoSmartException(e.getMessage());
		}
	}

	/**
	 * Permite generar la plantilla de acuerdo a la extensión usada, en este
	 * caso PKC.
	 * 
	 * @param extention
	 *            extensión de la aplicación.
	 * @return ITemplate con la información de la plantilla.
	 * @author grasotos
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public static ITemplateType getTemplateTypeFromExtention(String extention) throws MorphoSmartException {
		try {
			for (TemplateType templateType : TemplateType.values()) {
				if (templateType.getExtension().equalsIgnoreCase(extention)) {
					return templateType;
				}
			}
			for (TemplateFVPType templateFVPType : TemplateFVPType.values()) {
				if (templateFVPType.getExtension().equalsIgnoreCase(extention)) {
					return templateFVPType;
				}
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:MorphoSmartManager:getTemplateTypeFromExtention:", e);
			throw new MorphoSmartException(e.getMessage());
		}
		return TemplateType.MORPHO_NO_PK_FP;
	}

	/**
	 * Permite generar el listado de plantillas formado por las huellas
	 * necesarias para la verificación.
	 * 
	 * @param buffers
	 * @return TemplateList con la información de las huellas a comparar.
	 * @author grasotos
	 * @throws MorphoSmartException
	 * @throws ConexionHuelleroException
	 * @date 24-Feb-2015
	 */
	public TemplateList generarListaTemplates(List<byte[]> buffers) throws MorphoSmartException {
		TemplateList templateList = new TemplateList();

		try {

			for (int i = 0; i < buffers.size(); i++) {

				Template template = new Template();
				TemplateFVP templateFVP = new TemplateFVP();
				// TODO:CAMBIAR A STRINGS DE AUTENTICADOR
				ITemplateType iTemplateType = getTemplateTypeFromExtention(".pkc");
				if (iTemplateType instanceof TemplateFVPType) {
					templateFVP.setData(buffers.get(i));
					templateFVP.setTemplateFVPType((TemplateFVPType) iTemplateType);
					templateList.putFVPTemplate(templateFVP);
				} else {
					
						
						template.setData(buffers.get(i));
						template.setTemplateType((TemplateType) iTemplateType);
						templateList.putTemplate(template);
					
					
					
				}
			}
		} catch (Exception e) {
			log4jDroid.error("AutenticadorAndroidProject:MorphoSmartManager:generarListaTemplates:", e);
			throw new MorphoSmartException(e.getMessage());
		}
		return templateList;

	}

}
