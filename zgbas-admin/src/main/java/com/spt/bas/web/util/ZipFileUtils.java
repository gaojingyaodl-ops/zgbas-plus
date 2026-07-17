package com.spt.bas.web.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipFileUtils {
	private static int BUF_SIZE = 1024*10;
/* public static void main(String[] args) {
		try {
			File f = new ZipFileUtils().createZip("D:\\download\\zip","D:\\download\\zip","doczip12");
			System.out.println(f.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	//File f = new File("D:\\download\\1531279152789");
	// new ZipFileUtils().delete(f);
	
	 
	}*/

	/**
	 * 创建压缩文件
	 * @param sourcePath 要压缩的文件
	 * @param zipFilePath 文件存放路徑
	 * @param zipfileName 压缩文件名称
	 * @return File
	 * @throws IOException
	 */
	public static File createZip(String sourcePath ,String zipFilePath,String zipfileName) throws IOException{
		 //打包文件名称
		 zipfileName = zipfileName+".zip";
		 
		 /**在服务器端创建打包下载的临时文件夹*/
		 File zipFiletmp = new File(zipFilePath+System.currentTimeMillis());
		 if(!zipFiletmp.exists() && !(zipFiletmp.isDirectory())){
		    zipFiletmp.mkdirs();
		 }
		 
		 File fileName = new File(zipFiletmp,zipfileName);
		 //打包文件

		 createZip(sourcePath,fileName);
		return fileName;
	}
	
	 /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, File zipFile) {
        ZipOutputStream zos = null;
        try {
        	zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile),BUF_SIZE));
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
        	 throw new RuntimeException(e); 
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
            	 throw new RuntimeException(e); 
            }
 
        }
    }
    
    /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath),BUF_SIZE));
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
        	 throw new RuntimeException(e); 
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
            	 throw new RuntimeException(e); 
            }
 
        }
    }
	/**
	 * 
	 * @param file
	 * @param parentPath
	 * @param zos
	 */
	private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                    writeZip(f, parentPath, zos);
                }
            }else{
                DataInputStream dis=null;
                try {
                    dis=new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[BUF_SIZE];
                    int len;
                    while((len=dis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }
                     
                 zos.closeEntry(); 
                } catch (FileNotFoundException e) {
                	 throw new RuntimeException(e); 
                } catch (IOException e) {
                	 throw new RuntimeException(e); 
                }finally{
                    try {
                        if(dis!=null){
                            dis.close();
                        }
                    }catch(IOException e){
                    	 throw new RuntimeException(e); 
                    }
                }
            }
        }
    }   
	
	/**
	 * 刪除文件
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static boolean delFile(File file) throws Exception {
		boolean result = false;
        if(file.exists()&&file.isFile()) 
        {
	        file.delete();
	        file.getParentFile().delete();
	        result = true;
        }
        return result;
    }
	/**
     * 删除指定目录和子目录下的所有文件
     * @author Bian Jiang
     * @since 2008.06.03
     * @param filePath
     */
    public static void delAllFile(String filePath) {
        
        try {
            File file = new File(filePath);
            File[] fileList = file.listFiles();
            String dirPath = null;
            if(fileList != null) {
                for(int i = 0 ; i < fileList.length; i++) {
                    if(fileList[i].isFile()) {
                        fileList[i].delete();
                    }
                    if(fileList[i].isDirectory()){  
                        dirPath = fileList[i].getPath();
                        delAllFile(dirPath);
                    }
                }
                file.delete();
            }
        } catch (Exception ex) {
            
        }

    }
    
    
    
    public static void delFolder(String folderPath) {

	     try {
	        delAllFile(folderPath); //删除完里面所有内容
         String filePath = folderPath;
	        filePath = filePath.toString();
	        java.io.File myFilePath = new java.io.File(filePath);
	        if(!myFilePath.getName().equals("download")){
	        	 myFilePath.delete(); //删除空文件夹
	        }
	     } catch (Exception e) {
	       e.printStackTrace(); 
	     }
	}

	//删除指定文件夹下所有文件
	//param path 文件夹完整绝对路径
	   public static boolean delAllFiles(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             delFolder(path + "/" + tempList[i]);//再删除空文件夹
	             flag = true;
	          }
	       }
	       return flag;
	     }
}
