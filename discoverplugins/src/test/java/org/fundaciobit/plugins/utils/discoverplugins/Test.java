package org.fundaciobit.plugins.utils.discoverplugins;

import java.util.Set;

import org.fundaciobit.plugins.exportdata.ExportData;
import org.fundaciobit.plugins.exportdata.ExportFile;
import org.fundaciobit.plugins.exportdata.IExportDataPlugin;

/**
 * 
 * @author anadal
 * 
 */
@TestAnnotation
public class Test implements IExportDataPlugin{

  @org.junit.Test
  public void test() {

    System.out.println();
  }
  
  
  
  public void test1() throws Exception {
    
    
    ClassLoader[] loader = new ClassLoader[] { 
        ClassLoader.getSystemClassLoader(),
        Thread.currentThread().getContextClassLoader()
    };
    DiscoverPlugins.classLoader = loader;
    
    DiscoverPlugins.searchPackages = new String[] { "es.caib"};
    
    Set<Class<?>> plugins;
    plugins = DiscoverPlugins.getTypesAnnotatedWith(TestAnnotation.class, loader);
    
    if (plugins.isEmpty()) {
      
      System.out.println("¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿ NO HI HA ANOTACIONS");
      
    } else {
      for (Class<?> class1 : plugins) {
        System.out.println("¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿ " + class1);
      }
    }
  }
  
  
  
  
  public static void main(String[] args) {
    
    try {
      
      Set<Class<? extends IExportDataPlugin>> conjunt;
      
      DiscoverPlugins.searchPackages = new String[] { "org." };
      
      conjunt = DiscoverPlugins.getPluginsByInterface(IExportDataPlugin.class);
      
      if (conjunt.isEmpty()) {
        System.out.println("ESTA BUIT");
      } else {
        for (Class<? extends IExportDataPlugin> class1 : conjunt) {
          System.out.println(" CLASS = " + class1.getName());
        }
      }
      
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
  }


  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public ExportFile getIcon() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public ExportFile getExportFile(ExportData exportData) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }
}
