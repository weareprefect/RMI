import java.rmi.*;
import java.util.ArrayList;

public interface IFileTransport extends Remote
{
	int getFileLength(String fileName) throws Exception;//文件长度	
	int judge(String fileName) throws Exception;//判断调用那个函数下载部分
	ArrayList<String> makeDir(String fileName) throws Exception;//下载部分返回创建文件夹
	byte[] getFile(String fileName,int n) throws Exception;//下载
	void upFile(String f,byte[] b,int len) throws Exception;
	void upset() throws Exception;
	void upFile1(String filename) throws Exception;//文件夹
}