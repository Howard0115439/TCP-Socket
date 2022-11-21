import java.io.*;
import java.net.Socket;

public class client
{
    public static void main(String[] args)
    {
        try
        {
            Socket st = new Socket("localhost", 8089);
            PrintWriter output = new PrintWriter(st.getOutputStream(), true);// to server
            BufferedReader input = new BufferedReader(new InputStreamReader(st.getInputStream()));// from server
            BufferedReader type_in = new BufferedReader(new InputStreamReader(System.in));// from keyboard
            while (true)
            {
                System.out.print("client input : ");
                String command = type_in.readLine();
                if (command.equals("index") || command.startsWith("get "))      //startsWith()表示開始的值，如果正確回傳true
                {
                    output.println(command);
                    String firstLine = "";
                    String line = "";
                    while (!(line = input.readLine()).equals("end"))           //如果還沒readLine()讀到最後(server端用EOF表示最後元素)
                    {
                        if (firstLine.equals(""))           //剛開始firstLine為空
                        {
                            firstLine = line;
                        }
                        System.out.println(line);
                    }

                    if (command.startsWith("get ") && !firstLine.startsWith("error"))       //如果輸入是get且檔案存在的話(server-66行)，就break
                    {
                        break;
                    }
                }
                else
                {
                    System.err.println("Invalid command.");
                }
                output.flush();
            }
            output.close();
            input.close();
            st.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}