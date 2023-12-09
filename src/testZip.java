import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.nio.charset.Charset;
// тест сжатия и разжатия
public class testZip {
    public static void main (String[] args){
        try{
            Zipper zip = new Zipper();
            String filename = "C:\\Users\\Marlou\\IdeaProjects\\PcClient\\src\\sample4.json";
            FileInputStream is = new FileInputStream(filename);
            byte[] data = new byte[is.available()];
            int k = is.read(data);
            byte[] compressed = zip.flate(data);
            byte[][] packets = getPackets(compressed);

            // stack pakages
            // when end of package recieved
            // deflate them\\\
            byte[] data_ = new byte[(packets.length-1) * SerailPort.bufferSize + packets[packets.length -1].length];
            for(int i = 0; i< packets.length;i++)
                for(int j=0;j<packets[i].length;j++)data_[i*SerailPort.bufferSize + j] = packets[i][j];
            byte[] decompressed = zip.deflate(data_);
            System.out.println(new String(decompressed));

        }catch(IOException ex){
            System.out.println(ex);
        }
    }
    private  static byte[][] getPackets(byte[] Bytes){
        int bound = 0;
        int byteSize = Bytes.length;
        int nPackets = byteSize / SerailPort.bufferSize + 1;
        byte[][] packets = new byte[nPackets][];
        for(int i =0; i< nPackets;i++){
            if (i != nPackets-1)
                bound = (i+1)*SerailPort.bufferSize;
            else bound = byteSize;
         //   if(bound < 0)bound += SerailPort.bufferSize;
            packets[i] = Arrays.copyOfRange(Bytes, i*SerailPort.bufferSize, bound);
        }
        return  packets;
    }
}
