import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * A facade to the JavaCompiler API. If the source file resides
 * in a package, be sure to specify the proper relative path 
 * to <code>createSourceFile</code>. E.g., if the source belongs
 * to the package some.folder, the relative path is 
 * <code>/some/folder</code>.
 */
public class JPlatform {

    private static JPlatform INSTANCE;
    private File log;
    private String outputDir;

    private JPlatform(String outputDir) {
        this.outputDir = outputDir;
        log = new File(outputDir + "/error.log");
    }
    
    /**
     * Get an instance of the compiler.
     * 
     * @param outputDir An absolute path where the source and class
     *                  files will be created.
     */
    public static JPlatform getInstance(String outputDir) {
        if (INSTANCE == null)
            INSTANCE = new JPlatform(outputDir);
        return INSTANCE;
    }

    public void setOutputDirectory(String outputDir) {
        this.outputDir = outputDir;
        log = new File(outputDir + "/error.log");
    }
    
    public String getOutputDirectory() {
        return this.outputDir;
    }

    /**
     * Create the source file.
     * 
     * @param file A relative path for the file to be created.
     *             The path is relative to the output directory.
     * @param source The source code.
     * 
     * @return The newly created source file.
     */
    public File createSourceFile(String file, String source) {
        File root = new File(outputDir);
        File sourceFile = new File(root, file);
        try {
            new FileWriter(sourceFile).append(source).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourceFile;
    }

    /**
     * Compile the source file.
     * 
     * @param source The source file. The class file is written to the
     *               same directory as the source file.
     * 
     * @return Returns <code>true</code> if the compilation succeeded, otherwise
     *         returns <code>false</code>.
     */
    public boolean compile(File source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        int success = 1;
        try {
            success = compiler.run(null, null, new FileOutputStream(log),
                    source.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return (success == 0) ? true : false;
    }
    
    /**
     * Create a new instance of the specified class.
     * 
     * @param qualifiedClass The fully-qualified class name.
     * 
     * @return A new instance of the specified class.
     * 
     * @throws Exception If an error occurs.
     */
    public Object newInstance(String qualifiedClass) throws Exception {
        File outputDirFile = new File(outputDir);
        URLClassLoader classLoader = URLClassLoader
                .newInstance(new URL[] { outputDirFile.toURI().toURL() });
        Class<?> cls = Class.forName(qualifiedClass, true, classLoader);
        return cls.newInstance();
    }

    /**
     * Invoke the specified method on the given instance with the specified arguments.
     * 
     * @param instance The instance to invoke the method on.
     * @param method The method to be invoked.
     * @param args The arguments to be passed to the method.
     * 
     * @return The return value of the invoked method.
     * 
     * @throws Exception If the method couldn't be found or an error occurs.
     */
    public Object invokeMethod(Object instance, String method,
            Object... args) throws Exception {
        Class<?> cls = instance.getClass();
        for (Method m : cls.getDeclaredMethods()) {
            if (m.getName().equals(method)) {
                return m.invoke(instance, args);
            }
        }
        throw new Exception("Couldn't find the declared method with that name.");
    }

    /**
     * Return the compiler error if <code>compile</code> fails.
     * 
     * @return The compiler error or <code>null</code> if no error is found.
     */
    public String getCompileError() {
        StringBuilder error = new StringBuilder();
        try {
            Scanner scnr = new Scanner(log);
            if(!scnr.hasNextLine())
                return null;
            String errorLine = scnr.nextLine();
            String[] tokens = errorLine.split(" ");
            for(int i = 1; i < tokens.length; i++)
                error.append(tokens[i]+" ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return error.toString();
    }
}
