import java.io.File;

/**
 * A Java statement interpreter. Add a list of statements with
 * <code>newStatement</code> and run them with
 * <code>run</code>. After running the interpreter, all
 * previously added statements are forgotten.
 */
public class JInterpreter {

    private static JInterpreter INSTANCE;
    private JPlatform compiler;
    private StringBuilder source = new StringBuilder();
    private StringBuilder imports = new StringBuilder();

    private JInterpreter(String outputDir) {
        compiler = JPlatform.getInstance(outputDir);
    }

    /**
     * Get an instance of the interpreter.
     *
     * @param outputDir An absolute path where support
     *                  files will be created. They aren't
     *                  deleted on exit.
     */
    public static JInterpreter getInstance(String outputDir) {
        if (INSTANCE == null)
            INSTANCE = new JInterpreter(outputDir);
        INSTANCE.reset();
        return INSTANCE;
    }

    public void setOutputDirectory(String outputDir) {
        compiler.setOutputDirectory(outputDir);
    }

    public String getOutputDirectory() {
        return compiler.getOutputDirectory();
    }

    /**
     * Reset the interpreter to a fresh state.
     */
    public void reset() {
        source.delete(0, source.length());
        imports.delete(0, imports.length());
    }

    private void beginSource() {
        source.insert(0, "public class Runner { public void run() { try { ");
        source.insert(0, imports.toString());
    }

    private void endSource() {
        source.append(" }catch(Exception e){ e.printStackTrace(); } } }");
    }

    /**
     * Add a new statement to the interpreter.
     *
     * @param statement A valid Java statement other than a return statement.
     * @throws Exception If a return statement is added.
     */
    public void newStatement(String statement) throws Exception {
        if (statement.startsWith("return"))
            throw new Exception("No return statements allowed.");
        else if (statement.startsWith("import"))
            newImportStatement(statement);
        else
            source.append(statement);
    }

    private void newImportStatement(String statement) {
        imports.append(statement);
    }

    /**
     * Run the statements given to the interpreter.
     *
     * @return Returns <code>true</code> if the run was successful, otherwise
     * returns <code>false</code>.
     * @throws Exception If an error occurs.
     */
    public boolean run() throws Exception {
        beginSource();
        endSource();
        File sourceFile = compiler.createSourceFile("Runner.java",
                source.toString());
        if (compiler.compile(sourceFile)) {
            Object instance = compiler.newInstance("Runner");
            compiler.invokeMethod(instance, "run", null);
            reset();
            return true;
        }
        reset();
        System.out.println(compiler.getCompileError());
        return false;
    }
}
