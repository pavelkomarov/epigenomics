
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestLine {

	public static void main(String ... args) {
		
		try {//open files
			//process metadata
			Scanner one = new Scanner(new File(args[0]));
			Scanner two = new Scanner(new File(args[1]));
			String[] lineone = one.nextLine().split(",");
			String[] linetwo = two.nextLine().split(" ");

			System.out.println(lineone.length);
			System.out.println(linetwo.length);

			boolean flag = true;
			for (int i = 0; i < lineone.length; i++) {
				if (!lineone[i].equals("X")) {
					flag &= lineone[i].equals(linetwo[i]); 
				}
			}

			System.out.println(flag);

		} catch (FileNotFoundException ex) {
			System.out.println("java JaffeParser <data> <metadata> <excludelist> <out>");
		}
	}

}
