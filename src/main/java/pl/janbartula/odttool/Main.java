package pl.janbartula.odttool;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main
{
    static String tempDirectory = "temp/";
    private static String inputDirectory = "input/";
    private static String outputDirectory = "output/";


    private static String templateFile = "content.xml";
    private static String dataFile = "data.json";


    public static void main(String[] args)
    {

        try
        {
            //Check if input data directory exist
            if (Files.notExists(Paths.get(inputDirectory)))
            {
                System.out.println("Error: Input directory does not exist.");
                return;
            }

            //Check if input JSON data exist
            if (Files.notExists(Paths.get(inputDirectory + dataFile)))
            {
                System.out.println("Error: JSON file does not exist in input directory.");
                return;
            }


            //Check if temporary directory exist
            if (Files.notExists(Paths.get(tempDirectory)))
            {

                //If not Create Temporary Directory
                if (!new File(tempDirectory).mkdirs())
                {
                    System.out.println("Error: Making of temporary directory.");
                }
            }

            //Check if Output directory exist
            if (Files.notExists(Paths.get(outputDirectory)))
            {

                //If not -> Create Output Directory
                if (!new File(outputDirectory).mkdirs())
                {
                    System.out.println("Error: Making of Output directory.");
                }
            }

            //Unzip ODT
            Zipper.unzip(inputDirectory, "template.odt", tempDirectory);


            //Check if input content data exist
            if (Files.notExists(Paths.get(inputDirectory + dataFile)))
            {
                System.out.println("Error: Cannot find " + dataFile + " in decompressed .odt file.");
                return;
            }


            //Read ODT Content file
            String template = readFile(tempDirectory + templateFile);


            //Read JSON Data
            String jsonData = readFile(inputDirectory + dataFile);
            JSONObject obj = new JSONObject(jsonData);
            JSONArray arr = obj.getJSONArray("datas");


            File srcDir = new File(tempDirectory);

            //for each objects in array replace gap with data from JSON
            for (int index = 1; index < arr.length(); index++)
            {

                //Read Json Data at Index in Array
                String name = arr.getJSONObject(index).getString("name");
                String login = arr.getJSONObject(index).getString("login");
                String password = arr.getJSONObject(index).getString("password");

                //Copy template and replace gaps
                String tempTemplate = template;
                tempTemplate = tempTemplate.replace("{aaaa}", name);
                tempTemplate = tempTemplate.replace("{bbbb}", login);
                tempTemplate = tempTemplate.replace("{cccc}", password);


                //Save replaced content file to temporary directory
                saveFile(tempDirectory + templateFile, tempTemplate);

                //Zip temporary directory with replaced content file
                Zipper.zip(tempDirectory, outputDirectory, "File" + index + ".odt");
            }

            //Remove temporary directory
            FileUtils.deleteDirectory(srcDir);


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    /**
     * @param path String path to file
     * @return Read file as String
     * @throws IOException Exception
     */
    private static String readFile(String path) throws IOException
    {
        return readFile(path, StandardCharsets.UTF_8);
    }

    /**
     * @param path     String path to file
     * @param encoding Encoding of file
     * @return Read file as String
     * @throws IOException Exception
     */
    private static String readFile(String path, Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    /**
     * @param path     String path to file
     * @param content Content to save - String
     * @throws IOException Exception
     */
    private static void saveFile(String path, String content) throws IOException
    {
        saveFile(path, content, StandardCharsets.UTF_8);
    }

    /**
     * @param path     String path to file
     * @param content Content to save - String
     * @param encoding Encoding of file
     * @throws IOException Exception
     */
    private static void saveFile(String path, String content, Charset encoding) throws IOException
    {
        try (Writer bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(path), encoding)
        ))
        {
            bufferedWriter.write(content);
        }

    }
}
