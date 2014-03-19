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