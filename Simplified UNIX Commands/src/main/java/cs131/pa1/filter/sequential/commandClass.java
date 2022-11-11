package cs131.pa1.filter.sequential;
import cs131.pa1.filter.Filter;
import cs131.pa1.filter.Message;

import java.io.*;
import java.util.*;

/**
 * 
 * This class goes through command and its possible scenarios 
 * 
 * @author Steven Rud cs131a
 *
 */

public class commandClass extends SequentialFilter {
	
	int lsCount;						//ALL VARIABLES USED THROUGHOUT 
	private String line;				// parts of variable represent what it was used in (cdS for cd, usc for uniq scanner etc)
	static String commandS = "";
	String[] cdL;
	String cdS;
	private Scanner sc;
	private Scanner hsc;
	private Scanner tsc;
	private Scanner gsc;
	private Scanner wsc;
	private Scanner usc;
	private Scanner rsc;
	File ls;
	boolean cdB = true;
	String[] lsL;
	public int wcL;
	public int wcW;
	public int wcC;
	public String grepS;
	public HashSet<String> uniqS;
	public int headCount;
	Stack<String> tailL;
	String[] tailLFlip;
	int tailCount;
	String[] c2;
	boolean CHpipe;
	
	
	public commandClass(String line) throws Exception {
		super();
		input = new LinkedList<String>();
		output = new LinkedList<String>();

		commandS = line;
		//System.out.println(commandS);
		
		//System.out.println(c2[0]+c2[1]);
		
		if (commandS.equals("pwd")) { //if simple pwd just prints cwd
			//System.out.println(commandS);
			System.out.println(SequentialREPL.currentWorkingDirectory);
		}
		if (commandS.equals("ls")) { //if simply ls puts all values in a list and print in process
			ls = new File(SequentialREPL.currentWorkingDirectory);
			lsL= ls.list();
			//System.out.println(lsL[0]);
		}
		if (commandS.substring(0,2).equals("cd")) { //if starts with cd gets dealt with in process more specifically
			c2 = commandS.trim().split(" ");
			//System.out.println(c2[1]);
			//System.out.println("c2");
			this.line = line;
			if (commandS.contains("|")) { //if cd is piped makes error
				System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(c2[0]+" "+ c2[1]));
				throw new Exception();
			}
		}
		//commandS.substring(0,2).equals("ca")
		if (commandS.contains("cat") && !commandS.contains("head") && CHpipe != true && !commandS.contains(">")) { //if just cat
			String[] catC = line.split(" ");
			//System.out.println(catC[1]);
			if(catC.length == 1) { //if not given parameter cause error
				System.out.printf(Message.REQUIRES_PARAMETER.toString(),line);
				throw new Exception();
			}
			else if (commandS.contains("|")) { //find where the pipe is if there is one
				int pipepos = 0;
				for (int p = 0; p<catC.length; p++) {
					if (catC[p].equals("|")) {
						pipepos = p;
						break;
					}
				}
				
				//if cd pipes into any of these cause error as not possible
				if (catC[pipepos+1].equals("cat")) {
					System.out.printf(Message.CANNOT_HAVE_INPUT.toString(),line.substring(6));
					throw new Exception();
				}
				if (catC[pipepos+1].equals("cd")) {
					System.out.printf(Message.CANNOT_HAVE_INPUT.with_parameter(catC[catC.length-2] + " " + catC[catC.length-1]));
					throw new Exception();
				}
				if (catC[pipepos+1].equals("ls")) {
					System.out.printf(Message.CANNOT_HAVE_INPUT.with_parameter(catC[catC.length-1]));
					throw new Exception();
				}
				if (catC[pipepos+1].equals("pwd")) {
					System.out.printf(Message.CANNOT_HAVE_INPUT.with_parameter(catC[catC.length-1]));
					throw new Exception();
				}
			}
			else { //if no pipe just cd to the specified directory
				String catS = catC[1];
				//System.out.println(catS);
				catS = "\\"+ catS;
				catS = SequentialREPL.currentWorkingDirectory + catS;
				File catF = new File(catS);
				//System.out.println(catF);
				try {
					sc = new Scanner(catF);
				} 
				catch(FileNotFoundException fex) { //error if directory not found
					System.out.printf(Message.FILE_NOT_FOUND.toString(),line);
					throw new Exception();
					
				}
				
			}
		}
		//for cat or ls when they pipe into head
		if ((commandS.contains("cat") || commandS.contains("ls")) && commandS.contains("|") && commandS.contains("head") && !commandS.contains(">")) {
			headCount = 0;
			CHpipe = true;
			String[] headSp = line.split(" ");
//			for (int i = 0; i<headSp.length; i++) {
//				System.out.println(headSp[i]);
//			}
			if(headSp[1] == null) { //error when no parameter given
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(line));
				return;
			}
			String head1 = headSp[1]; //add the document from a file and scan through it to print out the first 10 values
			head1 = "\\"+ head1;
			head1 = SequentialREPL.currentWorkingDirectory + head1;
			File headF = new File(head1);
			//System.out.println(headF);
			hsc = new Scanner(headF);
			while(hsc.hasNextLine() && headCount <10) { 
				headCount++;
				String headSc = hsc.nextLine();
				output.add(headSc);
				System.out.println(headSc);
			}
			hsc.close();
		}
		
		if (commandS.equals("head")) { //if head alone then error
			System.out.print(Message.REQUIRES_INPUT.with_parameter(line));
			throw new Exception();
		}
		
		//for when cat is piped into tail
		if (commandS.contains("cat") && commandS.contains("|") && commandS.contains("tail") && !commandS.contains(">")) {
			tailL = new Stack<String>();
			tailLFlip = new String[10];
			
			tailCount = -1;
			String[] tailSp = line.split(" ");
			if(tailSp[1] == null) { //error when no parameter given
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(line));
				return;
			}
			String tail1 = tailSp[1]; //same as head put document into file
			tail1 = "\\"+ tail1;
			tail1 = SequentialREPL.currentWorkingDirectory + tail1;
			File tailF = new File(tail1);
			//System.out.println(tailF);
			tsc = new Scanner(tailF);
			while(tsc.hasNextLine()) {  //add values to stack this time so we can pop out the last 10 
				String tailSc = tsc.nextLine();
				tailL.push(tailSc);
			}
			tsc.close();
			if (tailL.size() == 0) { //if empty doc
				return;
			}
			for (int i = 0; i<10; i++) {
				if (tailL.isEmpty() == true) {// if less than 10 values then break out early
					i=100;
					break;
				}
				tailLFlip[i] = (tailL.pop());
				tailCount++;
			}
			int tailCount2 = tailCount;
			//System.out.println(tailLFlip[1]);
			for (int x = 0; x <= tailCount2; x++) {
				if (tailLFlip[tailCount] != null) { //go through flipped list and print it to get tail values
					output.add(processLine(tailLFlip[tailCount]));
					System.out.println(tailLFlip[tailCount]);
					tailCount--;
				}
				else {
					break;
				}
			}
		}
		
		if (commandS.equals("tail")) { //if just tail then error
			System.out.print(Message.REQUIRES_INPUT.with_parameter(line));
			throw new Exception();
		}
		
		//if cat, ls, or pwd pipe into grep
		if ((commandS.contains("cat") || commandS.contains("ls") || commandS.contains("pwd")) && commandS.contains("|") && commandS.contains("grep") && !commandS.contains(">")) {
			String[] grepC = line.split(" ");
			int grepCount = 0;
			if (grepC.length <= 1) { // if not given parameter then error
				System.out.printf(Message.REQUIRES_PARAMETER.toString(), line);
			}
			if(grepC[1] == null) { //same
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(line));
				return;
			}
			//indexes where the grep is in the full command
			for (int x =0; x<grepC.length; x++) {
				if (grepC[x].equals("grep")) {
					grepCount = x;
					break;
				}
			}
			if (commandS.contains("pwd")) { // error for when pwd is piped into grep without parameter
//				System.out.println(grepCount);
//				System.out.println(grepC.length);
				if (grepCount == grepC.length-1) {
					System.out.printf(Message.REQUIRES_PARAMETER.toString(), "grep");
					throw new Exception();
				}
			}
			if (commandS.contains("cat")) { //goes through similar cat process but only prints value if it matches the grep param
				String grep1 = grepC[1];
				grep1 = "\\"+ grep1;
				grep1 = SequentialREPL.currentWorkingDirectory + grep1;
				//System.out.println(grep1);
				File grepF = new File(grep1);
				gsc = new Scanner(grepF);

				//System.out.println(grepCount);
				while(gsc.hasNextLine()) { 
					String grepSc = gsc.nextLine();
					if (grepSc.contains(grepC[grepCount+1])) { //check for match
						System.out.println(grepSc);
					}
				}
				gsc.close();
			}
			if (commandS.contains("ls")) {  //goes through similar ls process but only prints value if it matches the grep param
				File gls = new File(SequentialREPL.currentWorkingDirectory);
				String[] glsL= gls.list();
				for (int g =0; g<glsL.length; g++) {
					if (glsL[g].contains(grepC[grepCount+1])) {
						System.out.println(glsL[g]);
					}
				}
			}

		}
		
		//if ls redirects into something
		if (commandS.substring(0,2).equals("ls") && commandS.contains(">")) {
			String[] lsSp3 = line.split(" ");
			int rPos = commandS.indexOf(">");
			//System.out.println(lsSp3.length);
			if (rPos > lsSp3.length) { //if it doesn't redirect into anything create error
				System.out.printf(Message.REQUIRES_PARAMETER.with_parameter(lsSp3[lsSp3.length-1]));
				throw new Exception();
			}
			File rels = new File(SequentialREPL.currentWorkingDirectory); //create file with the ls values
			int reLsPos = commandS.indexOf(">");
			String commandSls = commandS.substring(reLsPos-1).trim();

			String[] redLsSp =commandSls.split(" ");
			String[] redLsSpOG =commandS.split(" ");
			String[] relsL= rels.list();
			//File redLsFOG= new File(SequentialREPL.currentWorkingDirectory+Filter.FILE_SEPARATOR,redLsSpOG[1]);
			//System.out.println(redLsSp[redLsSp.length-1]);
			FileWriter FWred = new FileWriter(SequentialREPL.currentWorkingDirectory+Filter.FILE_SEPARATOR+redLsSp[relsL.length-1]);
			//rsc = new Scanner(redFOG);
			for (int rls = 0; rls<relsL.length; rls++) {  //go through the file and write the values into the new doc
				output.add(relsL[rls]);
				//System.out.println(relsL[rls]);
				FWred.write(relsL[rls] + "\n");
			}
			FWred.close();

		}
		
		//if starts with grep alone create error as it can't begin
		if (commandS.substring(0,2).equals("gr")) {
			System.out.print(Message.REQUIRES_INPUT.with_parameter(line));
			throw new Exception();
		}
		
		
		if (!commandS.contains("|") && commandS.contains("wc") && !commandS.contains(">")) { //if piped into wc
			String[] wcSp2 = line.split(" ");
			if(wcSp2.length == 1) { //if wc alone cause error
				System.out.printf(Message.REQUIRES_INPUT.with_parameter(line));
				throw new Exception();
			}
		}
		
		if (commandS.contains("|") && commandS.contains("wc") && !commandS.contains(">")) { //if piped into wc
			wcL =0; wcW = 0; wcC = 0;
			String[] wcSp = line.split(" ");
			String wc1 = wcSp[1];
			wc1 = "\\"+ wc1;
			wc1 = SequentialREPL.currentWorkingDirectory + wc1; //get cwd with the new file
			//System.out.println(wc1);
			File wcF = new File(wc1);
			wsc = new Scanner(wcF);
			while (wsc.hasNextLine()) { //scan through and add all word, line, and char counts
				String wcnext = wsc.nextLine();
				for (String w: wcnext.split(" ")) {
					if(w.length() > 0) { 
						wcW += 1;
					}
				}
				wcC += wcnext.length();
				wcL++; 
				
			}
			wsc.close();
			output.add(wcL + " " + wcW + " " + wcC);
			System.out.println(wcL + " " + wcW + " " + wcC); //print them
			
		}
		
		if (commandS.contains("|") && commandS.contains("uniq")) { //when piped into uniq
			uniqS = new LinkedHashSet<String>(); //use a linkedhashset to remove duplicates and still keep inserted order
			String[] uSp = line.split(" ");
			String u1 = uSp[1];
			u1 = "\\"+ u1;
			u1 = SequentialREPL.currentWorkingDirectory + u1;
			//System.out.println(u1);
			File uF = new File(u1);
			usc = new Scanner(uF); //scan through and add to uniqS
			while (usc.hasNextLine()) {
				String unext = usc.nextLine();
				uniqS.add(unext);
			}
			usc.close();
			Iterator uniqI = uniqS.iterator(); //iterate through uniqS and print
			while (uniqI.hasNext()) {
				System.out.println(uniqI.next());
			}
		}
		
		//if uniq alone thats an error
		if (commandS.equals("uniq")) {
			System.out.print(Message.REQUIRES_INPUT.with_parameter(line));
			throw new Exception();
		}
		
		
		if (commandS.contains(">") && commandS.contains("cat")) { // if cat is redirected
			int redPos = commandS.indexOf(">"); //find > index
			String commandS2 = commandS.substring(redPos-1).trim();

			String[] redSp =commandS2.split(" ");
			String[] redSpOG =commandS.split(" ");
			if (commandS.contains("|")) { //if it contains a pipe after redirect create an error
				int pPos = commandS.indexOf("|");
				if (pPos > redPos) {
					System.out.print(Message.CANNOT_HAVE_OUTPUT.with_parameter(commandS2.substring(0,commandS2.length()-3)));
					throw new Exception();
				}
			}

			//System.out.println(redSp);
			if(redSp.length<=1) { //if no parameter given also error
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(commandS2));
				throw new Exception();
			}
			//create all files like ls redirect. Then scan through it and add to new file 
			//File redF= new File(SequentialREPL.currentWorkingDirectory+Filter.FILE_SEPARATOR,redSp[redSp.length-1]); 
			File redFOG= new File(SequentialREPL.currentWorkingDirectory+Filter.FILE_SEPARATOR,redSpOG[1]);
			//System.out.println(redFOG);
			FileWriter FWred = new FileWriter(SequentialREPL.currentWorkingDirectory+Filter.FILE_SEPARATOR+redSp[redSp.length-1]);
			rsc = new Scanner(redFOG);
			while(rsc.hasNextLine()) { 
				String redSc = rsc.nextLine();
				output.add(redSc);
				//System.out.println(redSc);
				FWred.write(redSc+ "\n");
			}
			rsc.close();
			FWred.close();
			
		}
		
		//if redirect alone then error
		if (commandS.substring(0,1).equals(">")) {
			System.out.print(Message.REQUIRES_INPUT.with_parameter(line));
			throw new Exception();
		}
		
	}
	
	
	/**
	 * processLine for the main general subcommands
	 */
	@Override
	protected String processLine(String line) {
		if (commandS.equals("pwd")) {
			return SequentialREPL.currentWorkingDirectory; //returns cwd for just pwd
		}
		if (commandS.substring(0,2).equals("cd")) { //boolean to use for cd start
			if (cdB == false) {
				return "False";
			}
			else {
				return "True";
			}
		}
		
		return commandS;
	}
	
	/**
	 * process method for dealing with simple commands 
	 */
	public void process() {
		//System.out.println(commandS);
		if (commandS.equals("pwd")) { //if simply pwd just call to current directory and print
			output.add(processLine(""));
		}
		if (commandS.equals("ls")) { // if simply ls just print the ls 
			for (lsCount =0; lsL.length > lsCount; lsCount++) {
				output.add(processLine(""));
			}
			for (int i = 0; i<lsL.length; i++) { //print here
				System.out.println(lsL[i]);
			}
		  }
		if (commandS.substring(0,2).equals("cd")) { //if starts with cd
			int cdCount = SequentialREPL.currentWorkingDirectory.length()-1;
			String cwdS =SequentialREPL.currentWorkingDirectory;
			if(c2.length==1) { //if cd alone then throw error and require parameter
				cdB = false;
				System.out.print(Message.REQUIRES_PARAMETER.with_parameter(line));
				return;
			}
			if(c2[1].equals("..")) { //for going back a directory like actual UNIX
				while (cdCount >=0 ) {
					if(FILE_SEPARATOR.equals(SequentialREPL.currentWorkingDirectory.substring(cdCount,cdCount+1))) {
						SequentialREPL.currentWorkingDirectory=SequentialREPL.currentWorkingDirectory.substring(0,cdCount); //push back cwd
						cdCount--;
						break;
					}
					cdCount--;
				}
				
			}
			else if (c2[1].equals(".")) { //don't move directory
				return;
			}
			else { //for cd'ing to a specific directory
				cwdS+=FILE_SEPARATOR+c2[1];
				File cdF =new File(cwdS);
				if (!cdF.isDirectory()) { //if directory not found then error
					cdB = false;
					System.out.print(Message.DIRECTORY_NOT_FOUND.with_parameter(line)); 
					return;
				}
				SequentialREPL.currentWorkingDirectory+=FILE_SEPARATOR+c2[1]; //if present moves it
				
			}

		}
		
		if (commandS.substring(0,2).equals("ca") && !commandS.contains(">")) { //for a simple cat
			//System.out.println(commandS);
			while(sc.hasNextLine()) { // it goes through the document and prints everything
				String catSc = sc.nextLine();
				output.add(catSc);
				System.out.println(catSc);
			}
			sc.close();
		}
		
	}
	

}
