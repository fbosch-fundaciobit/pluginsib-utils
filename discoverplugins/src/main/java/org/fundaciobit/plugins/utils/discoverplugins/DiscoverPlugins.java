package org.fundaciobit.plugins.utils.discoverplugins;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fundaciobit.plugins.IPlugin;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.Vfs;





/**
 * 
 * @author anadal
 *
 */
public class DiscoverPlugins {
  

  public static ClassLoader[] classLoader = null;
  

  public static String[] searchPackages = null;

  /**
   * Cache de classes utilitzat pel mètode getPluginsOf()
   */
  private static final Map<Class<?>,Object> classesCache = new HashMap<Class<?>, Object>();

  private static Reflections reflectionsCache = null;
  
  private static Reflections getReflections() {
    if (reflectionsCache == null) {

      Vfs.addDefaultURLTypes(new UrlTypeVFS2());
      
      if (searchPackages == null && classLoader == null) {
          reflectionsCache = new Reflections("");        
      } else {
        int cl = classLoader == null ? 0 : classLoader.length;
        int sp = searchPackages == null? 0 : searchPackages.length;
        
        Object[] config = new Object[sp + cl];
        if (searchPackages != null) {
          for (int j = 0; j < searchPackages.length; j++) {
            config[j] = searchPackages[j];
          }
        }
        if (classLoader != null) {
          for (int j = 0; j < classLoader.length; j++) {
            config[sp + j] = classLoader[j];
          }
        }
        reflectionsCache = new Reflections(config);
      }
    }
    return reflectionsCache;
  }
  
  public static <T extends Object> Set<Class<? extends T>> getSubTypesOfInterface(
      Class<T> interficie) throws Exception {

    Set<Class<? extends T>> implementations;
    implementations = (Set<Class<? extends T>>) classesCache.get(interficie);

    if (implementations == null) {
      implementations = new HashSet<Class<? extends T>>();
      Reflections ref = getReflections();
      Set<Class<? extends T>> tmp = ref.getSubTypesOf(interficie);
      implementations.addAll(tmp);
      classesCache.put(interficie, implementations);
    }

    return implementations;
  }

  
  public static Set<Class<?>> getTypesAnnotatedWith(
      Class<? extends Annotation> annotation, ClassLoader ... classLoaders) throws Exception {

    Set<Class<?>> implementations;
    implementations = (Set<Class<?>>) classesCache.get(annotation);

    ConfigurationBuilder cb = new ConfigurationBuilder().setUrls(ClasspathHelper.forPackage("es.caib"))
       .setScanners(new SubTypesScanner(),new TypeAnnotationsScanner());
    
    if (classLoaders != null && classLoaders.length != 0) {
      cb.addClassLoaders(classLoaders);
    }
    
    if (implementations == null) {
      implementations = new HashSet<Class<?>>();
      Reflections ref = new Reflections(cb);

      
      Set<Class<?>> tmp = ref.getTypesAnnotatedWith(annotation);
      implementations.addAll(tmp);
      classesCache.put(annotation, implementations);
    }

    return implementations;
  }

  
  
  public static <T extends IPlugin> Set<Class<? extends T>> getPluginsByInterface(Class<T> interficie)
      throws Exception {

    Set<Class<? extends T>> implementations;
    implementations = (Set<Class<? extends T>>) classesCache.get(interficie);

    if (implementations == null) {
      implementations = new HashSet<Class<? extends T>>();
      Reflections ref = getReflections();
      Set<Class<? extends T>> tmp = ref.getSubTypesOf(interficie);
      implementations.addAll(tmp);
      classesCache.put(interficie, implementations);
    }

    return implementations;
  }
   
  
}