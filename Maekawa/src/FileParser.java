import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileParser {

	public static void main(String[] args) throws FileNotFoundException {

		Scanner sc;
		if (args.length > 0) {
			File f1 = new File(args[0]);
			sc = new Scanner(f1);
		} else {
			System.out.println("Enter a file name:");
			sc = new Scanner(System.in);
		}

		String previous[], current[];
		String previousLine, currentLine;
		previousLine = sc.nextLine();
		previous = previousLine.split(" ");

		while (sc.hasNext()) {

			currentLine = sc.nextLine();
			current = currentLine.split(" ");
			// current[1].equals(previous[1]) &&
			if ((current[0].equals(previous[0]))) {

				System.out.println("Overlapping sections");
				return;
			} else {

				previous = currentLine.split(" ");

			}

		}

		System.out.println("overlapping not found");
	}

}
