import java.io.File;
import java.util.Scanner;

public class JInterpreterDemo {

    private static String tmpLocation;
    private static JInterpreter interpreter;

    public static void main(String[] args) {
        System.out.print("Enter a valid temporary "
                + "file directory (that you have access permissions for): ");
        Scanner kbd = new Scanner(System.in);
        tmpLocation = kbd.nextLine();
        if (!isFilePathValid(tmpLocation)) {
            System.out.println("Invalid file location. Quitting.");
            System.exit(1);
        }
        interpreter = JInterpreter.getInstance(tmpLocation);
        System.out.print("Run in interactive mode (Y/N): ");
        String choice = kbd.nextLine();
        if (choice.equalsIgnoreCase("Y"))
            runInteractiveMode();
        else if(choice.equalsIgnoreCase("N"))
            runStaticMode();
        else
            System.out.println("Invalid choice. Quitting.");
    }

    private static boolean isFilePathValid(String path) {
        return new File(path).canWrite();
    }

    private static void runInteractiveMode() {
        showUsage();
        Scanner kbd = new Scanner(System.in);
        String command;
        try {
            do {
                System.out.print("> ");
                command = kbd.nextLine().trim();
                if (command.equals("quit"))
                    break;
                else if (command.equals("reset"))
                    interpreter.reset();
                else if (command.equals("run"))
                    interpreter.run();
                else
                    interpreter.newStatement(command);
            } while (true);
            System.out.println("Bye!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showUsage() {
        System.out
                .println("\nEnter a valid Java statement "
                        + "or import statement. \nNo return or package statements, field, "
                        + "method, or top-level class declarations are allowed."
                        + "\nLocal class declarations and anonymous classes are allowed!"
                        + "\nYou can enter an import statement at any point."
                        + "\nException-handling is taken care of for you."
                        + "\nEnter 'run' to run the interpreter, 'reset' to reset it, or 'quit' to quit."
                        + "\nNote: You must enter all statements to be interpreted *before* running."
                        + "\nAfter running the interpreter all statements are lost.");
        System.out
                .println("\nSome examples of valid interpreter statements: \n\n"
                        + "import java.util.*;\n"
                        + "int i = 0;\n"
                        + "if(Math.pow(2, 2) == 4.0)System.out.println(\"Equal\");\n"
                        + "int i = 0 ; if( i < 100) System.out.println(\"Nice!\");\n"
                        + "class HelloWorld { void hello() { System.out.println(\"Hello!\"); } } new HelloWorld().hello(); \n");
    }

    private static void runStaticMode() {
        System.out
                .println("\nThe following output was produced from interpreted "
                        + "Java statements sent in from a client of the Interpreter class.\n");
        try {
            interpreter.newStatement("int i = 10;");
            interpreter.newStatement("System.out.println(i);");
            interpreter
                    .newStatement("if(i < 100)System.out.println(\"i is less than 100\");");
            interpreter.newStatement("for(int r = 0; r < 5; r++)");
            interpreter.newStatement("System.out.println(r);");
            interpreter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
