
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;
import java.io.PrintWriter;

public class FindDisjoint {

	public static void main(String ... args) {
		
		try {//open files
			//process metadata
			Scanner ixasfile = new Scanner(new File(args[0]));
			Scanner original = new Scanner(new File(args[1]));
			String[] lineixa, lineori;

			HashSet<String> include = new HashSet<String>();
			while (ixasfile.hasNext()) {				
				lineixa = ixasfile.nextLine().split(" ");
				
				include.add(lineixa[0]);
			}
			System.out.println("finished parsing ixa");

			HashSet<String> exclude = new HashSet<String>();
			while (original.hasNext()) {
				lineori = original.nextLine().split(",");
				
				if (!include.contains(lineori[0])) {
					exclude.add(lineori[0]);
				}
			}
			System.out.println("finished parsing original");

			PrintWriter out = new PrintWriter(args[2]);
			for (String cpg : exclude) {
				out.println(cpg.substring(1, cpg.length()-1));
			}
			out.flush();
			
		} catch (FileNotFoundException ex) {
			System.out.println("java JaffeParser <data> <metadata> <excludelist> <out>");
		}
	}

}