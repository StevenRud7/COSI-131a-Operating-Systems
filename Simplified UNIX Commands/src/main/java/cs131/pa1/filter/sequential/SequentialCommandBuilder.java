package cs131.pa1.filter.sequential;
import cs131.pa1.filter.Message;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class manages the parsing and execution of a command. It splits the raw
 * input into separated subcommands, creates subcommand filters, and links them
 * into a list.
 * 
 * @author cs131a Steven Rud
 *
 */
public class SequentialCommandBuilder {
	
	static String aCommands = "cd pwd ls cat grep wc uniq head tail >"; //used to check for not commands
	

	/**
	 * Creates and returns a list of filters from the specified command
	 * 
	 * @param command the command to create filters from
	 * @return the list of SequentialFilter that represent the specified command
	 * @throws Exception 
	 */
	public static List<SequentialFilter> createFiltersFromCommand(String command) throws Exception {
//		String cutCommand = adjustCommandToRemoveFinalFilter(command);
		String[] splitA = command.split("\\|"); //splits the command into required parts
		List<SequentialFilter> inputs = new LinkedList<SequentialFilter>();
		SequentialFilter finalF = determineFinalFilter(command); //call the finalfilter to eventually go into the command class
		int count = 0;
		while (count < splitA.length-1) { //add values into the input
			SequentialFilter seqF = constructFilterFromSubCommand(splitA[count].trim());
			inputs.add(seqF);
			count++;
			
		}
		inputs.add(finalF);
		return inputs;
	}

	/**
	 * Returns the filter that appears last in the specified command
	 * 
	 * @param command the command to search from
	 * @return the SequentialFilter that appears last in the specified command
	 * @throws Exception 
	 */
	private static SequentialFilter determineFinalFilter(String command) throws Exception {
		SequentialFilter s = constructFilterFromSubCommand(command); //go through each sub command and eventually go to command class
		return s;
	}

	/**
	 * Returns a string that contains the specified command without the final filter
	 * 
	 * @param command the command to parse and remove the final filter from
	 * @return the adjusted command that does not contain the final filter
	 */
	private static String adjustCommandToRemoveFinalFilter(String command) {
		String[] commandTrim = command.trim().split("\\|");
		String adjustC = "";
		
		if (commandTrim.length == 1) {
			return "";
		}
		for (int i =0; i<commandTrim.length - 1; i++) {
			adjustC += commandTrim[i] + "|";
		}
		
		if (adjustC.length() != 0) {
			adjustC = adjustC.substring(0,(adjustC.length()-1)).trim();
		}
		return adjustC;
	}

	/**
	 * Creates a single filter from the specified subCommand
	 * 
	 * @param subCommand the command to create a filter from
	 * @return the SequentialFilter created from the given subCommand
	 * @throws Exception 
	 */
	private static SequentialFilter constructFilterFromSubCommand(String subCommand) throws Exception { //go through subcommand
		String[] c =subCommand.trim().split(" ");
		int pipec = 0;
		//System.out.println(subCommand);
		//System.out.println(c[0]);
		SequentialFilter seqFil = null;
		if (c.length == 1) { //if a single not present command create error
			if (!aCommands.contains(c[0])) {
				System.out.printf(Message.COMMAND_NOT_FOUND.with_parameter(c[0]));
				throw new Exception();
			}
		}
		for (int i = 0; i<c.length; i++) {
			if (c[i].equals("|")) {
				pipec = i;
				if (!aCommands.contains(c[i+1])) { //if the last command is not a real command throw and exception
					if (pipec +1 == c.length-1) {
						System.out.printf(Message.COMMAND_NOT_FOUND.with_parameter(c[i+1]));
						throw new Exception();
					} 
					else { //if there are more commands after the failed command so we can continue inputting
						System.out.printf(Message.COMMAND_NOT_FOUND.with_parameter(subCommand.substring(c[0].length()+3)));
						return seqFil;
					}
				}
			}
		}
		//go through each subcommand and if it is equal go into the command class with it
		if (c[0].equals("pwd")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("ls")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("cd")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("cat")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("head")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("tail")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("grep")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("wc")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals("uniq")) {
			seqFil = new commandClass(subCommand);
		}
		if (c[0].equals(">")) {
			seqFil = new commandClass(subCommand);
		}
		return seqFil;
	}

	/**
	 * links the given filters with the order they appear in the list
	 * 
	 * @param filters the given filters to link
	 * @return true if the link was successful, false if there were errors
	 *         encountered. Any error should be displayed by using the Message enum.
	 */
	private static boolean linkFilters(List<SequentialFilter> filters) { //didn't end up using cause I just used one class for all commands
		return false;
	}

}
