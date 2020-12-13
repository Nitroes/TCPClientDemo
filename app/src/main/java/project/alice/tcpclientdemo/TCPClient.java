package project.alice.tcpclientdemo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;

public class TCPClient {

    public String ipString = "127.0.0.1";   // 服务器端ip
    public int port = 37280;                // 服务器端口

    public Socket socket;
    public SocketCallBack call;				// 数据接收回调方法
    interface SocketCallBack{
        void Print(String info);
    }


    public TCPClient(SocketCallBack print, String ipString, int port)
    {
        this.call = print;
        if (ipString != null) this.ipString = ipString;
        if (port >= 0) this.port = port;
    }

    /** 创建Socket并连接 */
    public void start()
    {
        if (socket != null && socket.isConnected()) return;

        Executors.newCachedThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (socket == null)
                    {
                        InetAddress ip = InetAddress.getByName(ipString);
                        socket = new Socket(ip, port);

                        if (call != null) call.Print("服务器已连接 -> " + ip + ":" + port);
                    }
                }
                catch (Exception ex)
                {
                    if (call != null) call.Print("连接服务器失败 " + ex.toString()); // 连接失败
                }

                // Socket接收数据
                try
                {
                    if (socket != null)
                    {
                        InputStream inputStream = socket.getInputStream();

                        // 1024 * 1024 * 3 = 3145728
                        byte[] buffer = new byte[3145728];		// 3M缓存
                        int len = -1;
                        while (socket.isConnected() && (len = inputStream.read(buffer)) != -1)
                        {
                            String data = new String(buffer, 0, len);

                            // 通过回调接口将获取到的数据推送出去
                            if (call != null)
                            {
                                call.Print("接收信息 -> " + data);
                            }
                        }

                    }
                }
                catch (Exception ex)
                {
                    if (call != null) call.Print("接收socket信息失败" + ex.toString()); // 连接失败
                    socket = null;
                }
            }
        });

    }

    /** 发送信息 */
    public void Send(String data)
    {
        new Thread(()->{
            try
            {
                if(socket != null && socket.isConnected())
                {
                    byte[] bytes = data.getBytes();
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(bytes);

                    if (call != null) call.Print("发送信息 -> " + data);
                }
                else
                {
                    if (call != null) call.Print("未连接服务器！清先连接后，再发送。");
                }
            }
            catch (Exception ex)
            {
                if (call != null) call.Print("发送socket信息失败！");
            }
        }).start();
    }

    /** 断开Socket */
    public void disconnect()
    {
        try
        {
            if (socket != null && socket.isConnected())
            {
                socket.close();
                socket = null;

                if (call != null) call.Print("服务器已断开！ ");
            }
        }
        catch (Exception ex)
        {
            if (call != null) call.Print("断开socket失败!");
        }
    }
}

