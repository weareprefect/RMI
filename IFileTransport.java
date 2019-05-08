import java.rmi.*;
import java.util.ArrayList;

public interface IFileTransport extends Remote
{
	int getFileLength(String fileName) throws Exception;//�ļ�����	
	int judge(String fileName) throws Exception;//�жϵ����Ǹ��������ز���
	ArrayList<String> makeDir(String fileName) throws Exception;//���ز��ַ��ش����ļ���
	byte[] getFile(String fileName,int n) throws Exception;//����
	void upFile(String f,byte[] b,int len) throws Exception;
	void upset() throws Exception;
	void upFile1(String filename) throws Exception;//�ļ���
}