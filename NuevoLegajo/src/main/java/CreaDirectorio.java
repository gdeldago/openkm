import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMAuth;
import com.openkm.util.*;
import com.openkm.bean.Permission;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Select;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.*;
import java.sql.*;
import com.openkm.dao.*;

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
		
		formSel = (Select) context.getContextInstance().getVariable("proyecto");
		String proyecto = formSel.getValue();
		//System.out.println(proyecto);
		formVar = (Input) context.getContextInstance().getVariable("cuil");
		String cuil = formVar.getValue().trim();
		//System.out.println(cuil);
		formVar = (Input) context.getContextInstance().getVariable("apellidoNombre");
		String apellidoNombre = formVar.getValue().trim();
		//System.out.println(apellidoNombre);
		/**
		 * Crea Directorio Principal
		 */
		String newFolder = "/okm:root/ADMRRHH-QAS/Legajos/" + proyecto + "/" + cuil;
		System.out.println(newFolder);
		OKMFolder.getInstance().createSimple(null, newFolder);
		/**
		 * Agrega Grupo de Propiedades 
		 */
		OKMPropertyGroup.getInstance().addGroup(null, newFolder, "okg:admrrhh_legajo");
		/**
		 * Asigna valores
		 */
		Map<String, String> properties = new HashMap<>();
        properties.put("okp:admrrhh_legajo.cuil", cuil);
        properties.put("okp:admrrhh_legajo.apellidoNombre", apellidoNombre);
        properties.put("okp:admrrhh_legajo.estado", "0");
        properties.put("okp:admrrhh_legajo.proyecto", proyecto);
        OKMPropertyGroup.getInstance().setPropertiesSimple(null, newFolder, "okg:admrrhh_legajo", properties);           
		/**
		 * Crea Subdirectorios
		 */
      	OKMFolder.getInstance().createSimple(null, newFolder + "/Adm. Personal");
      	OKMFolder.getInstance().createSimple(null, newFolder + "/Nóminas");
      	OKMFolder.getInstance().createSimple(null, newFolder + "/Legales");
        String docMedFolder = newFolder + "/Documentación Médica";
        //System.out.println(docMedFolder);
        OKMFolder.getInstance().createSimple(null, docMedFolder);
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Ausentismo");                                
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Ausentismo" + "/Seguimiento de Ausentismo");                                
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Ausentismo" + "/Certificados");                                
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Informes Médicos");                                
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Informes Médicos" + "/Informes");                                
      	OKMFolder.getInstance().createSimple(null, docMedFolder + "/Informes Médicos" + "/Devolución de Preexistencias");                                
		/**
		 * Asigna Permisos
		 */
       	OKMAuth.getInstance().grantRole (null, docMedFolder, "ADMRRHH_DOC_MED_CON", Permission.READ, true);
    	OKMAuth.getInstance().grantRole (null, docMedFolder, "ADMRRHH_DOC_MED_EDI", Permission.READ + Permission.WRITE, true);
		/**
		 * Actualiza Tabla Empleados
		 */
    	Session hbmSession = HibernateUtil.getSessionFactory().openSession();
    	Connection con = hbmSession.connection();
    	String sql = "INSERT INTO OKM_DB_METADATA_VALUE (DMV_TABLE, DMV_COL00, DMV_COL01, DMV_COL02, DMV_COL03) VALUES ('legajos', '" + cuil + "','0', '"+ apellidoNombre + "','" + proyecto + "')";
	  	LegacyDAO.execute(con, sql);
	  	LegacyDAO.close(con);
	  	HibernateUtil.close(hbmSession);
        
        // Termina, para esperar señal manual comentar la linea de abajo
        context.leaveNode();  

	}

}

/*
import com.openkm.api.OKMRepository;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import java.io.*;
import java.sql.*;
import org.hibernate.*;
import com.openkm.core.*;
import com.openkm.util.*;
import com.openkm.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.openkm.api.OKMAuth;
import com.openkm.bean.Permission;


Map map = new HashMap(); 


Logger log = LoggerFactory.getLogger("GUS_DEBUG");

Session hbmSession = HibernateUtil.getSessionFactory().openSession();
Connection con = hbmSession.connection();

String sql;

OKMRepository repo = OKMRepository.getInstance();

String path = repo.getNodePath(systemToken, uuid);
String parent_path = path.substring(0, path.lastIndexOf("/"));
String folder_name =  path.substring(path.lastIndexOf("/")+1);

parent_path = parent_path.substring(0, parent_path.lastIndexOf("/"));


log.info(path);
log.info(parent_path);

if (parent_path.equals("/okm:root/ADMRRHH-QAS/Legajos")) {
        OKMPropertyGroup.getInstance().addGroup(null, path, "okg:admrrhh_legajo");
	map.put("okp:admrrhh_legajo.cuil",folder_name); 
        OKMPropertyGroup.getInstance().setPropertiesSimple(null, path, "okg:admrrhh_legajo", map);
	
	String docmed_path = path + "/Documentación Médica";
        OKMFolder.getInstance().createSimple(null, docmed_path);
    
    	OKMFolder.getInstance().createSimple(null, docmed_path + "/Ausentismo");                                
	OKMFolder.getInstance().createSimple(null, docmed_path + "/Ausentismo" + "/Seguimiento de Ausentismo");                                
	OKMFolder.getInstance().createSimple(null, docmed_path + "/Ausentismo" + "/Certificados");                                

	OKMFolder.getInstance().createSimple(null, docmed_path + "/Informes Médicos");                                
	OKMFolder.getInstance().createSimple(null, docmed_path + "/Informes Médicos" + "/Informes");                                
	OKMFolder.getInstance().createSimple(null, docmed_path + "/Informes Médicos" + "/Devolución de Preexistencias");                                
    
	OKMFolder.getInstance().createSimple(null, path + "/Adm. Personal");
	OKMFolder.getInstance().createSimple(null, path + "/Nóminas");
	OKMFolder.getInstance().createSimple(null, path + "/Legales");

    	OKMAuth.getInstance().grantRole (null, docmed_path, "ADMRRHH_DOC_MED_CON", Permission.READ, true);
	OKMAuth.getInstance().grantRole (null, docmed_path, "ADMRRHH_DOC_MED_EDI", Permission.READ + Permission.WRITE, true);

    	sql = "INSERT INTO OKM_DB_METADATA_VALUE (DMV_TABLE, DMV_COL00, DMV_COL01) VALUES ('legajos', '" + folder_name + "','0')";
	log.info("sql="+sql);
   	LegacyDAO.execute(con, sql);
}

LegacyDAO.close(con);
HibernateUtil.close(hbmSession);*/