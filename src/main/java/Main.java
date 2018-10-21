import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import zipper.Zipper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{

    public static String nametemplate = "temp/content.xml";
    public static String data = "input/data.json";
    public static String tempdirectory = "temp";


    public static void main(String[] args)
    {

        try
        {
            if (Files.notExists(Paths.get(tempdirectory)))
            {
                if (!new File(tempdirectory).mkdirs())
                {
                    System.out.println("Error Making of temporary directory.");
                }
            }

          Zipper.unzip("input/","template.odt","temp");

            String jsondata = readFile(data, StandardCharsets.UTF_8);
            System.out.println(jsondata);

            JSONObject obj = new JSONObject(jsondata);
            JSONArray arr = obj.getJSONArray("datas");

            File destDir = new File("tempcopy");


            for (int i = 1; i < arr.length(); i++)
            {

                File srcDir = new File("temp");

                FileUtils.copyDirectory(srcDir, destDir);


                String template = readFile(nametemplate, StandardCharsets.UTF_8);

                String name = arr.getJSONObject(i).getString("name");
                String login = arr.getJSONObject(i).getString("login");
                String password = arr.getJSONObject(i).getString("password");


                String copyoftemplate = template;
                copyoftemplate = copyoftemplate.replace("{aaaa}", name);
                copyoftemplate = copyoftemplate.replace("{bbbb}", login);
                copyoftemplate = copyoftemplate.replace("{cccc}", password);


                Writer out2 = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream("temp/content.xml"), StandardCharsets.UTF_8));
                try
                {
                    out2.write(copyoftemplate);
                } finally
                {
                    out2.close();
                }

                Zipper.zip("tempcopy", "output/", "File" + i + ".odt");


            }


            //
            //java.nio.file.Path#delete();

            FileUtils.deleteDirectory(destDir);


//            File fin = new File("temp");
//            for (File file : fin.listFiles()) {
//                FileDeleteStrategy.FORCE.delete(file);
//            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
