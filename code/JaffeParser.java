
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashSet;

public class JaffeParser {

	public static void main(String ... args) {
		
		try {//open files
			//process metadata
			Scanner metadata = new Scanner(new File(args[1]));
			HashSet<Integer> droprows = new HashSet<Integer>();
			String line[], entry, acc;
			int dropsamplecol=0, bestqccol=0, agecol=0, platecol=0, groupcol=0;

			line = metadata.nextLine().split(",");//find important columns in the metadata with
			for (int i = 0; i < line.length; i++) {//features we need to filter on
				entry = line[i];
				if (entry.contains("dropsample (whether to remove the sample" +
					" for failing quality control)")) {
					dropsamplecol = i;
				} else if (entry.contains("bestqc (best sample to use when more than 1" +
					" array were run on the same subject/brnum)")) {
					bestqccol = i;
				} else if (entry.contains("age (in years)")) {
					agecol = i;
				} else if (entry.contains("plate (processing plate)")) {
					platecol = i;
				} else if (entry.contains("group")) {
					groupcol = i;
				}
			}

			//drop samples from the output based on conditions
			int row = 1;//filter samples on conditions
			while (metadata.hasNext()) {
				line = metadata.nextLine().split(",");//csv
				
				if (line[dropsamplecol].contains("TRUE") ||//reasons to drop a sample from analysis
					line[bestqccol].contains("FALSE") ||
					//Double.parseDouble(line[agecol]) < 16 || //include for SZ_vs_control only
					(Double.parseDouble(line[agecol]) > 16 && line[groupcol].contains("Control")) || //include for SZ_vs_youth only
					//line[groupcol].contains("Schizo") || //include for control_vs_youth only
					line[platecol].equals("Lieber_244")) {
					droprows.add(row);
				}
				row++;
			}//removerows now has set of numbers of rows to be removed from data
			//data will remove the corresponding columns at i=2*row and i=2*row-1

			//read in CpGs we will exclude and put them in a Hashset
			Scanner exclude = new Scanner(new File(args[2]));
			HashSet<String> xcpgs = new HashSet<String>();

			while (exclude.hasNext()) {
				xcpgs.add(exclude.nextLine().trim());
			}

			//parse huge dataset, filtering columns based on metadata and rows based on CpG exclusions
			Scanner data = new Scanner(new File(args[0]));
			PrintWriter out = new PrintWriter(args[3]);

			line = data.nextLine().split(",");//copy title line
			acc = ",";
			for (int i = 1; i < line.length; i+=2) {
				if (!droprows.contains((i+1)/2)) {
					acc += line[i] + ",";
				}
			}
			out.println(acc.substring(0, acc.length()-1));

			int itr = 1;//copy all the data lines
			while (data.hasNext() && itr < 1000000) {//itr is a safety, to get only small datasets
				line = data.nextLine().split(",");//csv
				entry = line[0].substring(1, line[0].length()-1);//CpG name without quotes

				if (!xcpgs.contains(entry)) {//if we are considering this CpG
					acc = line[0] + ",";//reset accumulator
					
					for (int i = 1; i < line.length-1; i+=2) {
						if (!droprows.contains((i+1)/2)) {//fast query to see whether we are keeping this sample
							
							if (Double.parseDouble(line[i+1]) > 0.01) {//check p value to see if measurement is junk
								acc += "NA,";
							} else {
								acc += line[i] + ",";
							}
						}
					}
					out.println(acc.substring(0, acc.length()-1));//chop off last ","

					if ((itr % 1000) == 0) {//keep this from building up in memory
						out.flush();
						System.out.print( (itr%10000)==0 ? itr : ".");
					}
					itr++;
				}
			}
			out.flush();//last little push

		} catch (FileNotFoundException ex) {
			System.out.println("java JaffeParser <data> <metadata> <excludelist> <out>");
		}
	}

}