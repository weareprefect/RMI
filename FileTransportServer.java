import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.rmi.server.*;
import java.util.ArrayList;
import java.rmi.registry.*;
import java.net.*;
import java.io.*;

//文件传输服务器端
public class FileTransportServer extends JFrame{
	private static final long serialVersionUID = 1L;
    class FileTransportImpl extends UnicastRemoteObject implements IFileTransport
	{
		private static final long serialVersionUID = 1L;
		private ArrayList<String> list = new ArrayList<String>();
		private DataOutputStream ou=null;
		public FileTransportImpl() throws Exception{
			
		}
		//服务器端文件长度
		public int getFileLength(String fileName) throws Exception{
			 File f=new File(fileName);
			 if (f.isFile()) {
		            return (int)f.length();
			 }
			 int m=0;
			 if(f!=null) {
		           File[] arr = f.listFiles();
		           if(arr!=null) {
		            for (int i=0;i<arr.length;i++) {
		             if(arr[i]!=null)
		            m+= getFileLength(fileName+"\\"+arr[i].getName());
		            }
		           }
		       }
			 return m;
		}
		//下载读入
		public byte[] getFile(String fileName,int n) throws Exception{
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			File f1=new File(fileName);
			DataInputStream in = new DataInputStream(new FileInputStream(f1));
			in.skipBytes(n);
			byte[] buffer = new byte[2*1024*1024];
			int len;
			if((len=in.read(buffer))!=-1 ) 
			{	
				byteArray.write(buffer,0,len);
			}	
			in.close();
			return byteArray.toByteArray();
		}
		//判断文件夹
		public int judge(String fileName) throws Exception {
	     File f=new File(fileName);
	     if(f.isDirectory()) {
	    	 return 1;
	     }
	     if(!f.exists()) {
	    	 return 2;
	     }
	     return 0;
		}
		//目录
		public ArrayList<String> makeDir(String fileName) throws Exception {
			list.clear();
			File f=new File(fileName);
			if (f.exists() && f.isDirectory() &&f.listFiles().length > 0) {
		          File[] arr = f.listFiles();
			      for (File file : arr) {
			    	list.add(file.getName());
			      }
			}
			return list;
		}
		//创建文件夹
		public void upFile1(String filename) throws Exception{
			File f=new File(filename);
			if(!f.exists()) {
				f.mkdirs();
			}
		}
		//关闭流
		public void upset() throws Exception {
			ou.close();
			ou=null;
		}
		//写入上传
		public void upFile(String filename,byte[] b,int len) throws Exception{
			File f=new File(filename);
			if(!f.exists()) {
				f.createNewFile();
			}
			if(ou==null) {
			ou = new DataOutputStream(new FileOutputStream(f));
			}
			ou.write(b,0,len);
		}
	}
	class ButtonListener implements ActionListener//按钮处理内部类调用本类的函数
	{
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				JButton b = (JButton) e.getSource();
                if(b == changeButton)
				{
					changePort();
				}
			}
			catch(Exception ee)
			{
				JOptionPane.showMessageDialog(null, ee.getMessage());
			}
		}
	}
	
	//服务器窗体处理
	class ServerWindowListener extends WindowAdapter
	{
		public void windowClosing(WindowEvent e)
		{
			unbind();
		}
	}
	
	//窗体控件及其他成员变量
	private JLabel serverInfoLabel;
	private JLabel portLabel;
	private JButton changeButton;
	private int port;
	
	//构造函数，初始化对象
	public FileTransportServer() throws Exception
	{
		this.port = 8080;	//赋初值
		
		ButtonListener bal = new ButtonListener();
		
		//初始化控件
		this.setSize(700, 200);
		this.setLayout(null);
		this.setTitle("RMI文件上传下载服务端");
		
		Container cp = this.getContentPane();
		
		this.serverInfoLabel = new JLabel("服务器IP：" + InetAddress.getLocalHost().getHostAddress());
		this.serverInfoLabel.setBounds(10, 10, 200, 30);
		cp.add(this.serverInfoLabel);
		
		
		this.portLabel = new JLabel("端口号：" + this.port);
		this.portLabel.setBounds(220, 10, 120, 30);
		cp.add(this.portLabel);
		
		this.changeButton = new JButton("修改端口号");
		this.changeButton.setBounds(350, 10, 100, 30);
		this.changeButton.addActionListener(bal);
		cp.add(this.changeButton);		
		
		this.addWindowListener(new ServerWindowListener());
	}

	public void register() throws Exception
	{
		FileTransportImpl ft = new FileTransportImpl();
		Registry reg;
		try
		{
			reg = LocateRegistry.getRegistry(this.port);
			reg.rebind("FileTransport", ft);
		}
		catch(RemoteException re)
		{
			reg = LocateRegistry.createRegistry(this.port);
			reg.bind("FileTransport", ft);
		}
		this.portLabel.setText("端口号：" + this.port);
	}
	
	//修改端口
	private void changePort() throws Exception
	{
		Object o = JOptionPane.showInputDialog(this, "输入端口号", "修改端口号", JOptionPane.YES_NO_OPTION, null, null, this.port);
		if(o == null)
		{
			return;
		}
		
		try
		{
			int v = Integer.parseInt(o.toString());
			if(v >= 1024 && v <= 65535)
			{
				this.unbind();
				this.port = v;				
				this.register();
			}
			else
			{
				JOptionPane.showMessageDialog(this, "此端口号不合适");
			}
		}
		catch(ClassCastException cce)
		{
			cce.printStackTrace();
		}
	}
	
	//撤销绑定
	private void unbind()
	{
		try
		{
			Registry reg = LocateRegistry.getRegistry(this.port);
			reg.unbind("FileTransport");
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			FileTransportServer ftServer = new FileTransportServer();
			ftServer.register();
			ftServer.setVisible(true);
		}
		catch(Exception ee)
		{
			JOptionPane.showMessageDialog(null, ee.getMessage());
		}
	}
}
