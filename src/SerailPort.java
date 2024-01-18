import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
///Message breakes on two messages size 1 an n-1, skipped first byte
// использовал библиотеку jssc для работы с последовательным портом подключал через project structure

public class SerailPort {
    public static SerialPort serialPort;
    public static final int bufferSize = 57;
    public static final int sleepTime = 1100; // время для чтения и записи нужно для работы модема
    private  static byte[] data;
    public void init(){// инициализация порта
        serialPort = new SerialPort("COM3");     // поработать над этим реализвать автоподключение
        try {
            //Открываем порт
            serialPort.openPort();
            //Выставляем параметры
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            //Включаем аппаратное управление потоком
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //Устанавливаем ивент лисенер и маску
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
            System.out.println("connected");
            //Отправляем запрос устройству

        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
    }
    public void serilWrite(Message message){ // что то записать принимает сообщения
        try{
            byte[] Bytes = message.toArray();
            serialPort.writeBytes(Bytes);
            Thread.sleep(SerailPort.sleepTime);
        }catch (SerialPortException ex){
            System.out.println("can't write");
        }catch (InterruptedException ex){
            System.out.println("can't write");
        }

    }
    public static void serilWrite(byte[] data){
        try{
            serialPort.writeBytes(data);
            Thread.sleep(SerailPort.sleepTime);
        }catch (SerialPortException ex){
            System.out.println("can't write");
        }catch (InterruptedException ex){
            System.out.println("can't write");
        }
    }
    // обработчик событий последовательного порта
    private static class PortReader implements SerialPortEventListener {
        private static byte[] SerialRead() throws SerialPortException{ // просто читаем байты
            data = serialPort.readBytes();
            return data;
        }
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    byte[] data = SerialRead(); // добавить в словарь пакетов
                    //create message
                    if(data.length > 3){
                        Message mess =new Message(data);
                        MessageConntainer.addMessage(mess);
                        // !replace for widget paste
                    }

                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}
