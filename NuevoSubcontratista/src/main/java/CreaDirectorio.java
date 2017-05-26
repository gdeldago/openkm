import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.module.db.stuff.DbSessionManager;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import java.util.HashMap;
import java.util.Map;

public class CreaDirectorio implements ActionHandler {

	private static final long serialVersionUID = 1L;

	/**
	* The message member gets its value from the configuration in the
	* processdefinition. The value is injected directly by the engine.
	*/
	String message;

	/**
	* A message process variable is assigned the value of the message
	* member. The process variable is created if it doesn't exist yet.
	*/
	public void execute(ExecutionContext context) throws Exception {

		Input formVar;
		Select formSel;
		
		String sysToken = DbSessionManager.getInstance().getSystemToken();
				
		formSel = (Select) context.getContextInstance().getVariable("proyecto");
		String proyecto = formSel.getValue();
		//System.out.println(proyecto);
		formVar = (Input) context.getContextInstance().getVariable("cuit");
		String cuit = formVar.getValue().trim();
		//System.out.println(cuit);
		formVar = (Input) context.getContextInstance().getVariable("razonSocial");
		String razonSocial = formVar.getValue().trim();
		//System.out.println(razonSocial);
		formVar = (Input) context.getContextInstance().getVariable("fechaIngreso");
		String fechaIngreso = formVar.getValue().trim();
		//System.out.println(fechaIngreso);
		/**
		 * Crea Directorio 
		 */
		String newFolder = "/okm:root/SIGCSSA-QAS/09 - Proyectos/" + proyecto + "/Gestión CSSA/Subcontratistas/" + cuit;
		//System.out.println(newFolder);
		OKMFolder.getInstance().createSimple(sysToken, newFolder);
		/**
		 * Agrega Grupo de Propiedades 
		 */
		OKMPropertyGroup.getInstance().addGroup(sysToken, newFolder, "okg:cssa_subcontra");
		/**
		 * Asigna valores
		 */
		Map<String, String> properties = new HashMap<>();
        properties.put("okp:cssa_subcontra.cuit", cuit);
        properties.put("okp:cssa_subcontra.razonSocial", razonSocial);
        properties.put("okp:cssa_subcontra.fechaIngreso", fechaIngreso);
        OKMPropertyGroup.getInstance().setPropertiesSimple(null, newFolder,"okg:cssa_subcontra", properties);           
	           
        // Termina, para esperar señal manual comentar la linea de abajo
        context.leaveNode();  
	}

}
