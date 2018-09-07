
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.HashSet;

public class MetaMatcher {

	public static void main(String ... args) {
		try {
			//read the first line of the data file because it contains the names of all the samples we want
			Scanner data = new Scanner(new File(args[0]));
			String[] titleline = data.nextLine().split(",");

			//put samples we're considering in to a hashset
			HashSet<String> samples = new HashSet<String>();
			for (int i = 1; i<titleline.length; i++) {
				samples.add(titleline[i].substring(1,titleline[i].indexOf('_')));
			}

			//start processing the metadata
			Scanner metadata = new Scanner(new File(args[1]));
			metadata.nextLine();//eat the title line and replace with my own
			PrintWriter out = new PrintWriter(args[2]);	
			out.println("SampleID,BrNum,Plate,DropSample,Sex,Ethnicity,Dx,Age,negControl_PC1,negControl_PC2," +
				"negControl_PC3,negControl_PC4,predictedSex,bestQC,ES,NPC,DA_NEURON,NeuN_pos,NeuN_neg");

			String line[], sample, acc;
			while (metadata.hasNext()) {
				line = metadata.nextLine().split(",");//csv
				sample = line[0].substring(0, line[0].indexOf('_'));//SampleX without _Control or _Schizo

				if (samples.contains(sample)) {//if we are considering this sample
					acc = sample + ",";//reset accumulator
					
					for (int i=2; i<line.length; i++) {//accumulate all the other info on the line
						acc += line[i] + ",";
					}
					out.println(acc.substring(0, acc.length()-1));//chop off last "," and output
				}
			}
			out.flush();//make sure it goes on disk

		} catch (FileNotFoundException ex) {
			System.out.println("java JaffeParser <data> <metadata> <out>");
		}
	}
}