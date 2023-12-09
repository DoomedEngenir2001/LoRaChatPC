import javax.swing.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.lang.Thread;
// использовал  java swing для ЮИ
// 20 JDK
// запускать этот файл
public class messageScreen extends JDialog {
    private JPanel contentPane;
    private JButton buttonSend;
    private JButton buttonSendFile;
    private JTextField textField1;
    private static SerailPort serialSocket ;
    private FileInputStream is;
    private Zipper compressor;
    private static byte[] Id = {(byte) 0, (byte) 1}; // адрес модема
    private static final int payloadSize = SerailPort.bufferSize -4;  // размер полезной нагрузки
    public messageScreen() { // инициализация интерфейса пользоваеля
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonSend);

        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSend();
            }
        });

        buttonSendFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSendFile();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    private  static Message[] getPackets(byte[] Bytes){ // разбивает байтовую последовательность на пакеты
        int bound = 0;
        int byteSize = Bytes.length;
        int nPackets = byteSize / payloadSize + 1;
        byte[] payload = new byte[payloadSize];
        Message[] packets = new Message[nPackets];
        for(int i =0; i< nPackets;i++){
            if (i != nPackets-1)
                bound = (i+1)*payloadSize;
            else bound = byteSize;
            //   if(bound < 0)bound += SerailPort.bufferSize;
            payload = Arrays.copyOfRange(Bytes, i*payloadSize, bound);
            if(i != nPackets-1)packets[i] = new Message(Id, (byte)i, payload); // create packet
            else packets[i] = new Message(Id, (byte)255, payload);
        }
        return  packets;
    }

    private  void onSend() { // sending simple message

        Zipper comp = new Zipper();
        String text = textField1.getText();
        text =text + " " + " " + " " + " "; // костыль, связанный с особенностью сжатия
        byte[] textBytes = text.getBytes();
        byte[] compData = comp.flate(textBytes); // сжать
        Message[] packets = getPackets(compData);
        for(int i=0; i<packets.length;++i){
            serialSocket.serilWrite(packets[i]);
        }
        textField1.setText("");
    }

    private void onSendFile() {// отправить файл
        try {
            compressor = new Zipper();
            JFileChooser dialog = new JFileChooser();
            int status = dialog.showOpenDialog(null);
            String filename;
            if (status == JFileChooser.APPROVE_OPTION) {
                filename = dialog.getSelectedFile().getAbsolutePath();
            } else {
                return;
            }
            is = new FileInputStream(filename);
            byte[] data = new byte[is.available()];
            int pos = is.read(data);// читаем
            byte[] compressedData = compressor.flate(data);// сжать
            Message[] packets = getPackets(compressedData); // разбить на пакеты
            for (int i = 0; i < packets.length; ++i) {
                serialSocket.serilWrite(packets[i]);
            }
            is.close();
        }catch (IOException ex){
            System.out.println(ex);
        }

    }
    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        serialSocket  = new SerailPort();
        serialSocket.init();
        messageScreen dialog = new messageScreen();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
