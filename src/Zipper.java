import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.zip.*;
import java.io.*;
public class Zipper {
    private final int compressedSize =1024;
    public byte [] CompressdBuffer = new byte[compressedSize];
    private FileInputStream is;
    private FileOutputStream os;
    private GZIPOutputStream GzipStream;
    private GZIPInputStream GzipInSteam;
    public byte[] flate(byte[] fileData){// сжать
            byte[] compressed = new byte[fileData.length];
            Deflater compressor = new Deflater();
            compressor.setInput(fileData);
            compressor.finish();
            int len = compressor.deflate(compressed);
            byte[] cutCompressed = Arrays.copyOf(compressed, len);
            compressor.end();
            return cutCompressed;
    }

    public byte[] deflate(byte[] compressedData){ // разжать
        try{
            byte[] decompressedBuffer = new byte[10000];
            Inflater decompressor = new Inflater();
            decompressor.setInput(compressedData);
            int len = decompressor.inflate(decompressedBuffer);
            decompressor.finished();
            byte[] cutData = Arrays.copyOf(decompressedBuffer, len);
            decompressor.end();
            return  cutData;

        }catch (DataFormatException ex){
            System.out.println("Unknown extension");
            return new byte[1];
        }


    }


}
