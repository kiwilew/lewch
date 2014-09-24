package org.lewch.util.zip;

import java.io.*;
import java.util.*;

/**
 * 文件ZIP压缩解压工具类（支持中文文件名称）
 * 
 * 依赖ANT jar包，如ant-1.7.0.jar
 * 
 * @author Lewch
 * 
 */
public class FileZipUtility {

	private final static String ZIP_ENCODER = "UTF-8";

	/**
	 * 压缩文件
	 * 
	 * @param srcfile
	 *            File[] 压缩源文件列表
	 * @param zipfile
	 *            File 压缩目标ZIP文件
	 */
	private static void ZipFiles(java.io.File[] srcfile, java.io.File zipfile) {
		byte[] buf = new byte[1024];
		try {
			// 创建压缩目标ZIP文件
			org.apache.tools.zip.ZipOutputStream out = new org.apache.tools.zip.ZipOutputStream(
					new FileOutputStream(zipfile));
			out.setEncoding(ZIP_ENCODER);
			// 压缩
			for (int i = 0; i < srcfile.length; i++) {
				FileInputStream in = new FileInputStream(srcfile[i]);
				// 添加ZIP Entry到输出流
				out.putNextEntry(new org.apache.tools.zip.ZipEntry(srcfile[i]
						.getName()));
				// 输出
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				// 关闭流
				out.closeEntry();
				in.close();
			}
			// 完成压缩
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void ZipFiles(String[] srcfilePaths, String zipfilePath) {
		List<java.io.File> srcfileList = new ArrayList<java.io.File>();
		java.io.File zipfile = new java.io.File(zipfilePath);
		for (String filePath : srcfilePaths) {
			java.io.File f = new java.io.File(filePath);
			if (f.exists()) {
				srcfileList.add(f);
			}
		}

		java.io.File[] srcfiles = new java.io.File[srcfileList.size()];
		for (int i = 0; i < srcfileList.size(); i++) {
			srcfiles[i] = srcfileList.get(i);
		}
		ZipFiles(srcfiles, zipfile);
	}

	/**
	 * 解压缩
	 * 
	 * @param zipfile
	 *            File 需要解压缩的文件
	 * @param descDir
	 *            String 解压后的目标目录
	 */
	public static void UnZipFiles(java.io.File zipfile, String descDir) {
		try {
			// 获取需解压文件
			org.apache.tools.zip.ZipFile zf = new org.apache.tools.zip.ZipFile(
					zipfile, ZIP_ENCODER);

			for (Enumeration entries = zf.getEntries(); entries
					.hasMoreElements();) {
				// 获取解压文件中的Zip Entry
				org.apache.tools.zip.ZipEntry entry = ((org.apache.tools.zip.ZipEntry) entries
						.nextElement());
				String zipEntryName = entry.getName();
				InputStream in = zf.getInputStream(entry);
				// System.out.println(zipEntryName);
				File descDirFile = new File(descDir);
				if (!descDirFile.exists()) {
					descDirFile.mkdirs();
				}
				// 输出文件
				OutputStream out = new FileOutputStream(descDir
						+ File.separator + zipEntryName);
				byte[] buf1 = new byte[1024];
				int len;
				while ((len = in.read(buf1)) > 0) {
					out.write(buf1, 0, len);
				}
				// 关闭流
				in.close();
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}