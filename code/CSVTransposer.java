
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

//currently tuned for the particular task of transposing Jaffe metadata
public class CSVTransposer {

	public static void main(String ... args) {
		
		try {//open files
			Scanner metadata = new Scanner(new File(args[0]));
			PrintWriter out = new PrintWriter(args[1]);
			String line[], col[], entry, acc;
			ArrayList<String[]> transposed = new ArrayList<String[]>();

			//read file in to matrix (so only one read required)
			int linnum = 0;
			while (metadata.hasNext()) {
				line = metadata.nextLine().split("\t");//tsv in this case
				col = new String[line.length];

				//title the columns properly
				if (linnum < 2) {
					col[0] = line[0].substring(1);

					for (int i = 1; i < line.length; i++) {//copy the rest of the data
						col[i] = line[i];
					}
				} else {
					col[0] = line[1].substring(0, line[1].indexOf(':'));
					
					for (int i = 1; i < line.length; i++) {//put the rest of the data in the column
						col[i] = line[i].substring(line[i].indexOf(':')+1).trim();
					}
				}

				transposed.add(col);
				linnum++;
			}

			//write file out
			for (int i = 0; i < transposed.get(0).length; i++) {
				acc = "";
				for (int j = 0; j < transposed.size(); j++) {
					acc += transposed.get(j)[i] + ",";
				}
				out.println(acc.substring(0, acc.length()-1));
			}
			out.flush();//make sure to flush. very important.

		} catch (FileNotFoundException ex) {
			System.out.println("Could not find input file.");
		}
	}

}