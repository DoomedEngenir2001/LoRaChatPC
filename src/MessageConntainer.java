import java.util.*;
// Это надо менять
public  class MessageConntainer {
    // словарь в котором хранится соответствие адрес (котороый определяется в SerialSocket ) с сообщениями который он отправил
    // все сообщения представляют собой пакеты
    private static Map<byte[], List<Message>> messageContainer= new HashMap<byte[], List<Message> >();
    private static Zipper zip = new Zipper();
    public static void addMessage(Message message){
        byte[] id_ = message.getId();
        byte n = message.getN();
        List<Message> currentList;
        if (containAddress(messageContainer, id_)){// если с адреа приходили сообщения
            byte[] key = getExistKey(messageContainer, id_);
            currentList = messageContainer.get(key); // if key exist
            currentList.add(message);
           // messageContainer.remove(key);
            messageContainer.put( key, currentList);
        }
        else {// если сообщение приходит в первый раз
            currentList = new Vector<Message>(240);
            currentList.add(message); // in any case append message and update in hashmap
            messageContainer.put(id_, currentList);

        }
        // когда получаем пакет, котороый является последним
        if (n==(byte)255){ // if end packet
            int listLen =  currentList.size();
            int payloadSize = SerailPort.bufferSize -6;
                int size = (currentList.size() - 1)*(payloadSize)
                        + currentList.get(listLen - 1).getPayload().length;
                byte[] recievedData = new byte[size];// собираем всю полезную нагрузку воедино

                for(int i=0;i< listLen;i++){
                    byte[] currentPayload = currentList.get(i).getPayload();
                    System.arraycopy(currentPayload, 0, // copy to array
                            recievedData, i*payloadSize, currentPayload.length);
                }
                byte[] decompressData = zip.deflate(recievedData);// разжимаем
                System.out.println(new String(decompressData)); // заменит на отображение в чате
                //TO-DO may add to file
                messageContainer.remove(getExistKey(messageContainer, id_)); // e

            }
    }

    public static boolean containAddress(Map<byte[], List<Message>> map, byte[] Address){// костыль - поиск адресов, с которых отправлялись сообщения
        for(byte[] key : map.keySet()){
            if(key[0] == Address[0] && key[1] == Address[1])return true;
        }
        return false;
    }

    public static byte[] getExistKey(Map<byte[], List<Message>> map, byte[] value){// костыль получение интересующего адреса
        for(byte[] key : map.keySet()){
            if(key[0] == value[0] && key[1] == value[1])return key;
        }
        return null;
    }

}
