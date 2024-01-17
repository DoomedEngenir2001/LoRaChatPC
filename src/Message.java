public class Message {
    private byte[] Id;
    private byte n;
    private byte[] payload;
    private byte[] time_label;

    Message(byte[] data){
        Id = new byte[2];
        time_label = new byte[3];
        Id[0] = data[0];
        Id[1]= data[1];
        n = data[2];
        time_label[0] = data[3]; // hours
        time_label[1] = data[4]; // minutes
        time_label[2] = data[5]; // seconds
        payload = new byte[data.length - 6];
        System.arraycopy(data, 6, payload, 0,data.length-6);
    }
    Message(byte[] id, byte n_, byte[] payload_, byte[] time){
        Id = id;
        n = n_;
        payload = payload_;
        time_label = time;
    }

    byte[] getId(){
        return Id;
    }

    byte[] getPayload(){
        return  payload;
    }
    byte getN(){
        return  n;
    }

    int getSize(){
        return 6 + payload.length;
    }

    byte[] toArray(){
        byte[] message = new byte[7+payload.length];
        message[0] = (byte)127;
        message[1] = Id[0];
        message[2] = Id[1];
        message[3] =n;
        message[4] = time_label[0];
        message[5] = time_label[1];
        message[6] = time_label[2];
        System.arraycopy(payload,0,message,7,payload.length);
        return message;

    }
}
