
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.HashSet;

public class TestProbes {

	public static void main(String ... args) {
		
		try {//open files
			//process metadata
			Scanner biglist = new Scanner(new File(args[0]));
			Scanner probeslist = new Scanner(new File(args[1]));
			HashSet<String> all = new HashSet<String>();
			HashSet<String> small = new HashSet<String>();
			HashSet<String> disjoint = new HashSet<String>();

			String entry;
			while (biglist.hasNext()) {
				entry = biglist.nextLine().trim();
				all.add(entry);
			}

			while (probeslist.hasNext()) {
				entry = probeslist.nextLine().trim();
				small.add(entry);
			}

			int ctr = 0;
			for (String probe : all) {
				if (!small.contains(probe)) {
					disjoint.add(probe);
				} else {
					ctr++;
				}
			}

			System.out.println("lists share " + ctr + " probes");
			System.out.println("bigger list has " + disjoint.size() + " new probes");

		} catch (FileNotFoundException ex) {
			System.out.println("java JaffeParser <data> <metadata> <excludelist> <out>");
		}
	}

}
