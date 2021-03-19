package zad1;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Futil {
	public static void processDir(String dirName, String resultFileName) {
        Path startPath = Paths.get(dirName);
        Path resultPath = Paths.get(resultFileName);

        try {
	    	FileChannel dstChannel = FileChannel.open(resultPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	    	Charset outputCharset = Charset.forName("UTF-8");

			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
			    {
			        FileChannel srcChannel = FileChannel.open(file);
			        
			        ByteBuffer buffer = ByteBuffer.allocateDirect(256);

			        Charset inputCharset = Charset.forName("windows-1250");
			        
			        while (srcChannel.read(buffer) != -1) {
			            buffer.rewind();
			            CharBuffer charBuffer = inputCharset.decode(buffer);
			            dstChannel.write(outputCharset.encode(charBuffer));

			            buffer.clear();
			        }
			        
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
