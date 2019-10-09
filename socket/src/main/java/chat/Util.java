package chat;

import java.io.*;
import java.util.logging.Logger;

public class Util {

    public static void bytesToFile(byte[] buffer, final String filePath){

        File file = new File(filePath);

        OutputStream output = null;
        BufferedOutputStream bufferedOutput = null;

        try {
            output = new FileOutputStream(file);

            bufferedOutput = new BufferedOutputStream(output);

            bufferedOutput.write(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(null!=bufferedOutput){
                try {
                    bufferedOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(null != output){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    public static byte[] fileToBytes(String filePath) {
        byte[] buffer = null;
        File file = new File(filePath);

        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            bos.write(new byte[]{-2});     // 开头-2 表示是文件
            byte[] b = new byte[1024];

            int n;

            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }

            buffer = bos.toByteArray();
        } catch (FileNotFoundException ex) {
//            Logger.getLogger(JmsReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
//            Logger.getLogger(JmsReceiver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (null != bos) {
                    bos.close();
                }
            } catch (IOException ex) {
//                Logger.getLogger(JmsReceiver.class.getName()).log(Level.SEVERE, null, ex);
            } finally{
                try {
                    if(null!=fis){
                        fis.close();
                    }
                } catch (IOException ex) {
//                    Logger.getLogger(JmsReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return buffer;
    }
}
