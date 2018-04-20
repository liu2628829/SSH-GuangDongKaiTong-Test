package util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;
/**
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.ISevenZipInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;


 * 数据库连接密码加密工具
 * @author pengjiewen
 * @date Dec 10, 2012
 */
public class MD5ToolSwing {

    private JFrame frame;
    private JPasswordField password;
    private JPasswordField confirm;
//    private JComboBox type;
    private TextAreaMenu resultArea;
    private String rootPath;
    private String sourceFile;
    private Object md5;
    
    public MD5ToolSwing() {
        initialize();
    }
    
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MD5ToolSwing window = new MD5ToolSwing();
                    window.access(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * 初始化界面
     */
    private void initialize() {
        frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                File file = new File(rootPath + "/DES");
                if(file.exists()){
                    deleteDir(file);
                }
            }
        });
        frame.setResizable(false);
        frame.setTitle("\u52A0\u5BC6\u5DE5\u5177");
        frame.setBounds(100, 100, 348, 281);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        
        JLabel lblNewLabel = new JLabel("\u52A0\u5BC6\u660E\u6587\uFF1A");
        lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
        lblNewLabel.setBounds(37, 35, 127, 24);
        frame.getContentPane().add(lblNewLabel);
        
        password = new JPasswordField();
        password.setBounds(116, 35, 192, 24);
        frame.getContentPane().add(password);
        
        confirm = new JPasswordField();
        confirm.setBounds(116, 77, 192, 24);
        frame.getContentPane().add(confirm);
        
        JLabel label = new JLabel("\u786E\u8BA4\u8F93\u5165\uFF1A");
        label.setFont(new Font("SimSun", Font.PLAIN, 16));
        label.setBounds(37, 77, 127, 24);
        frame.getContentPane().add(label);
        
