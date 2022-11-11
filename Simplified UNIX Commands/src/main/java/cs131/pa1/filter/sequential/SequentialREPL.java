package cs131.pa1.filter.sequential;

import java.io.FileNotFoundException;
import java.util.*;
//import java.util.Scanner;

import cs131.pa1.filter.Message;

/**
 * The main implementation of the REPL loop (read-eval-print loop). It reads
 * commands from the user, parses them, executes them and displays the result.
 * 
 * @author cs131a
 *
 */
public class SequentialREPL {
	/**
	 * the path of the current working directory
	 */
	static String currentWorkingDirectory;

	/**
	 * The main method that will execute the REPL loop
	 * 
	 * @param args not used
	 * @throws FileNotFoundException 
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		System.out.print(Message.WELCOME);
		int temp = 0;
		currentWorkingDirectory = System.getProperty("user.dir");
		Scanner console = new Scanner(System.in);
		while(temp == 0) {
			System.out.print(Message.NEWCOMMAND); //get the new command
			String s = console.nextLine();
			if(s.equals("exit")) { //if exit then leave
				temp = 100;
				console.close();
				break;
			}
			else if(!s.trim().equals("")){
				List<SequentialFilter> lFil = null;
				try {
					lFil = SequentialCommandBuilder.createFiltersFromCommand(s); //begin the command builder from subcommand
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(lFil != null) {
					
					for (SequentialFilter fil : lFil) {
						if (s.contains("|") || s.contains(">")) { //as to not repeat
							break;
						}
						fil.process(); //process main commands
						break;
					}
				}
			}
		}
		//console.close();
		System.out.print(Message.GOODBYE);
	}

}
