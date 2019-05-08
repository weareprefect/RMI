import java.rmi.*;
import java.util.ArrayList;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

//�ļ�����ͻ���
public class FileTransportClient extends JFrame
{	
	private static final long serialVersionUID = 1L;
	private String hostIP;	//������IP
	private int hostPort;	//�������˿�
	private IFileTransport ft; //�ļ�����Զ�̽ӿ�
	private int z=0;   //�ж��Ƿ������
	private int length=0;//�ļ���С
    private int start;
		
	//���ӽ���
	private JLabel server;
	private JLabel portLabel;	
	private JTextField serverText;
	private JTextField portText;	
	private JButton connectButton;
	private JButton upButton;
	private JProgressBar progressBar;
	private JLabel progressLabel;
	private JLabel download;
	private JTextField download1;//ԭ���ڷ�������·��
	private JButton setdownButton;//ѡ���ŵ�·��
	private JLabel download2;
	private JTextField download3;//��ŵ�·��
	private JButton downloadButton; //��ʼ����
	private JLabel upload; 
	private JTextField upload1;//�ϴ�������·��
	private JLabel upload2;
	private JTextField upload3;//ԭ·��
	private JButton uploadButton;//��ʼ�ϴ�
	
	//�ļ������߳�
	class TransportThread extends Thread
	{
		private String fileName;
		private String downloadPath;
		public TransportThread(String filePath,String down)
		{
			this.fileName = filePath;
			this.downloadPath=down;
		}
		
		public void run()
		{
			transport(this.fileName,this.downloadPath);
		}
	}
	//�ļ��ϴ��߳�
	class TransportUP extends Thread
	{
		private String fileName;
		private String f1;
		public TransportUP(String filePath,String f)
		{
			this.fileName = filePath;
			this.f1=f;
		}
		
		public void run()
		{
			updata(this.fileName,this.f1);
		}
	}
	
