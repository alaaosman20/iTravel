package com.xmliu.itravel.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.FileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.xmliu.itravel.Constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {

	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
	private static final String TAG = "FileUtils";
	private static FileNameGenerator fileNameGenerator = null;

	public static void initDir(){
		createDir(new File(Constants.Path.ImageCameraDir));
		createDir(new File(Constants.Path.ImageDownloadDir));
		createDir(new File(Constants.Path.ImageCacheDir));
		createDir(new File(Constants.Path.ImageCompressDir));
		createDir(new File(Constants.Path.ImageCrop));
		createDir(new File(Constants.Path.DBPath));
	}

	public static long getFileSizes(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}

		}
		return size;
	}
	/**
	 * 获取指定文件大小
	 *
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("resource")
	public static long getFileSize(File file) {
		long size = 0;
		FileInputStream fis = null;
		try {
			if (file.exists()) {

				fis = new FileInputStream(file);
				size = fis.available();
			} else {
				file.createNewFile();
				Log.e("获取文件大小", "文件不存在!");
			}
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return size;
	}
	public static void init() throws IOException {
		fileNameGenerator = new Md5FileNameGenerator();
	}

	public static String generatorFileName(String fileName) {
		return fileNameGenerator.generate(fileName);
	}

	/**
	 * 拷贝文件
	 * 
	 * @param fromFile
	 * @param toFile
	 * @throws IOException
	 */
	public static void copyFile(File fromFile, String toFile) throws IOException {

		FileInputStream from = null;
		FileOutputStream to = null;
		try {
			from = new FileInputStream(fromFile);
			to = new FileOutputStream(toFile);
			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = from.read(buffer)) != -1)
				to.write(buffer, 0, bytesRead); // write
		} finally {
			if (from != null)
				try {
					from.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
			if (to != null)
				try {
					to.close();
				} catch (IOException e) {
					Log.e(TAG, "", e);
				}
		}
	}

	/**
	 * 创建文件
	 *
	 * @param file
	 * @return
	 */
	public static File createNewFile(File file) {

		try {
			if (file.exists()) {
				return file;
			}

			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			Log.e(TAG, "", e);
			return null;
		}
		return file;
	}

	/**
	 * 创建文件
	 *
	 * @param path
	 */
	public static File createNewFile(String path) {
		File file = new File(path);
		return createNewFile(file);
	}

	/**
	 * 删除文件
	 *
	 * @param path
	 */
	public static void deleteFile(String path) {
		File file = new File(path);
		deleteFile(file);
	}

	/**
	 * 递归删除文件和文件夹
	 *
	 * @param file
	 *            要删除的根目录
	 */
	public void DeleteFile(File file) {
		if (file.exists() == false) {
			return;
		} else {
			if (file.isFile()) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					DeleteFile(f);
				}
				file.delete();
			}
		}
	}

	/**
	 * 删除文件
	 *
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (!file.exists()) {
			return;
		}
		if (file.isFile()) {
			file.delete();
		} else if (file.isDirectory()) {
			File files[] = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteFile(files[i]);
			}
		}
		file.delete();
	}

	/**
	 * 获得文件名
	 *
	 * @param path
	 * @return
	 */
	public static String getFileName(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		File f = new File(path);
		String name = f.getName();
		f = null;
		return name;
	}

	/**
	 * 创建文件夹
	 *
	 * @param dir
	 * @return
	 */
	public static boolean createDir(File dir) {
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}
			return true;
		} catch (Exception e) {
			Log.e(TAG, "create dir error", e);
			return false;
		}
	}

	/**
	 * 判断SD卡上的文件是否存在
	 */
	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 判断文件是否小于指定大小，单位为KB
	 *
	 * @param file
	 * @param limit
	 * @return
	 */
	public static boolean isFileLowThan(File file, int limit) {
		int fileSize = 0;
		boolean success = false;
		try {
			FileInputStream fis = new FileInputStream(file);
			fileSize = fis.available();
			fis.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fileSize < 1024) {
			success = true;
		} else if (fileSize < 1048576) {
			if ((fileSize / 1024) <= limit) {
				success = true;
			}
		}
		return success;
	}

	/**
	 * 保存bitmap至本地
	 *
	 * @param bmp
	 */
	public static void saveBitmap(String name, Bitmap bmp) {
		try {
			File root = new File(Constants.Path.ImageDownloadDir);
			if (!root.exists()) {
				root.mkdirs();
			}
			File f = new File(Constants.Path.ImageDownloadDir + name);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 压缩文件
	 *
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFile(File resFile, ZipOutputStream zipout, String rootpath) throws FileNotFoundException,
			IOException {
		rootpath = rootpath + (rootpath.trim().length() == 0 ? "" : File.separator) + resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");

		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	/**
	 * 复制asset目录下的文件到内置sd卡缓存下
	 * @param filename
	 * @param mContext
	 */
	public static void copyFile(String filename,Context mContext) {
		AssetManager assetManager = mContext.getAssets();

		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String newFileName = mContext.getCacheDir()+ filename;
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", e.getMessage());
		}

	}

	//判断SD卡是否存在
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

}
