package org.fundaciobit.plugins.webutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fundaciobit.plugins.utils.AbstractPluginProperties;

/**
 * 
 * @author anadal
 *
 */
public abstract class AbstractWebPlugin<K,V> extends AbstractPluginProperties {

  protected Logger log = Logger.getLogger(this.getClass());
  

  public static final String ABSTRACT_WEB_RES_BUNDLE = "webapi";
  
  public static final String WEBRESOURCE = "webresource";


  /**
   * 
   */
  public AbstractWebPlugin() {
    super();
  }

  /**
   * @param propertyKeyBase
   * @param properties
   */
  public AbstractWebPlugin(String propertyKeyBase, Properties properties) {
    super(propertyKeyBase, properties);
  }

  /**
   * @param propertyKeyBase
   */
  public AbstractWebPlugin(String propertyKeyBase) {
    super(propertyKeyBase);
  }
  
  
  
  public String getName(Locale locale) {
    return getSimpleName();
  }
  

  protected abstract String getSimpleName();
  
  
  //---------------------------------------------------------
 // ------------------- REQUEST BASE ------------------------
 // ---------------------------------------------------------

  
  
  /**
   * 
   */
  protected void requestGETPOST(String absolutePluginRequestPath,
      String relativePluginRequestPath, K key, V value,
      String query, Locale languageUI,
      HttpServletRequest request, HttpServletResponse response, boolean isGet) {

   

    if (query.startsWith(WEBRESOURCE)) {

      retornarRecursLocal(absolutePluginRequestPath, relativePluginRequestPath, key,
          query, request, response, languageUI);
    
    } else {
      // XYZ Fer un missatges com toca
      String titol = (isGet ? "GET" : "POST") + " " + getName(new Locale("ca"))
          + " DESCONEGUT";
      requestNotFoundError(titol, absolutePluginRequestPath, relativePluginRequestPath,
          query, key, request, response, languageUI);
    }
  }
   
  
  
  
  

  // ---------------------------------------------------------
  // ------------------- I18N Utils ------------------------
  // ---------------------------------------------------------

  public abstract String getResourceBundleName();

  public final String getTraduccio(String key, Locale locale, Object... params) {
    return getTraduccio(getResourceBundleName(), key, locale, params);
  }

  public final String getTraduccio(String resourceBundleName, String key, Locale locale,
      Object... params) {

    try {
      // TODO MILLORA: Map de resourcebundle per resourceBundleName i locale

      ResourceBundle rb = ResourceBundle.getBundle(resourceBundleName, locale, UTF8CONTROL);

      String msgbase = rb.getString(key);

      if (params != null && params.length != 0) {
        msgbase = MessageFormat.format(msgbase, params);
      }

      return msgbase;

    } catch (Exception mre) {
      log.error("No trob la traducció per '" + key + "'", new Exception());
      return key + "_" + locale.getLanguage().toUpperCase();
    }

  }

  protected UTF8Control UTF8CONTROL = new UTF8Control();