	//��ť�¼�����
	class ButtonActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			JButton b = (JButton) e.getSource();
			if(b == connectButton)
			{
				connect();
			}
			else if(b == setdownButton)
			{
				setup();
			}
			else if(b==upButton) {
				choose();
			}
			else if(b==uploadButton) {
				length=start=z=0;
				TransportUP t=new TransportUP(upload3.getText(),upload1.getText());
				t.start();
			}
			else if(b==downloadButton) {
				length=start=z=0;
				TransportThread t=new TransportThread(download1.getText(),download3.getText());
				t.start();
			}
		}
	}

	//���캯���������ʼ��
	public FileTransportClient()
	{	
		this.setLayout(null);
		this.setSize(700, 700);
		this.setTitle("RMI����ͻ���");
		Container cp = this.getContentPane();
		
		this.server = new JLabel("������IP");
		this.server.setBounds(20, 20, 100, 30);
		cp.add(this.server);
		
		this.serverText  = new JTextField(8);
		this.serverText.setBounds(130, 20, 150, 30);
		cp.add(this.serverText);
		
		this.portLabel = new JLabel("�˿ں� ");
		this.portLabel.setBounds(300, 20, 50, 30);
		cp.add(this.portLabel);
		
		this.portText = new JTextField(4);
		this.portText.setBounds(400, 20, 90, 30);	
		cp.add(this.portText);
		
		this.download = new JLabel("ԭ·�� ");
		this.download.setBounds(20, 60, 100, 30);
		cp.add(this.download);
		
		this.download1=new JTextField(50);
		this.download1.setBounds(130, 60, 150, 30);	
		cp.add(this.download1);
		
		this.download2 = new JLabel("Ŀ��·�� ");
		this.download2.setBounds(20, 100, 100, 30);
		cp.add(this.download2);
		
		this.download3=new JTextField(50);
		this.download3.setBounds(130, 100, 150, 30);	
		cp.add(this.download3);
		
		this.setdownButton = new JButton("ѡ��Ŀ��·��");	
		this.setdownButton.setBounds(290, 100, 120, 30);
		cp.add(this.setdownButton);
		
		this.downloadButton=new JButton("��ʼ����");	
		this.downloadButton.setBounds(130, 140, 120, 30);
		cp.add(this.downloadButton);
		
		this.upload = new JLabel("Ŀ��·�� ");
		this.upload.setBounds(20, 180, 100, 30);
		cp.add(this.upload);
		
		this.upload1=new JTextField(50);
		this.upload1.setBounds(130, 180, 150, 30);	
		cp.add(this.upload1);
		
		this.upload2 = new JLabel("ԭ·�� ");
		this.upload2.setBounds(20, 220, 100, 30);
		cp.add(this.upload2);
		
		this.upload3=new JTextField(50);
		this.upload3.setBounds(130, 220, 150, 30);	
		cp.add(this.upload3);
		
		this.upButton = new JButton("ѡ��ԭ·��");	
		this.upButton.setBounds(290, 220, 120, 30);
		cp.add(this.upButton);
		
		this.uploadButton=new JButton("��ʼ�ϴ�");	
		this.uploadButton.setBounds(130, 260, 120, 30);
		cp.add(this.uploadButton);
	
		this.connectButton = new JButton("����");
		this.connectButton.setBounds(130, 300, 120, 30);
		cp.add(this.connectButton);
		
		ButtonActionListener bal = new ButtonActionListener();
		this.connectButton.addActionListener(bal);
		this.upButton.addActionListener(bal);
		this.setdownButton.addActionListener(bal);
		this.uploadButton.addActionListener(bal);
		this.downloadButton.addActionListener(bal);
	
		this.progressBar = new JProgressBar();	
		this.progressBar.setBounds(20, 340, 400, 30);
		this.progressBar.setVisible(false);
		cp.add(this.progressBar);	
		
		this.progressLabel = new JLabel();
		this.progressLabel.setBounds(20, 380, 200, 30);
		cp.add(this.progressLabel);
		
		try
		{   //Ĭ�ϻ�ȡ��ǰ��������Ĭ�϶˿ں�
			this.serverText.setText(InetAddress.getLocalHost().getHostAddress());
			this.portText.setText( ((Integer)8080).toString() );
		}
		catch(java.net.UnknownHostException uhe)
		{
			uhe.printStackTrace();
		}
	}
	
	//���ӷ�����
	public void connect()
	{
		try
		{
			this.progressBar.setVisible(false);
			this.hostIP = this.serverText.getText().trim();
			this.hostPort = Integer.parseInt(this.portText.getText().trim());			
			this.ft = (IFileTransport) Naming.lookup(("//" + this.hostIP + ":" + ((Integer)this.hostPort).toString()+"/FileTransport"));
			JOptionPane.showMessageDialog(this, "���������ӳɹ�", "�ɹ�", JOptionPane.OK_CANCEL_OPTION);
		}
		catch(Exception e)
		{
			JOptionPane.showMessageDialog(this, "���������Ӵ���", "����", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	//��������·��
	public void setup()
	{
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			this.download3.setText(fc.getSelectedFile().getAbsolutePath());
		}
	}
	//�����ϴ���ԭ·��
	public void choose() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			String f = fc.getSelectedFile().getAbsolutePath();
			this.upload3.setText(f);
		}
	}
	public void updata(String uploadf,String n) {
		try {
		File f=new File(uploadf);
		byte[] buffer=new byte[2*1024*1024];
		if(length==0) {
			length=getlength(f);
			}
			if(start==0) {
			 start = 0;
			}
			if(z==0) {
			z = length;
			}
		this.progressBar.setMaximum(length);
		this.progressBar.setVisible(true);
		while(z>0)
		{
			if(f.isDirectory()){
				File[] arr = f.listFiles();
				for (int i = 0; i < arr.length; i++) {
					this.ft.upFile1(n);
					updata(arr[i].toString(),n+"\\"+arr[i].getName());
				}
				return;
			}
			else{
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			int len=0;
			while((len=in.read(buffer))!=-1) {
			 this.ft.upFile(n,buffer,len);
			 z-= len;
			 start += len;
			 this.progressBar.setValue(start);
			 this.progressBar.setIndeterminate(false);
			 int percent = (int)(100 * ((double) start / (double)length));
			 this.progressLabel.setText("���ϴ� " + percent + "%");
			}
			in.close();
			this.ft.upset();
			return;
			}
		}
	}catch(Exception e)
	{
		JOptionPane.showMessageDialog(this, e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
	}
	}	
	public int getlength(File f) {
		if (f.isFile()) {
            return (int)f.length();
	   }
		 int m=0;
		 if(f!=null) {
             File[] arr = f.listFiles();
              for (int i=0;i<arr.length;i++) {
            	  if(arr[i]!=null)
               m+= getlength(arr[i]);
              }
            }
		 return m;
	 }
	//�����ļ�
	public void transport(String fileName,String downloadPath)
	{
		try
		{
			if(length==0) {
			length = this.ft.getFileLength(fileName);
			}
			if(z==0) {
			z = length;
			}
			byte[] buff;
			this.progressBar.setMaximum(length);
			this.progressBar.setVisible(true);
			while(z>0){
				if(this.ft.judge(fileName)==1) {
					File f2=new File(downloadPath);
					if(!f2.exists()) {
						f2.mkdirs();
					}
					ArrayList<String> m=this.ft.makeDir(fileName);
					
					for(int n=0;n<m.size();n++) {
						transport(fileName+"\\"+m.get(n).toString(),downloadPath+"\\"+m.get(n).toString()); 
						}
					return;
					}
				if(this.ft.judge(fileName)==0) {
					File f2=new File(downloadPath);
					if(f2.isDirectory()) {
						File f3=new File(fileName);
						f2=new File(downloadPath+"\\"+f3.getName());
					}
					if(!f2.exists()) {
						f2.createNewFile();
					}
				    DataOutputStream out = new DataOutputStream(new FileOutputStream(f2));
				    int m=this.ft.getFileLength(fileName);
				    int n=0;
				    while(m>0) {
				     buff = this.ft.getFile(fileName,n);				
				     out.write(buff);
				     z-= buff.length; 
				     n+=buff.length;
				     m-= buff.length;
				     start += buff.length;
				     this.progressBar.setValue(start);
				     this.progressBar.setIndeterminate(false);
				     int percent = (int)(100 * ((double) start / (double)length));
				     this.progressLabel.setText("������ " + percent + "%");
				    }
				   out.close();
				   return;
				 }
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, e.getMessage(), "����", JOptionPane.ERROR_MESSAGE);
		}
	}
	public static void main(String[] args)
	{
		FileTransportClient fn= new FileTransportClient();
		fn.setVisible(true);
	}
}
