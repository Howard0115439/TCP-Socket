import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;

public class server
{
    public static void main(String[] args)
    {
        if (args.length != 1)                                           //cmd no folder path input, so args.length==0
        {
            System.err.println("Please input the folder path.");
            return;
        }
        String folderPathString = args[0];
        Path folderPath = Paths.get(folderPathString);                  //convert string to the path
        if (!Files.isDirectory(folderPath))                             //isDirectory()判斷是否為路徑，是return true，否return false
        {
            System.err.println("Invalid folder path.");
            return;
        }

        try
        {
            ServerSocket ss = new ServerSocket(8089);              //創建server socket
            System.out.println("wait for connected");
            Socket st = ss.accept();                                    //accept()等待連接請求
            System.out.println("Server connected");
            BufferedReader input = new BufferedReader(new InputStreamReader(st.getInputStream()));// from client, InputStreamReader()將字節流轉成字符流,
            PrintWriter output = new PrintWriter(st.getOutputStream(), true);// to client, true可以用追加的方式寫文件
            while (true)
            {
                String command = input.readLine();// read from the client
                if (command.equals("index"))
                {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Folder Path : " + folderPath.toAbsolutePath().toString() + "\n");     //toAbsolutePath()表示path的絕對路徑
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath))       //先存到類似arraylist的DirectoryStream<Path>再遍歷目錄
                    {
                        for (Path p : stream)
                        {
                            sb.append(p.getFileName() + "\n");
                        }
                        sb.append("end\n");                         //以end判斷做結束
                        String message = sb.toString();
                        output.print(message);
                    }
                    catch (IOException e)
                    {
                        System.err.println("Invalid folder path.");
                        e.printStackTrace();                    //printStackTrace()代表"在命令行打印異常信息在程序中出錯的位置及原因"
                    }
                }
                else if (command.startsWith("get "))
                {
                    String filePathString = command.split(" ")[1];          //輸入為get test1.txt,所以command.split(" ")[0]是get,command.split(" ")[1]是test1.txt
                    Path filePath = Paths.get(folderPathString + "/" + filePathString);  //"路徑名稱"和"檔案名稱"和在一起
                    if (Files.notExists(filePath))
                    {
                        output.println("error\nInvalid file name.\nend\n");
                        System.err.println("Invalid file name.");
                    }
                    else
                    {
                        StringBuilder sb = new StringBuilder();
                        sb.append("ok\n");

                        for (String line : Files.readAllLines(filePath))        //如果filePath存在，就把每一行印出
                        {
                            sb.append(line + "\n");
                        }

                        sb.append("end\n");
                        String message = sb.toString();
                        output.println(message);
                        System.out.println("Server disconnected");
                        break;
                    }
                }
                else
                {
                    output.println("error\nInvalid file name.\nend\n");
                    System.err.println("Invalid command.");
                }
                output.flush();// 清空缓存区
            }
            output.close();
            input.close();
            st.close();
            ss.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}