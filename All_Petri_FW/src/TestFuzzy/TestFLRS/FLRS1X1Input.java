package TestFuzzy.TestFLRS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public class FLRS1X1Input {
	public static void main(String[] args) throws InterruptedException, IOException {
		File file = new File("C:\\Users\\user\\Desktop\\DCS_Project_Danci_Daria_30341\\PS1\\All_Petri_Nets_Framework_4_IntelliJ-master\\All_Petri_FW\\All_Petri_FW\\test3.txt");
		Files.deleteIfExists(file.toPath());
		FileWriter fw = new FileWriter(file.getPath());
		Float f = -1f;
		for (int i = 0; i < 100; i++) {
		
			fw.write("P1:"+f+"F\n");
			f += 0.00001f;
		}
		fw.close();
		System.out.println("Done!");
	}
}
