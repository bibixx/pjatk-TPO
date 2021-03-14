package zad1;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
	public static void processDir(String dirName, String resultFileName) {
        Path startPath = Paths.get(dirName);
        try {
        	FileChannel dstChannel = new FileOutputStream(resultFileName).getChannel();
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				private int position = 0;
				
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			    {
			        FileChannel srcChannel = new FileInputStream(file.toFile()).getChannel();

			        dstChannel.transferFrom(srcChannel, this.position, srcChannel.size());
			        this.position += srcChannel.size();
			        srcChannel.close();

			        return FileVisitResult.CONTINUE;
			    }
			});
			
			dstChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
