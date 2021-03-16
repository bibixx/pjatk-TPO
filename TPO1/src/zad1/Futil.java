package zad1;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
	public static void processDir(String dirName, String resultFileName) {
        Path startPath = Paths.get(dirName);
        try {
        	FileOutputStream fos = new FileOutputStream(resultFileName);
        	FileChannel dstChannel = fos.getChannel();
        	Charset outputCharset = Charset.forName("UTF-8");

			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			    {
			    	FileInputStream fis = new FileInputStream(file.toFile());
			        FileChannel srcChannel = fis.getChannel();
			        
			        ByteBuffer buffer = ByteBuffer.allocateDirect(256);

			        Charset inputCharset = Charset.forName("windows-1250");
			        
			        while (srcChannel.read(buffer) != -1) {
			            buffer.rewind();
			            CharBuffer charBuffer = inputCharset.decode(buffer);
			            dstChannel.write(outputCharset.encode(charBuffer));

			            buffer.clear();
			        }
			        
			        srcChannel.close();
			        fis.close();

			        return FileVisitResult.CONTINUE;
			    }
			});
			
			dstChannel.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