//        JLabel typeLabel = new JLabel("\u52A0\u5BC6\u65B9\u5F0F\uFF1A");
//        typeLabel.setFont(new Font("SimSun", Font.PLAIN, 16));
//        typeLabel.setBounds(37, 103, 127, 24);
//        frame.getContentPane().add(typeLabel);
//        
//        type = new JComboBox();
//        type.setModel(new DefaultComboBoxModel(new String[] {"MD5\u52A0\u5BC6", "\u56FA\u5B9A\u5BC6\u94A5\u52A0\u5BC6"}));
//        type.setBounds(116, 103, 192, 23);
//        frame.getContentPane().add(type);
        
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "\u52A0\u5BC6\u5BC6\u6587", TitledBorder.LEADING, TitledBorder.TOP, null, SystemColor.textHighlight));
        panel.setBounds(10, 148, 322, 95);
        frame.getContentPane().add(panel);
        panel.setLayout(null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 24, 302, 61);
        panel.add(scrollPane);
        
        resultArea = new TextAreaMenu();
        DefaultCaret caret = (DefaultCaret) resultArea.getCaret();  
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);  
        scrollPane.setViewportView(resultArea);
        
        resultArea.setLineWrap(true);
        scrollPane.setViewportView(resultArea);
        
        final JButton btnNewButton = new JButton("\u52A0\u5BC6");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new Runnable() {
                    public void run() {
                        btnNewButton.setEnabled(false);
                        start();
                        btnNewButton.setEnabled(true);
                    }
                }).start();
            }
        });
        btnNewButton.setBounds(80, 119, 67, 23);
        frame.getContentPane().add(btnNewButton);
        
        JButton btnNewButton_1 = new JButton("\u6E05\u7A7A");
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                new Thread(new Runnable() {
                    public void run() {
                        clear();
                    }
                }).start();
            }
        });
        btnNewButton_1.setBounds(204, 119, 67, 23);
        frame.getContentPane().add(btnNewButton_1);
    }

    public void createMain(String pwd){
        if(pwd == null){
            System.exit(0);
        }else{
            try{
                unzipDirWithPassword(sourceFile,rootPath + "/DES",pwd);
//                MD5ToolSwing.class.getClassLoader().loadClass(rootPath + "\\MD5\\");
            }catch(Exception e){
                access(true);
                return;
            }
            try {
                md5 = NeoClassLoader.load("util.CipherKeyImpl");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "加载类util.CipherKeyImpl失败，无法启动！");
                File file = new File(rootPath + "/DES");
                if(file.exists()){
                    deleteDir(file);
                }
                System.exit(0);
            }
            setLocationCenter(frame);
            frame.show(true);
        }
    }
    
    private void access(boolean isWrong){
        if(!isWrong){
            rootPath =  new File(getFullRealPath("")).getParent();
            sourceFile = rootPath + "/DESTool.zip";
            if(!new File(sourceFile).exists()){
                JOptionPane.showMessageDialog(null, "找不到DESTool.zip文件，无法启动！");
                System.exit(0);
            }
        }
        String message = "\u8BF7\u8F93\u5165\u5BC6\u7801\uFF01";
        if(isWrong){
            message = "\u5BC6\u7801\u9519\u8BEF\uFF0C\u8BF7\u91CD\u65B0\u8F93\u5165\uFF01";
        }
        JDialog dialog = new PassWordDialog(message,this);
        setLocationCenter(dialog);
        dialog.show(true);
    } 

    /**
     * 解压加密压缩包方法
     * @param sourceZipFile
     * @param destinationDir
     * @param password
     * @throws Exception 
     */
    public void unzipDirWithPassword( final String sourceZipFile ,
            final String destinationDir , final String password ) throws Exception{
        /**
            RandomAccessFile randomAccessFile = null;
           ISevenZipInArchive inArchive = null;
           try{
              randomAccessFile = new RandomAccessFile(sourceZipFile, "r");
              inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
             
              // Getting simple interface of the archive inArchive
              ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
             
              for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()){
                  final int[] hash = new int[] { 0 };
                  if (!item.isFolder()){
                      ExtractOperationResult result;
                      result = item.extractSlow(new ISequentialOutStream(){
                            public int write(final byte[] data) throws SevenZipException{
                                  try{
                                        if(item.getPath().indexOf(File.separator)>0){
                                            String path = destinationDir+File.separator+item.getPath(). substring(0,item.getPath().lastIndexOf(File.separator));
                                            File folderExisting = new File(path);
                                            if (!folderExisting.exists())
                                                 new File(path).mkdirs();
                                        }
                                        if(!new File(destinationDir + File.separator+item.getPath()).exists()){
                                            File temp = new File(destinationDir + File.separator+item.getPath());
                                            temp.getParentFile().mkdirs();
                                            temp.createNewFile();
                                        }
                                        OutputStream out = new FileOutputStream(destinationDir+ File.separator+item.getPath());
                                        out.write(data);
                                        out.close();
                                   }catch( Exception e ){
                                        e.printStackTrace();
                                   }
                                   hash[0] |= Arrays.hashCode(data);
                                   return data.length; // Return amount of proceed data
                           }
                       },password); /// password.
                       if (result == ExtractOperationResult.OK){
//                           System.out.println(String.format("%9X | %s",
//                                       hash[0], item.getPath()));
                       }else{
//                           System.err.println("Error extracting item: " + result);
                           throw new Exception("extracting error");
                       }
                  }
              }
           } catch (Exception e){
                e.printStackTrace();
                throw e;
           } 
           finally {
                if (inArchive != null){
                    try {
                       inArchive.close();
                    } catch (SevenZipException e){
                       System.err.println("Error closing archive: " + e);
                       e.printStackTrace();
                    }
                }
                if (randomAccessFile != null) {
                     try{
                         randomAccessFile.close();
                     } catch (IOException e){
                         System.err.println("Error closing file: " + e);
                         e.printStackTrace();
                     }
                }
           }
*/
    }
    
    
    /**根据在项目中的相对路径获得其源码目录下的绝对路径*/
    public static String getFullRealPath(String absPath){
        //通过类加载器获得本类绝对路径
        String filePath = null;
        //获取根目录
        String resouPath = "";
        //参数为null时获取当前目录
        if(absPath==null)resouPath = "";
        try {
            filePath = MD5ToolSwing.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+resouPath;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if(absPath!=null){          
            //WEB-INF前的路径为项目所在的绝对路径
            filePath = filePath+"/"+absPath;
        }
        return filePath;
    }
    
    /**
     * 遍历删除目录
     * @param dir
     */
    public void deleteDir(File dir){
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                if(files[i].isDirectory()){
                    deleteDir(files[i]);
                }else{
                    files[i].delete();                  
                }
            }
            dir.delete();
        }else{
            dir.delete();
        }
    }

    /**
     * 开始加密
     */
    private void start(){
        String pw = String.valueOf(password.getPassword());
        String cf = String.valueOf(confirm.getPassword());
        String result = "";
        if("".equals(pw)||"".equals(cf)){
            JOptionPane.showMessageDialog(frame, "请输入明文并确认输入！");
            return;
        }
        if(!pw.equals(cf)){
            JOptionPane.showMessageDialog(frame, "两次输入不一致！");
            return;
        }
//        if(type.getSelectedIndex()==0){
//            result=MD5Tool.MD5Encrypt(pw); 
//            resultArea.setText(result);
//        }else if(type.getSelectedIndex()==1){
        try {
			//Object res = md5.getClass().getMethod("HexEncode", new Class[]{String.class}).invoke(md5, new Object[]{pw});
            //result = (String)res;
            
			Object res = md5.getClass().getMethod("getCipherKey").invoke(md5);
            result = DESTool.encrypt((byte[])res, pw);
        } catch (Exception e) {
            e.printStackTrace();
        } 
//            result = MD5Tool.HexEncode(pw);
            resultArea.setText(result);            
//        }
    }
    /**
     * 清空所有内容
     */
    private void clear(){
        password.setText("");
        confirm.setText("");
        resultArea.setText("");
    }
    
    public static void setLocationCenter(Component component) {  
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  
            Dimension compSize = component.getSize();  
            if (compSize.height > screenSize.height) {  
                compSize.height = screenSize.height;  
            }  
            if (compSize.width > screenSize.width) {  
                compSize.width = screenSize.width;  
            }  
            component.setLocation((screenSize.width - compSize.width) / 2,  
                    (screenSize.height - compSize.height) / 2);  
        }  
}

