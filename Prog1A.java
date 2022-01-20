import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Scanner;

public class Prog1A {
	// Stores the max size of each col
	private static int[] colSizes = new int[9];
	
	private static ArrayList<String[]> strRecords = new ArrayList<String[]>();
	private static ArrayList<int[]> intRecords = new ArrayList<int[]>();

	public static void main(String[] args) {
		readFile(args[0]);
		padStrings();
		writeBinFile();
	}

	public static void readFile(String filename) {
		File file = new File(filename);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// Skip metadata line
		if (scanner.hasNext()) {
			scanner.nextLine();
		} else {
			return;
		}
		
		while (scanner.hasNext()) {
			String[] strRecLine = new String[9];
			int[] intRecLine = new int[4];
			// Parse each line with correct regex and keep track of largest col sizes
			String line = scanner.nextLine();
			String[] lineArr = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 13);
			if (lineArr[12] == "") {
				lineArr[12] = "0";
			}
			
			for(int i = 0; i < lineArr.length; i++) {
				// Removing extra quote characters from each record
				if (lineArr[i].contains("\"")) {
					lineArr[i] = lineArr[i].substring(1, lineArr[i].length() - 1);
				}
				// Removing extra commas from integer records
				if (lineArr[i].contains(",") && i > 8) {
					lineArr[i] = lineArr[i].replaceAll(",", "");
				}
				// Storing the max sizes of each column
				if (i < 9 && lineArr[i].length() > colSizes[i]) {
					colSizes[i] = lineArr[i].length();
				}
				if (i < 9) {
					String normStr = Normalizer.normalize(lineArr[i], Normalizer.Form.NFKD).replaceAll("[^\\p{ASCII}]", "");
					strRecLine[i] = normStr;
				} else {
					intRecLine[i - 9] = Integer.parseInt(lineArr[i]);
				}
			}
			
			strRecords.add(strRecLine);
			intRecords.add(intRecLine);	
		}
		
//		for(String[] l: strRecords) {
//			for(String s: l) {
//				System.out.println(s);
//			}
//		}
//		
//		for(int[] l: intRecords) {
//			for(int s: l) {
//				System.out.println(s);
//			}
//		}
//		
//		for (int size: colSizes) {
//			System.out.println("SIZES: " + size);
//		}
		
		scanner.close();
	}
	
	public static void padStrings() {
		// Checks length of each string versus max column size
		for(int i = 0; i < strRecords.size(); i++) {
			for(int j = 0; j < strRecords.get(i).length; j++) {
				if (strRecords.get(i)[j].length() < colSizes[j]) {
					strRecords.get(i)[j] = String.format("%" + (-colSizes[j]) + "s", strRecords.get(i)[j]);
				}
			}
		}
	}
	
	private static void writeBinFile() {
		// TODO: Change to actual filename
		File fileRef = new File("filename.bin");
		RandomAccessFile dataStream = null;
		try {
			dataStream = new RandomAccessFile(fileRef, "rw");
		} catch (FileNotFoundException e) {
			System.out.println("I/O ERROR: Something went wrong with the "
                    + "creation of the RandomAccessFile object.");
			System.exit(-1);
		}
		
		for (int i = 0; i < strRecords.size(); i++) {
			for (int j = 0; j < 13; j++) {
				if (j < 9) {
					try {
						dataStream.writeBytes(strRecords.get(i)[j]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						dataStream.writeInt(intRecords.get(i)[j - 9]);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
        try {
            dataStream.close();
        } catch (IOException e) {
            System.out.println("VERY STRANGE I/O ERROR: Couldn't close "
                             + "the file!");
        }
	}
	
}
