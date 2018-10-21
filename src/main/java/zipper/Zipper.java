package zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Zipper
{

        public static void unzip(String file_path, String file_name, String unzip_path) throws IOException
    {
        if(!unzip_path.endsWith("/")){
            unzip_path+="/";
        }



        final byte[] buffer = new byte[1024];
        final ZipInputStream zis = new ZipInputStream(new FileInputStream(file_path+file_name));
        ZipEntry zipEntry = zis.getNextEntry();


        while (zipEntry != null) {
            final String fileName = zipEntry.getName();
            System.out.println(fileName);

            String filepaths[]= fileName.split("/");

            String path = unzip_path;
            for(int index = 0; index<filepaths.length-1;index++){



                path+="/"+filepaths[index];
                if (Files.notExists(Paths.get(path)))
                {

                    System.out.println(path);
                    if(!new File(path).mkdirs()){
                        System.out.println("Error Making of temporary directory.");
                    }
                }
            }







            final File newFile = new File("temp/"+fileName);
            System.out.println( "File save directory: temp/"+fileName );
            final FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }



    public static void zip(String files_path, String zip_path, String zip_name ) throws IOException{

        FileOutputStream fos = new FileOutputStream(zip_path+zip_name);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(files_path);




        zipPath(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

    }

    /**
     * @param fileToZip Path to file/directory to zip
     * @param fileName Path to zipped file.
     * @param zipOut
     * @throws IOException
     */
    private static void zipPath(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {

        fileName = fileName.replace("tempcopy/","");
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }


            File[] children = fileToZip.listFiles();
            for (File childFile : children) {


                zipPath(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