/**
 * class动态加载类
 * @author pengjiewen
 * @version Jan 30, 2013
 */
class NeoClassLoader extends URLClassLoader {
    public NeoClassLoader(URL[] urls, URLClassLoader parent) { 
        super(urls, parent);   
        }      
    protected Class<?> findClass(String name) throws ClassNotFoundException {  
        Class<?> clazz = null;            
        try{        
            clazz = super.findClass(name);      
            }catch(ClassNotFoundException ex){
                if( clazz == null ) 
                    clazz = _findClass(name);   
                if( clazz == null ) 
                    throw ex;     
                }      
            return clazz;   
            }      
    private Class<?> _findClass(String name) throws ClassNotFoundException {     
        Class<?> clazz = null;     
        String filePath = new File(MD5ToolSwing.getFullRealPath("")).getParent() 
        + File.separator + "DES" + File.separator + name.replace(".", File.separator) + ".class";        
        try{            
            FileInputStream fileInputStream = new FileInputStream(new File(filePath));          
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();           
            int size, SIZE = 4096;          
            byte[] buffer = new byte[SIZE];             
            while( (size = fileInputStream.read(buffer)) > 0 ){             
                outputStream.write(buffer, 0, size);            }           
            fileInputStream.close();            
            byte[] classBytes = outputStream.toByteArray();             
            outputStream.close();                       
            clazz = defineClass(name, classBytes, 0, classBytes.length);        
            }catch(Exception ex){           
                throw new ClassNotFoundException(name);     
                }       
            return clazz;     
            }        
    
    public static Object load(String className) throws Exception {        
        URLClassLoader classLoader = (URLClassLoader)Thread.currentThread().getContextClassLoader();               
        NeoClassLoader myClassLoader = new NeoClassLoader( classLoader.getURLs(), classLoader );             
        Class<?> c1 = myClassLoader.loadClass(className);       
        return c1.newInstance();             
        }       
    }
/**
 * 右键菜单
 * @author pengjiewen
 * @date Dec 10, 2012
 */
class TextAreaMenu extends JTextArea implements MouseListener {

    private JPopupMenu pop = null; // 弹出菜单

    private JMenuItem copy = null; // 三个功能菜单

    public TextAreaMenu() {
        super();
        init();
    }

    private void init() {
        this.addMouseListener(this);
        pop = new JPopupMenu();
        pop.add(copy = new JMenuItem("复制"));
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                action(e);
            }
        });
        this.add(pop);
    }

    /**
     * 菜单动作
     * @param e
     */
    public void action(ActionEvent e) {
        String str = e.getActionCommand();
        if (str.equals(copy.getText())) { // 复制
            this.copy();
        } 
    }

    public JPopupMenu getPop() {
        return pop;
    }

    public void setPop(JPopupMenu pop) {
        this.pop = pop;
    }

    /**
     * 文本组件中是否具备复制的条件
     *
     * @return true为具备
     */

    public boolean isCanCopy() {
        boolean b = false;
        int start = this.getSelectionStart();
        int end = this.getSelectionEnd();
        if (start != end)
            b = true;
        return b;
    }

    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }
    
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON3){
            copy.setEnabled(isCanCopy());
            pop.show(this, e.getX(), e.getY());
            }
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }
  }


class PassWordDialog extends JDialog{

    private final JPanel contentPanel = new JPanel();
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            PassWordDialog dialog = new PassWordDialog("",null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public PassWordDialog(String message,final MD5ToolSwing md) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        setTitle("\u52A0\u5BC6\u5DE5\u5177");
        setResizable(false);
        setBounds(100, 100, 280, 130);
        getContentPane().setLayout(null);
        contentPanel.setBounds(0, 0, 274, 60);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel);
        contentPanel.setLayout(null);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(10, 36, 254, 24);
        contentPanel.add(passwordField);
        
        JLabel lblNewLabel = new JLabel(message);
        lblNewLabel.setBounds(10, 10, 218, 15);
        contentPanel.add(lblNewLabel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBounds(0, 61, 280, 37);
            getContentPane().add(buttonPane);
            {
                JButton okButton = new JButton("\u786E\u5B9A");
                okButton.setBounds(66, 5, 60, 25);
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        new Thread(new Runnable(){
                            public void run(){
                                md.createMain(getPasswd());
                                closeDialog();
                            }
                        }).start();
                    }
                });
                buttonPane.setLayout(null);
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("\u5173\u95ED");
                cancelButton.setBounds(145, 5, 60, 25);
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        md.createMain(null);
                        closeDialog();
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }
    
    public String getPasswd(){
        return String.valueOf(passwordField.getPassword());
    }
    
    public void closeDialog(){
        this.dispose();
    }

}
