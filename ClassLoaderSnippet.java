/** Gets a list of all classes in a specific package. An example class must be provided to get the
     *   correct classloader.*/
 public static List<Class> getClassList(Class exampleClass, String packagePath) {
  String packageFilePath = packagePath.replace('.', '/');
  
  try
  {
   Enumeration<URL> urls = exampleClass.getClassLoader().getResources(packageFilePath);
   List<File> classDirectories = new ArrayList<File>();
   
   for (URL url;urls.hasMoreElements();)
   {
    url=urls.nextElement();
    classDirectories.add(new File(URLDecoder.decode(url.getFile(), "UTF-8")));
   }
   
   List<Class> foundClasses = new ArrayList<Class>();
   for (File classDirectory : classDirectories) {
    for (File classFile : classDirectory.listFiles())
    {
     if (classFile.isFile() && classFile.getName().endsWith(".class") && !classFile.getName().contains("$")) {
      foundClasses.add(Class.forName(packagePath + "."+classFile.getName().replace(".class", "")));
     }
    }
   }

   return foundClasses;
  } catch (Exception e)
  {
   Error e2 = new InstantiationError("Failed loading suborder classes");
   e2.initCause(e);
   throw e2;
  }
  
 }
 
 /******************* OR ****************************************/
 
 
 
     /** Add in all classes as handlers for the given package */
    private List<Class<?>> getClasses(String pckg)
    {
        List<Class<?>> handlers = new ArrayList<Class<?>>();
        URL root = Thread.currentThread().getContextClassLoader().getResource(pckg.replace(".", "/"));
        String rootPath = root.getFile();
        List<String> classes;
        
        if (rootPath.contains("!"))
        {
            classes = getClassNamesFromJar(root);
        } else {
            classes = getClassNamesFromDirectory(pckg, rootPath);
        }

        ClassLoader loader = this.getClass().getClassLoader();
        
        for (String className : classes)
        {
            try
            {
                Class<?> cls = loader.loadClass(className);
    
                if (AppRequestManager.class.isAssignableFrom(cls)) {
                    handlers.add(cls);
                }
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        
        return handlers;
    }
    
    /** Look up the basic directory structure and pull out all classes that are in the directory structure.*/
    private List<String> getClassNamesFromDirectory(String pckg, String rootPath)
    {
        ArrayList<String> classes = new ArrayList<String>();
        
        File[] files = new File(rootPath).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });

        for (File file : files) 
        {
            String className = file.getName().replaceAll(".class$", "");

            classes.add(pckg + "." + className);
        }

        return classes;
    }

    /** Look into the JAR and pull out all entries for the given directory*/
    private List<String> getClassNamesFromJar(URL jarDirectory) 
    {
        ArrayList<String> classes = new ArrayList<String>();
        try 
        {
            JarURLConnection jarConnection = (JarURLConnection)jarDirectory.openConnection();
            JarFile jar = jarConnection.getJarFile();
            try 
            {
                String jarPath = jarConnection.getEntryName();
                
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(jarPath) && !entry.isDirectory()) 
                    {
                        String className = entry.getName().replace("/", ".").replace(".class","");
                        classes.add(className);
                    }
                }
            }
            finally 
            {
                try { jar.close(); } catch (Exception e) {}
            }

            return classes;
        } catch (IOException e) 
        {
            throw new RuntimeException("Can't open Jar", e);
        }
    }
 
 