  public class UTF8Control extends ResourceBundle.Control {
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
        ClassLoader loader, boolean reload) throws IllegalAccessException,
        InstantiationException, IOException {
      // The below is a copy of the default implementation.
      String bundleName = toBundleName(baseName, locale);
      String resourceName = toResourceName(bundleName, "properties");
      ResourceBundle bundle = null;
      InputStream stream = null;
      if (reload) {
        URL url = loader.getResource(resourceName);
        if (url != null) {
          URLConnection connection = url.openConnection();
          if (connection != null) {
            connection.setUseCaches(false);
            stream = connection.getInputStream();
          }
        }
      } else {
        stream = loader.getResourceAsStream(resourceName);
      }
      if (stream != null) {
        try {
          // Only this line is changed to make it to read properties files as
          // UTF-8.
          bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
        } finally {
          stream.close();
        }
      }
      return bundle;
    }
  }
  
  


  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // ------------------- HTML UTILS BUTTON ----------------------
  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  protected void sendRedirect(HttpServletResponse response, String url) {
    try {
      response.sendRedirect(url);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  protected final PrintWriter generateHeader(HttpServletRequest request,
      HttpServletResponse response, String absolutePluginRequestPath,
      String relativePluginRequestPath, String lang, K key, V value) {

    response.setCharacterEncoding("utf-8");
    response.setContentType("text/html");
    PrintWriter out;
    try {
      out = response.getWriter();
    } catch (IOException e) {
      return null;
    }

    
    out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"" + lang
        + "\"  lang=\"" + lang + "\">");
    out.println("<head>");

    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;\" charset=\"UTF-8\" >");

    out.println("<title>" + getSimpleName() + "</title>");
    out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");

    // Javascript i CSS externs
    getJavascriptCSS(request, absolutePluginRequestPath, relativePluginRequestPath, out,
        key, value);

    out.println("</head>");
    out.println("<body>");

    // Missatges
    Map<String, List<String>> missatgesBySignID = missatges.get(keyToSingleString(key));

    if (missatgesBySignID != null && !missatgesBySignID.isEmpty()) {
      out.println("<div class=\"spacer\"></div>");

      for (String tipus : missatgesBySignID.keySet()) {

        for (String msg : missatgesBySignID.get(tipus)) {
          out.println("<div class=\"alert alert-" + tipus + "\">");
          out.println("<button type=\"button\" class=\"close\" data-dismiss=\"alert\">&times;</button>");
          out.println(msg);
          out.println("</div>");
        }
      }
      out.println("<div class=\"spacer\"></div>");
      missatges.remove(keyToSingleString(key));
    }

    return out;

  }

  protected final void generateFooter(PrintWriter out,  K key, V value ) {
    out.println("</body>");
    out.println("</html>");
  }

  protected void getJavascriptCSS(HttpServletRequest request,
      String absolutePluginRequestPath, String relativePluginRequestPath, PrintWriter out,
      K key, V value) {

    out.println("<script src=\"" + relativePluginRequestPath + "/" + WEBRESOURCE + "/js/jquery.js\"></script>");
    out.println("<script src=\"" + relativePluginRequestPath + "/" + WEBRESOURCE + "/js/bootstrap.js\"></script>");
    out.println("<link href=\"" + relativePluginRequestPath + "/" + WEBRESOURCE + "/css/bootstrap.css\" rel=\"stylesheet\" media=\"screen\">");


  }
  
  
  protected abstract String keyToSingleString(K key);

 
  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------
  // ------------------- MISSATGES ---------------------------------------
  // ----------------------------------------------------------------------------
  // ----------------------------------------------------------------------------

  public static final String ERROR = "error";

  public static final String WARN = "warn";

  public static final String SUCCESS = "success";

  public static final String INFO = "info";
  

  private Map<String, Map<String, List<String>>> missatges = new HashMap<String, Map<String, List<String>>>();


  public void saveMessageInfo(String signatureID, String missatge) {
    addMessage(signatureID, INFO, missatge);
  }

  public void saveMessageWarning(String signatureID, String missatge) {
    addMessage(signatureID, WARN, missatge);

  }

  public void saveMessageSuccess(String signatureID, String missatge) {
    addMessage(signatureID, SUCCESS, missatge);
  }

  public void saveMessageError(String signatureID, String missatge) {
    addMessage(signatureID, ERROR, missatge);
  }

  public void addMessage(String signatureID, String type, String missatge) {

    Map<String, List<String>> missatgesBySignID = missatges.get(signatureID);

    if (missatgesBySignID == null) {
      missatgesBySignID = new HashMap<String, List<String>>();
      missatges.put(signatureID, missatgesBySignID);
    }

    List<String> missatgesTipus = missatgesBySignID.get(type);

    if (missatgesTipus == null) {
      missatgesTipus = new ArrayList<String>();
      missatgesBySignID.put(type, missatgesTipus);
    }

    missatgesTipus.add(missatge);

  }

  public void clearMessages(String signatureID) {
    missatges.remove(signatureID);
  }

  public Map<String, List<String>> getMessages(String signatureID) {
    return missatges.get(signatureID);
  };
  
  

  // ---------------------------------------------------------
  // ------------------- Utils ------------------------
  // ---------------------------------------------------------

  public static Properties readPropertiesFromFile(File props) throws FileNotFoundException,
      IOException {

    Properties prop = null;
    if (props.exists()) {

      prop = new Properties();

      FileInputStream fis = new FileInputStream(props);
      prop.load(fis);
      fis.close();
    }
    return prop;
  }
  
  // ---------------------------------------------------------
  // ------------------- DEBUG ------------------------
  // ---------------------------------------------------------

 
  protected void logAllRequestInfo(HttpServletRequest request, String titol,
      String absolutePluginRequestPath, String relativePluginRequestPath, String query,
      K key) {

    log.info(allRequestInfoToStr(request, titol, absolutePluginRequestPath,
        relativePluginRequestPath, query, key));

  }

  protected String allRequestInfoToStr(HttpServletRequest request, String titol,
      String absolutePluginRequestPath, String relativePluginRequestPath, String query,
      K key) {

    StringBuffer str = new StringBuffer("======== PLUGIN REQUEST " + titol + " ===========\n");    
    str.append(key.toString() + "\n");    
    str.append(servletRequestInfoToStr(request, absolutePluginRequestPath,
        relativePluginRequestPath, query));
    return str.toString();
  }
  
  
  
  public static String servletRequestInfoToStr(HttpServletRequest request, String absolutePluginRequestPath,
      String relativePluginRequestPath, String query) {
    
    StringBuffer str = new StringBuffer("======== PLUGIN PARAMS INFO ===========\n");
    str.append("absolutePluginRequestPath: " + absolutePluginRequestPath + "\n");
    str.append("relativePluginRequestPath: " + relativePluginRequestPath + "\n");
    str.append("query: " + query + "\n");
    
    str.append(servletRequestInfoToStr(request));
    
    return str.toString();
  }
    
    public static String servletRequestInfoToStr(HttpServletRequest request) {
      StringBuffer str = new StringBuffer(
          " +++++++++++++++++ SERVLET REQUEST INFO ++++++++++++++++++++++\n");
    str.append(" ++++ Scheme: " + request.getScheme() + "\n");
    str.append(" ++++ ServerName: " + request.getServerName() + "\n");
    str.append(" ++++ ServerPort: " + request.getServerPort() + "\n");
    str.append(" ++++ PathInfo: " + request.getPathInfo() + "\n");
    str.append(" ++++ PathTrans: " + request.getPathTranslated() + "\n");
    str.append(" ++++ ContextPath: " + request.getContextPath() + "\n");
    str.append(" ++++ ServletPath: " + request.getServletPath() + "\n");
    str.append(" ++++ getRequestURI: " + request.getRequestURI() + "\n");
    str.append(" ++++ getRequestURL: " + request.getRequestURL() + "\n");
    str.append(" ++++ getQueryString: " + request.getQueryString() + "\n");
    str.append(" ++++ javax.servlet.forward.request_uri: "
        + (String) request.getAttribute("javax.servlet.forward.request_uri")  + "\n");
    str.append(" ===============================================================");
    return str.toString();
  }

  


  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // ------------------- UPLOAD FILES UTILS ------------------------------------
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  
  
  /**
   * 
   * @param request
   * @param response
   * @param params Output param. retorna els parameters NO File de la request.
   * @return null when error then you must call to "return;"
   */
  protected Map<String, FileItem> readFilesFromRequest(HttpServletRequest request,
      HttpServletResponse response, Properties params) {
    boolean isMultipart = ServletFileUpload.isMultipartContent(request);

    try {

      if (!isMultipart) {
        throw new Exception("Form is not Multipart !!!!!!!");
      }
      DiskFileItemFactory factory = new DiskFileItemFactory();

      
      File temp= getTempDir();
      factory.setRepository(temp);
      
      
      

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);

      // Parse the request to get file items.
      List<FileItem> fileItems = upload.parseRequest(request);

      Map<String, FileItem> mapFile = new HashMap<String, FileItem>();

      // Process the uploaded file items
      for (FileItem fi : fileItems) {

        if (fi.isFormField()) {
          
          if (params != null) {
            String fieldname = fi.getFieldName();
            String fieldvalue = fi.getString();
            params.put(fieldname,  fieldvalue);
          }
          
          
        } else {
          String fieldName = fi.getFieldName();
          if (log.isDebugEnabled()) {
            log.debug("Uploaded File:  PARAM = " + fieldName + " | FILENAME: "
              + fi.getName());
          }

          mapFile.put(fieldName, fi);

        }
      }

      return mapFile;

    } catch (Exception e) {
      String msg = e.getMessage();
      log.error(msg, e);
      // No emprar ni 404 ni 403
      try {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg); // bad
                                                                     // request
      } catch (Exception ee) {
        log.error(ee.getMessage(), ee);
      }
      return null; // = ERROR
    }

  }
  
  
  public static File tempDir = null;
  
  protected synchronized File getTempDir() throws IOException {
    
    if (tempDir == null) {
      File temp = File.createTempFile("test", "test");
      
      tempDir = temp.getParentFile();
            
      if (!temp.delete()) {
        temp.deleteOnExit();
      }
    }
    
    return tempDir;
    
  }
  
  
 
  
  // ---------------------------------------------------------
  // ------------------- READ LOCAL RESOURCES  ---------------
  // ---------------------------------------------------------


  protected boolean retornarRecursLocal(String absolutePluginRequestPath,
      String relativePluginRequestPath, K key, String query,
      HttpServletRequest request, HttpServletResponse response, Locale languageUI) {
    byte[] contingut = null;
    String mime = getMimeType(query);
    query = query.replace('\\', '/');

    query = query.startsWith("/") ? query : ('/' + query);

    try {

      InputStream input = getClass().getResourceAsStream(query);

      if (input != null) {

        contingut = IOUtils.toByteArray(input);

        int pos = query.lastIndexOf('/');
        String resourcename = pos == -1 ? query : query.substring(pos + 1);
        
        OutputStream out = response.getOutputStream();
        

        response.setContentType(mime);
        response.setHeader("Content-Disposition", "inline; filename=\"" + resourcename + "\"");
        response.setContentLength(contingut.length);


        out.write(contingut);
        out.flush();

        return true;
      }
    } catch (IOException e) {
      log.error("Error llegint recurs " + query, e);
    }

    // ERROR:  "No trob el recurs " + query;
    String titol = getTraduccio(ABSTRACT_WEB_RES_BUNDLE,"notfound.resource", languageUI, query);

    requestNotFoundError(titol, absolutePluginRequestPath, relativePluginRequestPath, query,
        key, request, response, languageUI);
    
    return false;
  }

  protected String getMimeType(String resourcename) {
    String mime = "application/octet-stream";
    if (resourcename != null && !"".equals(resourcename)) {
      String type = resourcename.substring(resourcename.lastIndexOf(".") + 1);
      if ("jar".equalsIgnoreCase(type)) {
        mime = "application/java-archive";
      } else if ("gif".equalsIgnoreCase(type)) {
        mime = "image/gif";
      } else if ("cab".equalsIgnoreCase(type)) {
        mime = "application/octet-stream";
      } else if ("exe".equalsIgnoreCase(type)) {
        mime = "application/octet-stream";
      } else if ("pkg".equalsIgnoreCase(type)) {
        mime = "application/octet-stream";
      } else if ("msi".equalsIgnoreCase(type)) {
        mime = "application/octet-stream";
      } else if ("js".equalsIgnoreCase(type)) {
        mime = "text/javascript";
      } else if ("zip".equalsIgnoreCase(type)) {
        mime = "application/zip";
      } else if ("css".equalsIgnoreCase(type)) {
        mime = "text/css";
      } else if ("png".equalsIgnoreCase(type)) {
        mime = "image/png";
      }
    }
    return mime;
  }

 
  
  
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------
  // ------------------- GESTIO D'ERRORS ---------------------------------------
  // ---------------------------------------------------------------------------
  // ---------------------------------------------------------------------------

  public void requestTimeOutError(String absolutePluginRequestPath,
      String relativePluginRequestPath, String query, K key, HttpServletRequest request, 
      HttpServletResponse response, String titol) {
    String str = allRequestInfoToStr(request, titol, absolutePluginRequestPath,
        relativePluginRequestPath, query, key);

    // TODO Traduir
    // El procés  amb ID " + key  + " ha caducat. Torni a intentar-ho.\n";
    Locale locale = request.getLocale();

    String msg = getTraduccio(ABSTRACT_WEB_RES_BUNDLE, "timeout.error", locale, getName(locale), key.toString());

    log.error(msg + "\n" + str);

    // No emprar ni 404 ni 403
    try {
      response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, msg); // Timeout
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public void requestNotFoundError(String titol, String absolutePluginRequestPath,
      String relativePluginRequestPath, String query, K key, HttpServletRequest request, HttpServletResponse response,
      Locale locale) {
    String str = allRequestInfoToStr(request, titol, absolutePluginRequestPath,
        relativePluginRequestPath, query, key);
    // S'ha realitzat una petició al plugin [{0}] però no s'ha trobat cap mètode
    // per processar-la {1}
    String msg = getTraduccio(ABSTRACT_WEB_RES_BUNDLE, "notfound.error", locale, getName(locale),
        str);

    log.error(msg + "\n" + str);
    // No emprar ni 404 ni 403
    try {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, msg); // bad
                                                                   // request
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
  
  /**
   * 
   * @param response
   * @param signaturesSet
   * @param errorMsg
   * @param th
   */
  public abstract void finishWithError(HttpServletResponse response, V value,
      String errorMsg, Throwable th);

}
