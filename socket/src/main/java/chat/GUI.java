package chat;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUI extends JFrame{

    private Button sendButton;
    private Button logButton;
    private Button clearButton;
    private Button shakeButton;
    private TextField tf;
    private TextArea sendText;
    private TextArea viewText;
    private DatagramSocket socket;
    private BufferedWriter bw;
    private JPanel northPanel;
    private JPanel southPanel;
    private JPanel centerPanel;
    private JComboBox setFontSize;
    private JButton fileChooseButton;
    private JFileChooser jFileChooser;
    private File[] selectedFiles;

    public GUI(){
        init();
        event();
    }
    private void quit() throws IOException {
        socket.close();
        bw.close();
        System.exit(0);
    }

    private void event() {

        fileChooseButton.addActionListener(new ActionListener() {     //打开文件浏览器
            public void actionPerformed(ActionEvent e) {
                try {
                    selectFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

//        fileFrame.addWindowListener(new WindowAdapter() {          // 关闭文件浏览器
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });




        this.addWindowListener(new WindowAdapter() {                // 关闭窗口
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    quit();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        sendButton.addActionListener(new ActionListener() {        // 发送
            public void actionPerformed(ActionEvent e) {
                try {
                    send();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cls();
            }
        });

        logButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logFile();

            }
        });

        shakeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    send(new byte[]{-1}, tf.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

       sendText.addKeyListener(new KeyAdapter() {                   // 快捷键 回车 发送
           @Override
           public void keyReleased(KeyEvent e) {
               if (e.isShiftDown() && e.getKeyCode() == KeyEvent.VK_ENTER){
                   sendText.append("\r\n");
               } else if (e.getKeyCode() == KeyEvent.VK_ENTER){
                   try {
                       send();
                   } catch (IOException ex) {
                       ex.printStackTrace();
                   }
               }
           }
       });

       sendText.addKeyListener(new KeyAdapter() {       //快捷键 Esc 关闭窗口
           @Override
           public void keyReleased(KeyEvent e) {
               if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                   try {
                       quit();
                   } catch (IOException ex) {
                       ex.printStackTrace();
                   }
               }
           }
       });

       viewText.addKeyListener(new KeyAdapter() {       //快捷键 Esc 关闭窗口
            @Override
            public void keyReleased(KeyEvent e) {
                System.out.println(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    try {
                        quit();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

       setFontSize.addActionListener(new ActionListener() {         // 设置字体显示大小
           public void actionPerformed(ActionEvent e) {
               int size = Integer.parseInt(setFontSize.getSelectedItem().toString());
               viewText.setFont(new Font("", 1, size));
           }
       });

    }

    private void shake() throws InterruptedException {
        int x = this.getLocation().x;
        int y = this.getLocation().y;
        for(int i = 0; i < 5; i++){
            this.setLocation(x + 20, y + 20);
            Thread.sleep(20);
            this.setLocation(x + 20, y - 20);
            Thread.sleep(20);
            this.setLocation(x - 20, y + 20);
            Thread.sleep(20);
            this.setLocation(x - 20, y - 20);
            Thread.sleep(20);
            this.setLocation(x , y);
        }
    }

    private void logFile() {
        try {
            bw.flush();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream("./logFile.txt");

            int len;
            byte[] arr = new byte[8192];
            while ((len = fis.read(arr)) != -1) {
                outputStream.write(arr, 0, len);
            }
            viewText.setText(outputStream.toString());

            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cls() {
        viewText.setText("");
    }

    private void send(byte[] arr, String ip) throws IOException {
        DatagramPacket packet = new DatagramPacket(arr,
                                                arr.length,
                                                InetAddress.getByName(ip),
                                                9999);
        socket.send(packet);
    }

    private void send() throws IOException {
        String message = sendText.getText().trim();
        String ip = tf.getText();
        ip = ip.trim().length() == 0 ? "255.255.255.255" : tf.getText();

        send(message.getBytes("UTF-8"), ip);

        String str = getCurrentTime() + "我对" + ip + "说：\r\n" + message + "\r\n\r\n";
        viewText.append(str);

        bw.write(str);

        sendText.setText("");
    }

    private String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");

        return df.format(date).toString();
    }

    private void init() {
        this.setName("WeChat");
        this.setSize(500,800);
        this.setLocation(new Point(800, 200));
        this.setTitle("WeChat");
        this.setLayout(new BorderLayout());

        this.add(northPanel(), BorderLayout.NORTH);
        this.add(centerPanel(), BorderLayout.EAST);
        this.add(southPanel(), BorderLayout.SOUTH);
        this.setVisible(true);

        new Recieve().start();                      // 开始接收消息

        try {
            socket = new DatagramSocket();
            bw = new BufferedWriter(new FileWriter("./logFile.txt", true));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void selectFile() throws IOException {                  // 文件选择
        jFileChooser = new JFileChooser();
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);          // 可以选择文件和文件夹
        jFileChooser.showOpenDialog(null);
//        jFileChooser.setMultiSelectionEnabled(true);
        File selectedFile = jFileChooser.getSelectedFile();
//        selectedFiles = jFileChooser.getSelectedFiles();
        sendText.append(selectedFile.getName() + "\r\n");

        byte[] bytes = Util.fileToBytes(selectedFile.getAbsolutePath());
        String ip = tf.getText();
        ip = ip.trim().length() == 0 ? "255.255.255.255" : tf.getText();
        send(bytes, ip);    // 发送文件，开头是-2

    }


    private JPanel centerPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new FlowLayout());

        String[] fontSize = new String[]{"10","15", "16", "17", "18", "19", "20", "21", "22"};      // 设置字体大小
        setFontSize = new JComboBox(fontSize);
        fileChooseButton = new JButton("选择文件");

        centerPanel.add(fileChooseButton);
        centerPanel.add(setFontSize);
        centerPanel.setPreferredSize(new Dimension(500, 50));
        return centerPanel;
    }

    private JPanel northPanel() {
        northPanel = new JPanel();
        northPanel.setLayout(new BorderLayout());
        viewText = new TextArea();
        sendText = new TextArea();

        viewText.setEditable(false);

        northPanel.add(viewText, BorderLayout.CENTER);
        northPanel.add(sendText, BorderLayout.SOUTH);

        northPanel.setPreferredSize(new Dimension(500, 650));

        return northPanel;
    }

    private JPanel southPanel() {
        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());

        tf = new TextField(15);
        tf.setText("127.0.0.1");
        sendButton = new Button("发送");
        logButton = new Button("记录");
        clearButton = new Button("清屏");
        shakeButton = new Button("震动");

        southPanel.add(tf);
        southPanel.add(sendButton);
        southPanel.add(logButton);
        southPanel.add(clearButton);
        southPanel.add(shakeButton);
        southPanel.setPreferredSize(new Dimension(500, 100));

        return southPanel;
    }


    private class Recieve extends Thread{
        @Override
        public void run() {
            try {
                DatagramSocket socket = new DatagramSocket(9999);
                DatagramPacket packet = new DatagramPacket(new byte[8192], 8192);
                while (true) {
                    socket.receive(packet);
                    byte[] arr = packet.getData();
                    int len = packet.getLength();
                    if (arr[0] == -1 && len == 1){
                        shake();
                        continue;
                    }
                    if (arr[0] == -2) {                 // 传输的是文件
                        FileOutputStream out = new FileOutputStream("./save.txt");
                        out.write(arr,1, len);
                        out.close();
                        continue;
                    }
                    String message = new String(arr, 0, len, "UTF-8");
                    String ip = packet.getAddress().getHostAddress();
                    String str = getCurrentTime() + ip + "对我说：\r\n" + message.trim() + "\r\n\r\n";
                    viewText.append(str);
                    bw.write(str);
//                    System.out.println(new String(message.getBytes("UTF-8"), "UTF-8"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("file.encoding"));
        new GUI();

    }
}
